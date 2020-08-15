package supercoder79.wavedefense.map;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.StructureAccessor;

import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.biome.FakeBiomeSource;
import supercoder79.wavedefense.map.feature.ShrubGen;
import xyz.nucleoid.plasmid.game.gen.feature.GrassGen;
import xyz.nucleoid.plasmid.game.gen.feature.PoplarTreeGen;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

import java.util.Random;

public class WaveDefenseChunkGenerator extends GameChunkGenerator {
	private final OpenSimplexNoise baseNoise;
	private final OpenSimplexNoise detailNoise;

	private final WaveDefenseMap map;
	private final double pathRadius;
	private final FakeBiomeSource biomeSource;

	public WaveDefenseChunkGenerator(MinecraftServer server, WaveDefenseMap map) {
		super(server);
		Random random = new Random();
		this.baseNoise = new OpenSimplexNoise(random.nextLong());
		this.detailNoise = new OpenSimplexNoise(random.nextLong());

		this.map = map;
		this.pathRadius = map.config.pathConfig.pathWidth * map.config.pathConfig.pathWidth;

		this.biomeSource = new FakeBiomeSource(server.getRegistryManager().get(Registry.BIOME_KEY), random.nextLong());
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

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				// Create base terrain
				double noise = baseNoise.eval(x / 256.0, z / 256.0);
				noise *= noise > 0 ? 14 : 12;

				// Add small details to make the terrain less rounded
				noise += detailNoise.eval(x / 20.0, z / 20.0) * 3.25;

				int height = (int) (56 + noise);

				BlockState surface = Blocks.GRASS_BLOCK.getDefaultState();
				BlockState waterState = Blocks.WATER.getDefaultState();
				mutable.set(x, 0, z);
				if (this.map.path.distanceToPath2(mutable) < pathRadius) {
					surface = Blocks.GRASS_PATH.getDefaultState();

					// Use a very low frequency noise to basically be a more coherent random
					// Technically we should be using a seperate noise here but the detail one can do for now :P
					if (detailNoise.eval(x / 2.0, z / 2.0) > 0) {
						waterState = Blocks.OAK_PLANKS.getDefaultState();
					}
				}

				// Generation height ensures that the generator interates up to at least the water level.
				int genHeight = Math.max(height, 48);
				for (int y = 0; y <= genHeight; y++) {
					// Simple surface building
					BlockState state = Blocks.STONE.getDefaultState();
					if (y == height) {
						// If the height and the generation height are the same, it means that we're on land
						if (height == genHeight) {
							state = surface;
						} else {
							// height and genHeight are different, so we're under water. Place dirt instead of grass.
							state = Blocks.DIRT.getDefaultState();
						}
					} else if ((height - y) <= 3) { //TODO: biome controls under depth
						state = Blocks.DIRT.getDefaultState();
					} else if (y == 0) {
						state = Blocks.BEDROCK.getDefaultState();
					}

					// If the y is higher than the land height, then we must place water
					if (y > height) {
						state = waterState;
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

			PoplarTreeGen.INSTANCE.generate(region, mutable.set(x, y, z).toImmutable(), random);
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
	}
}
