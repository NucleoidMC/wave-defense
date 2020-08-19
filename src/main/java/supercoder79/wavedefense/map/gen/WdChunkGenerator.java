package supercoder79.wavedefense.map.gen;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.StructureAccessor;
import supercoder79.wavedefense.game.WdConfig;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.biome.FakeBiomeSource;
import supercoder79.wavedefense.map.feature.CactusGen;
import supercoder79.wavedefense.map.feature.ImprovedDiskGen;
import supercoder79.wavedefense.map.feature.ShrubGen;
import xyz.nucleoid.plasmid.game.gen.feature.GrassGen;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

import java.util.Random;

public final class WdChunkGenerator extends GameChunkGenerator {
	private final WdHeightSampler heightSampler;
	private final OpenSimplexNoise pathNoise;
	private final OpenSimplexNoise detailNoise;
	private final OpenSimplexNoise erosionNoise;

	private final WdConfig config;
	private final WdMap map;
	private final double pathRadius;
	private final FakeBiomeSource biomeSource;

	private final int minBarrierRadius2;
	private final int maxBarrierRadius2;

	public WdChunkGenerator(MinecraftServer server, WdConfig config, WdMap map) {
		super(server);
		this.config = config;

		int minBarrierRadius = config.spawnRadius + 1;
		int maxBarrierRadius = minBarrierRadius + 1;
		this.minBarrierRadius2 = minBarrierRadius * minBarrierRadius;
		this.maxBarrierRadius2 = maxBarrierRadius * maxBarrierRadius;

		Random random = new Random();
		this.biomeSource = new FakeBiomeSource(server.getRegistryManager().get(Registry.BIOME_KEY), random.nextLong());
		this.heightSampler = new WdHeightSampler(map.path, biomeSource, random.nextLong());
		this.pathNoise = new OpenSimplexNoise(random.nextLong());
		this.detailNoise = new OpenSimplexNoise(random.nextLong());
		this.erosionNoise = new OpenSimplexNoise(random.nextLong());

		this.map = map;
		this.pathRadius = map.config.path.radius;
	}

	@Override
	public void populateBiomes(Registry<Biome> registry, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		((ProtoChunk)chunk).setBiomes(new BiomeArray(registry, chunkPos, this.biomeSource));
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor structures, Chunk chunk) {
		int chunkX = chunk.getPos().x * 16;
		int chunkZ = chunk.getPos().z * 16;

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = new Random();

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				mutable.set(x, 0, z);

				BiomeGen biome = biomeSource.getRealBiome(x, z);

				int terrainHeight = MathHelper.floor(this.heightSampler.sampleHeight(x, z));
				double slope = this.heightSampler.sampleSlope(x, z);

				BlockState surface = biome.topState(random);
				BlockState subsoil = biome.underState();
				BlockState underwater = biome.underWaterState();

				double erosionThreshold = 1.8 + this.erosionNoise.eval(x / 2.0, z / 2.0) * 0.5;
				if (slope > erosionThreshold) {
					surface = underwater = subsoil = Blocks.STONE.getDefaultState();
				}

				BlockState waterState = Blocks.WATER.getDefaultState();
				BlockState topWaterState = Blocks.WATER.getDefaultState();
				BlockState underWaterState = Blocks.WATER.getDefaultState();

				int distanceToPath2 = this.map.path.distanceToPath2(x, z);
				double pathRadius = (this.pathRadius + (this.pathNoise.eval(x / 48.0, z / 48.0) * (this.pathRadius * 0.25)));
				double pathRadius2 = pathRadius * pathRadius;

				if (distanceToPath2 < pathRadius2) {
					if (random.nextInt(12) != 0) {
						surface = biome.pathState();
					}

					underWaterState = Blocks.OAK_PLANKS.getDefaultState();

					// Use a very low frequency noise to basically be a more coherent random
					// Technically we should be using separate noises here but this can do for now :P
					double damageNoise = detailNoise.eval(x / 2.0, z / 2.0) + pathNoise.eval(x / 12.0, z / 12.0);
					if (damageNoise > -0.5) {
						topWaterState = Blocks.OAK_PLANKS.getDefaultState();

						// Randomly place support blocks for bridge
						if (random.nextInt(8) == 0) {
							waterState = Blocks.OAK_FENCE.getDefaultState().with(Properties.WATERLOGGED, Boolean.TRUE);
						}
					}
				}

				// Generation height ensures that the generator iterates up to at least the water level.
				int seaLevel = 48;
				BlockState air = Blocks.AIR.getDefaultState();
				BlockState stone = Blocks.STONE.getDefaultState();

				int genHeight = Math.max(terrainHeight, seaLevel);
				if (distanceToPath2 >= this.minBarrierRadius2 && distanceToPath2 <= this.maxBarrierRadius2) {
					genHeight = 255;
					air = waterState = Blocks.BARRIER.getDefaultState();
				}

				for (int y = 0; y <= genHeight; y++) {
					// Simple surface building
					BlockState state = air;

					if (y <= terrainHeight) {
						int depth = terrainHeight - y;
						if (y < seaLevel) {
							state = underwater;
						} else if (depth == 0) {
							state = surface;
						} else if (depth <= 3) {
							//TODO: biome controls under depth
							state = subsoil;
						} else {
							state = stone;
						}
					} else if (y <= seaLevel) {
						state = waterState;

						if (y == genHeight) { // Top layer of water can sometimes be a bridge
							state = topWaterState;
						} else if (y == (genHeight - 1)) { // Second to top layer is always a bridge
							state = underWaterState;
						}
					}

					// Set the state here
					chunk.setBlockState(mutable.set(x, y, z), state, false);
				}
			}
		}
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = new Random();

		int chunkX = region.getCenterChunkX() * 16;
		int chunkZ = region.getCenterChunkZ() * 16;

		BiomeGen biome = biomeSource.getRealBiome(chunkX + 8, chunkZ + 8);

		int treeAmt = biome.treeAmt(random);
		for (int i = 0; i < treeAmt; i++) {
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			int y = region.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z);

			biome.tree(x, z, random).generate(region, mutable.set(x, y, z).toImmutable(), random);
		}

		int shrubAmt = biome.shrubAmt(random);
		for (int i = 0; i < shrubAmt; i++) {
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			int y = region.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z);

			ShrubGen.INSTANCE.generate(region, mutable.set(x, y, z).toImmutable(), random);
		}

		int grassAmt = biome.grassAmt(random);
		for (int i = 0; i < grassAmt; i++) {
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			int y = region.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z);

			GrassGen.INSTANCE.generate(region, mutable.set(x, y, z).toImmutable(), random);
		}

		for (int i = 0; i < 4; i++) {
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			int y = region.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z);

			if (y <= 48) {
				ImprovedDiskGen.INSTANCE.generate(region, mutable.set(x, y, z).toImmutable(), random);
			}
		}

		int cactusAmt = biome.cactusAmt(random);
		for (int i = 0; i < cactusAmt; i++) {
			int x = chunkX + random.nextInt(16);
			int z = chunkZ + random.nextInt(16);
			int y = region.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z);

			CactusGen.INSTANCE.generate(region, mutable.set(x, y, z).toImmutable(), random);
		}
	}
}
