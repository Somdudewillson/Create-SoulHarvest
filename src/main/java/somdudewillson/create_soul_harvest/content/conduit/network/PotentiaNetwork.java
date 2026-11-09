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

	public record Endpoint(BlockPos pos, Direction sideOnHandler) {}

	public static void invalidate(BlockPos pos) {
		PotentiaNetwork net = CACHE.remove(pos);
		if (net != null) {
			for (BlockPos p : net.conduitPositions) {
				CACHE.remove(p);
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
			for (BlockPos pos : conduitPositions) {
				if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof WindowedPotentiaConduitBlockEntity windowedBe) {
					BlockState state = level.getBlockState(pos);
					if (state.hasProperty(WindowedPotentiaConduitBlock.AXIS)) {
						Direction flowDir = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(WindowedPotentiaConduitBlock.AXIS));
						windowedBe.recordFlow(stack, flowDir);
					}
				}
			}
		}

		return totalAccepted;
	}

	private record ConsumerTarget(IPotentiaHandler handler, long maxAccepted) {}

	public Set<BlockPos> getConduitPositions() {
		return Collections.unmodifiableSet(conduitPositions);
	}

	public List<Endpoint> getEndpoints() {
		return Collections.unmodifiableList(endpoints);
	}
}
