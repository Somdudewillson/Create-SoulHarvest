package somdudewillson.create_soul_harvest.content.conduit.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.conduit.WindowedPotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.conduit.WindowedPotentiaConduitBlockEntity;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaTransporter;
import somdudewillson.create_soul_harvest.content.potentia.PotentiaHelper;
import somdudewillson.create_soul_harvest.content.potentia.SoulPotentiaStack;

import java.util.*;

public class PotentiaNetwork {

	public static final int MAX_NETWORK_SIZE = 512;
	private static final Map<BlockPos, PotentiaNetwork> CACHE = new HashMap<>();

	private final Set<BlockPos> conduitPositions = new HashSet<>();
	private final List<Endpoint> endpoints = new ArrayList<>();
	private final Map<BlockPos, List<BlockPos>> adjacency = new HashMap<>();
	private final Map<BlockPos, Map<BlockPos, Integer>> distanceCache = new HashMap<>();

	public record Endpoint(BlockPos pos, Direction sideOnHandler) {}

	public static void invalidate(BlockPos pos) {
		PotentiaNetwork net = CACHE.remove(pos);
		if (net != null) {
			for (BlockPos p : net.conduitPositions) {
				CACHE.remove(p);
			}
		}
		for (Direction d : Direction.values()) {
			PotentiaNetwork neighborNet = CACHE.remove(pos.relative(d));
			if (neighborNet != null) {
				for (BlockPos p : neighborNet.conduitPositions) {
					CACHE.remove(p);
				}
			}
		}
	}

	public static void clearCache() {
		CACHE.clear();
	}

	public static PotentiaNetwork getOrCreate(Level level, BlockPos startPipe) {
		PotentiaNetwork existing = CACHE.get(startPipe);
		if (existing != null) {
			return existing;
		}
		PotentiaNetwork newNetwork = build(level, startPipe);
		for (BlockPos pos : newNetwork.conduitPositions) {
			CACHE.put(pos, newNetwork);
		}
		return newNetwork;
	}

	public static PotentiaNetwork build(Level level, BlockPos startPipe) {
		PotentiaNetwork network = new PotentiaNetwork();
		Queue<BlockPos> frontier = new ArrayDeque<>();
		frontier.add(startPipe);
		network.conduitPositions.add(startPipe);

		while (!frontier.isEmpty() && network.conduitPositions.size() < MAX_NETWORK_SIZE) {
			BlockPos current = frontier.poll();
			BlockState currentState = level.getBlockState(current);
			if (!(currentState.getBlock() instanceof IPotentiaTransporter conduit)) {
				continue;
			}

			for (Direction d : Direction.values()) {
				if (!conduit.canConnect(currentState, d)) {
					continue;
				}

				BlockPos neighborPos = current.relative(d);
				if (!level.isLoaded(neighborPos)) {
					continue;
				}

				BlockState neighborState = level.getBlockState(neighborPos);
				if (neighborState.getBlock() instanceof IPotentiaTransporter neighborTransporter) {
					if (neighborTransporter.canConnect(neighborState, d.getOpposite())) {
						network.adjacency.computeIfAbsent(current, k -> new ArrayList<>()).add(neighborPos);
						network.adjacency.computeIfAbsent(neighborPos, k -> new ArrayList<>()).add(current);
						if (network.conduitPositions.add(neighborPos)) {
							frontier.add(neighborPos);
						}
					}
				} else {
					Direction sideOnNeighbor = d.getOpposite();
					IPotentiaHandler handler = PotentiaHelper.getHandler(level, neighborPos, sideOnNeighbor);
					if (handler != null) {
						network.endpoints.add(new Endpoint(neighborPos, sideOnNeighbor));
					}
				}
			}
		}

		return network;
	}

	public static long distributeStack(Level level, BlockPos supplierPos, Direction pushSide, SoulPotentiaStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0L;
		}
		BlockPos startPipe = supplierPos.relative(pushSide);
		if (!(level.getBlockState(startPipe).getBlock() instanceof IPotentiaTransporter)) {
			return 0L;
		}

