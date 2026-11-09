package somdudewillson.create_soul_harvest.content.conduit.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.create_soul_harvest.content.conduit.PotentiaConduitBlock;
import somdudewillson.create_soul_harvest.content.potentia.IPotentiaHandler;
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
			if (!(currentState.getBlock() instanceof PotentiaConduitBlock conduit)) {
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
				if (neighborState.getBlock() instanceof PotentiaConduitBlock) {
					if (network.conduitPositions.add(neighborPos)) {
						frontier.add(neighborPos);
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

	public static void distributeFrom(Level level, BlockPos supplierPos, Direction pushSide, IPotentiaHandler supplier) {
		BlockPos startPipe = supplierPos.relative(pushSide);
		if (!(level.getBlockState(startPipe).getBlock() instanceof PotentiaConduitBlock)) {
			return;
		}

		PotentiaNetwork network = getOrCreate(level, startPipe);
		network.distribute(level, supplierPos, supplier);
	}

	public void distribute(Level level, BlockPos supplierPos, IPotentiaHandler supplier) {
		SoulPotentiaStack sample = supplier.drain(1000L, IPotentiaHandler.PotentiaAction.SIMULATE);
		if (sample.isEmpty()) {
			return;
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
			if (!handler.isPotentiaValid(0, sample)) {
				continue;
			}

			long simulatedAccept = handler.fill(sample.copy(), IPotentiaHandler.PotentiaAction.SIMULATE);
			if (simulatedAccept > 0) {
				validConsumers.add(new ConsumerTarget(handler, simulatedAccept));
			}
		}

		if (validConsumers.isEmpty()) {
			return;
		}

		long toDistribute = sample.getAmount();
		long remaining = toDistribute;

		for (int i = 0; i < validConsumers.size(); i++) {
			ConsumerTarget target = validConsumers.get(i);
			long share = Math.min(target.maxAccepted(), Math.max(1L, remaining / (validConsumers.size() - i)));
			SoulPotentiaStack offer = sample.copy();
			offer.setAmount(share);

			long accepted = target.handler().fill(offer, IPotentiaHandler.PotentiaAction.EXECUTE);
			if (accepted > 0) {
				supplier.drain(accepted, IPotentiaHandler.PotentiaAction.EXECUTE);
				remaining -= accepted;
			}
			if (remaining <= 0) {
				break;
			}
		}
	}

	private record ConsumerTarget(IPotentiaHandler handler, long maxAccepted) {}

	public Set<BlockPos> getConduitPositions() {
		return Collections.unmodifiableSet(conduitPositions);
	}

	public List<Endpoint> getEndpoints() {
		return Collections.unmodifiableList(endpoints);
	}
}