		PotentiaNetwork network = getOrCreate(level, startPipe);
		return network.distribute(level, supplierPos, stack);
	}

	public static void distributeFrom(Level level, BlockPos supplierPos, Direction pushSide, IPotentiaHandler supplier) {
		SoulPotentiaStack sample = supplier.drain(1000L, IPotentiaHandler.PotentiaAction.SIMULATE);
		if (sample.isEmpty()) {
			return;
		}

		long accepted = distributeStack(level, supplierPos, pushSide, sample);
		if (accepted > 0) {
			supplier.drain(accepted, IPotentiaHandler.PotentiaAction.EXECUTE);
		}
	}

	public long distribute(Level level, BlockPos supplierPos, SoulPotentiaStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0L;
		}

		List<ConsumerTarget> validConsumers = new ArrayList<>();
		for (Endpoint ep : endpoints) {
			if (ep.pos().equals(supplierPos)) {
				continue;
			}
			if (!level.isLoaded(ep.pos())) {
				continue;
			}

			IPotentiaHandler handler = PotentiaHelper.getHandler(level, ep.pos(), ep.sideOnHandler());
			if (handler == null || !handler.canAccept(ep.sideOnHandler())) {
				continue;
			}

			// Type and collision checking
			if (!handler.isPotentiaValid(0, stack)) {
				continue;
			}

			long simulatedAccept = handler.fill(stack.copy(), IPotentiaHandler.PotentiaAction.SIMULATE);
			if (simulatedAccept > 0) {
				validConsumers.add(new ConsumerTarget(handler, simulatedAccept));
			}
		}

		if (validConsumers.isEmpty()) {
			return 0L;
		}

		long toDistribute = stack.getAmount();
		long remaining = toDistribute;
		long totalAccepted = 0L;

		for (int i = 0; i < validConsumers.size(); i++) {
			ConsumerTarget target = validConsumers.get(i);
			long share = Math.min(target.maxAccepted(), Math.max(1L, remaining / (validConsumers.size() - i)));
			SoulPotentiaStack offer = stack.copy();
			offer.setAmount(share);

			long accepted = target.handler().fill(offer, IPotentiaHandler.PotentiaAction.EXECUTE);
			if (accepted > 0) {
				totalAccepted += accepted;
				remaining -= accepted;
			}
			if (remaining <= 0) {
				break;
			}
		}

		if (totalAccepted > 0) {
			Map<BlockPos, Integer> distances = getDistancesFrom(supplierPos);
			for (BlockPos pos : conduitPositions) {
				if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof WindowedPotentiaConduitBlockEntity windowedBe) {
					BlockState state = level.getBlockState(pos);
					if (state.hasProperty(WindowedPotentiaConduitBlock.AXIS)) {
						Direction.Axis axis = state.getValue(WindowedPotentiaConduitBlock.AXIS);
						Direction flowDir = determineFlowDirection(supplierPos, pos, axis, distances);
						windowedBe.recordFlow(stack, flowDir);
					}
				}
			}
		}

		return totalAccepted;
	}

	public Map<BlockPos, Integer> getDistancesFrom(BlockPos supplierPos) {
		return distanceCache.computeIfAbsent(supplierPos, this::computeDistancesFrom);
	}

	private Map<BlockPos, Integer> computeDistancesFrom(BlockPos supplierPos) {
		Map<BlockPos, Integer> distances = new HashMap<>();
		Queue<BlockPos> queue = new ArrayDeque<>();
		for (Direction d : Direction.values()) {
			BlockPos p = supplierPos.relative(d);
			if (conduitPositions.contains(p)) {
				distances.put(p, 0);
				queue.add(p);
			}
		}
		while (!queue.isEmpty()) {
			BlockPos current = queue.poll();
			int dist = distances.get(current);
			List<BlockPos> neighbors = adjacency.get(current);
			if (neighbors != null) {
				for (BlockPos next : neighbors) {
					if (!distances.containsKey(next)) {
						distances.put(next, dist + 1);
						queue.add(next);
					}
				}
			}
		}
		return distances;
	}

	public Direction determineFlowDirection(BlockPos supplierPos, BlockPos pos, Direction.Axis axis) {
		return determineFlowDirection(supplierPos, pos, axis, getDistancesFrom(supplierPos));
	}

	public Direction determineFlowDirection(BlockPos supplierPos, BlockPos pos, Direction.Axis axis, Map<BlockPos, Integer> distances) {
		BlockPos negPos = pos.relative(axis, -1);
		BlockPos posPos = pos.relative(axis, 1);

		if (supplierPos.equals(negPos)) {
			return Direction.get(Direction.AxisDirection.POSITIVE, axis);
		}
		if (supplierPos.equals(posPos)) {
			return Direction.get(Direction.AxisDirection.NEGATIVE, axis);
		}

		Integer negDist = distances.get(negPos);
		Integer posDist = distances.get(posPos);

		if (negDist != null && posDist != null) {
			if (negDist < posDist) {
				return Direction.get(Direction.AxisDirection.POSITIVE, axis);
			} else if (posDist < negDist) {
				return Direction.get(Direction.AxisDirection.NEGATIVE, axis);
			}
		} else if (negDist != null) {
			return Direction.get(Direction.AxisDirection.POSITIVE, axis);
		} else if (posDist != null) {
			return Direction.get(Direction.AxisDirection.NEGATIVE, axis);
		}

		return Direction.get(Direction.AxisDirection.POSITIVE, axis);
	}

	void addConnectionForTest(BlockPos from, BlockPos to) {
		conduitPositions.add(from);
		conduitPositions.add(to);
		adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
		adjacency.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
	}

	private record ConsumerTarget(IPotentiaHandler handler, long maxAccepted) {}

	public Set<BlockPos> getConduitPositions() {
		return Collections.unmodifiableSet(conduitPositions);
	}

	public List<Endpoint> getEndpoints() {
		return Collections.unmodifiableList(endpoints);
	}
}
