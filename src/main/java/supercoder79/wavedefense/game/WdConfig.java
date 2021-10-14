package supercoder79.wavedefense.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public final class WdConfig {
    public static final Codec<WdConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            Path.CODEC.fieldOf("path").forGetter(config -> config.path),
            Codec.INT.fieldOf("spawn_radius").forGetter(config -> config.spawnRadius),
            Codec.DOUBLE.fieldOf("min_wave_spacing").forGetter(config -> config.minWaveSpacing),
            Codec.DOUBLE.fieldOf("max_wave_spacing").forGetter(config -> config.maxWaveSpacing),
            MonsterSpawns.CODEC.fieldOf("monster_spawns").forGetter(config -> config.monsterSpawns),
            MonsterSpawnChoices.CODEC.fieldOf("monster_spawn_choices").forGetter(config -> config.monsterSpawnChoices),
            Shop.CODEC.fieldOf("shop").forGetter(config -> config.shop)
    ).apply(instance, (playerConfig1, path1, spawnRadius1, minWaveSpacing1, maxWaveSpacing1, monsterSpawns1, monsterSpawnChoices1, shop1) -> new WdConfig(playerConfig1, path1, monsterSpawns1, monsterSpawnChoices1, shop1, spawnRadius1, minWaveSpacing1, maxWaveSpacing1)));

    public final PlayerConfig playerConfig;
    public final Path path;
    public final MonsterSpawns monsterSpawns;
    public final MonsterSpawnChoices monsterSpawnChoices;
    public final Shop shop;
    public final int spawnRadius;
    public final double minWaveSpacing;
    public final double maxWaveSpacing;

    public WdConfig(PlayerConfig playerConfig, Path path, MonsterSpawns monsterSpawns, MonsterSpawnChoices monsterSpawnChoices, Shop shop, int spawnRadius, double minWaveSpacing, double maxWaveSpacing) {
        this.playerConfig = playerConfig;
        this.path = path;
        this.monsterSpawns = monsterSpawns;
        this.monsterSpawnChoices = monsterSpawnChoices;
        this.shop = shop;
        this.spawnRadius = spawnRadius;
        this.minWaveSpacing = minWaveSpacing;
        this.maxWaveSpacing = maxWaveSpacing;
    }

    public static final class Path {
        public static final Codec<Path> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("length").forGetter(config -> config.length),
                Codec.INT.fieldOf("segment_length").forGetter(config -> config.segmentLength),
                Codec.DOUBLE.fieldOf("radius").forGetter(config -> config.radius)
        ).apply(instance, Path::new));

        public final int length;
        public final int segmentLength;
        public final double radius;

        public Path(int length, int segmentLength, double radius) {
            this.length = length;
            this.segmentLength = segmentLength;
            this.radius = radius;
        }
    }

    public static final class MonsterSpawns {
        public static final Codec<MonsterSpawns> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("post_wave_5_scale").forGetter(config -> config.postWaveFiveScale),
                Codec.DOUBLE.fieldOf("base_group_size_scale").forGetter(config -> config.baseGroupSizeScale),
                Codec.DOUBLE.fieldOf("base_index_scale").forGetter(config -> config.baseIndexScale),
                Codec.DOUBLE.fieldOf("index_scale").forGetter(config -> config.indexScale),
                Codec.DOUBLE.fieldOf("group_size_scale").forGetter(config -> config.groupSizeScale),
                Codec.DOUBLE.fieldOf("upper_group_size_scale").forGetter(config -> config.upperGroupSizeScale),
                Codec.DOUBLE.fieldOf("upper_index_scale").forGetter(config -> config.upperIndexScale)
        ).apply(instance, MonsterSpawns::new));

        public final double postWaveFiveScale;
        public final double baseGroupSizeScale;
        public final double baseIndexScale;
        public final double indexScale;
        public final double groupSizeScale;
        public final double upperGroupSizeScale;
        public final double upperIndexScale;

        public MonsterSpawns(double postWaveFiveScale, double baseGroupSizeScale, double baseIndexScale, double indexScale, double groupSizeScale, double upperGroupSizeScale, double upperIndexScale) {
            this.postWaveFiveScale = postWaveFiveScale;
            this.baseGroupSizeScale = baseGroupSizeScale;
            this.baseIndexScale = baseIndexScale;
            this.indexScale = indexScale;
            this.groupSizeScale = groupSizeScale;
            this.upperGroupSizeScale = upperGroupSizeScale;
            this.upperIndexScale = upperIndexScale;
        }
    }

    public static final class MonsterSpawnChoices {
        public static final Codec<MonsterSpawnChoices> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("zombie").forGetter(config -> config.zombie),
                Codec.DOUBLE.fieldOf("skeleton").forGetter(config -> config.skeleton),
                Codec.DOUBLE.fieldOf("phantom").forGetter(config -> config.phantom),
                Codec.DOUBLE.fieldOf("witch").forGetter(config -> config.witch),
                Codec.DOUBLE.fieldOf("cave_spider").forGetter(config -> config.caveSpider)
        ).apply(instance, MonsterSpawnChoices::new));

        public final double zombie;
        public final double skeleton;
        public final double phantom;
        public final double witch;
        public final double caveSpider;

        public MonsterSpawnChoices(double zombie, double skeleton, double phantom, double witch, double caveSpider) {
            this.zombie = zombie;
            this.skeleton = skeleton;
            this.phantom = phantom;
            this.witch = witch;
            this.caveSpider = caveSpider;
        }
    }

    public static final class Shop {
        public static final Codec<Shop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SwordType.CODEC.fieldOf("sword_type").forGetter(config -> config.swordType),
                ArmorType.CODEC.fieldOf("armor_type").forGetter(config -> config.armorType),
                Item.CODEC.fieldOf("bread").forGetter(config -> config.bread),
                Item.CODEC.fieldOf("steak").forGetter(config -> config.steak),
                Item.CODEC.fieldOf("golden_carrot").forGetter(config -> config.goldenCarrot),
                Item.CODEC.fieldOf("golden_apple").forGetter(config -> config.goldenApple),
                Item.CODEC.fieldOf("arrow").forGetter(config -> config.arrow),
                Item.CODEC.fieldOf("healing_potion").forGetter(config -> config.healingPotion),
                Item.CODEC.fieldOf("harming_potion").forGetter(config -> config.harmingPotion),
                Item.CODEC.fieldOf("swiftness_potion").forGetter(config -> config.swiftnessPotion),
                Item.CODEC.fieldOf("regeneration_potion").forGetter(config -> config.regenerationPotion),
                Enchantment.CODEC.fieldOf("sharpness").forGetter(config -> config.sharpness),
                Enchantment.CODEC.fieldOf("power").forGetter(config -> config.power),
                Enchantment.CODEC.fieldOf("piercing").forGetter(config -> config.piercing),
                Enchantment.CODEC.fieldOf("protection").forGetter(config -> config.protection),
                ThreeLevelEnchantment.CODEC.fieldOf("quick_charge").forGetter(config -> config.quickCharge)
        ).apply(instance, Shop::new));

        public final SwordType swordType;
        public final ArmorType armorType;
        public final Item bread;
        public final Item steak;
        public final Item goldenCarrot;
        public final Item goldenApple;
        public final Item arrow;
        public final Item healingPotion;
        public final Item harmingPotion;
        public final Item swiftnessPotion;
        public final Item regenerationPotion;
        public final Enchantment sharpness;
        public final Enchantment power;
        public final Enchantment piercing;
        public final Enchantment protection;
        public final ThreeLevelEnchantment quickCharge;

        public Shop(SwordType swordType, ArmorType armorType, Item bread, Item steak, Item goldenCarrot, Item goldenApple, Item arrow, Item healingPotion, Item harmingPotion, Item swiftnessPotion, Item regenerationPotion, Enchantment sharpness, Enchantment power, Enchantment piercing, Enchantment protection, ThreeLevelEnchantment quickCharge) {
            this.swordType = swordType;
            this.armorType = armorType;
            this.bread = bread;
            this.steak = steak;
            this.goldenCarrot = goldenCarrot;
            this.goldenApple = goldenApple;
            this.arrow = arrow;
            this.healingPotion = healingPotion;
            this.harmingPotion = harmingPotion;
            this.swiftnessPotion = swiftnessPotion;
            this.regenerationPotion = regenerationPotion;
            this.sharpness = sharpness;
            this.power = power;
            this.piercing = piercing;
            this.protection = protection;
            this.quickCharge = quickCharge;
        }

        static class SwordType {
            public static final Codec<SwordType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("diamond").forGetter(config -> config.diamond),
                    Codec.INT.fieldOf("netherite").forGetter(config -> config.netherite)
            ).apply(instance, SwordType::new));

            public final int diamond;
            public final int netherite;

            public SwordType(int diamond, int netherite) {
                this.diamond = diamond;
                this.netherite = netherite;
            }
        }

        static class ArmorType {
            public static final Codec<ArmorType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("iron").forGetter(config -> config.iron),
                    Codec.INT.fieldOf("diamond").forGetter(config -> config.diamond),
                    Codec.INT.fieldOf("netherite").forGetter(config -> config.netherite)
            ).apply(instance, ArmorType::new));

            public final int iron;
            public final int diamond;
            public final int netherite;

            public ArmorType(int iron, int diamond, int netherite) {
                this.iron = iron;
                this.diamond = diamond;
                this.netherite = netherite;
            }
        }

        static class Item {
            public static final Codec<Item> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("count").forGetter(config -> config.count),
                    Codec.INT.fieldOf("cost").forGetter(config -> config.cost)
            ).apply(instance, Item::new));

            public final int count;
            public final int cost;

            public Item(int count, int cost) {
                this.count = count;
                this.cost = cost;
            }
        }

        static class Enchantment {
            public static final Codec<Enchantment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("base").forGetter(config -> config.base),
                    Codec.DOUBLE.fieldOf("scale").forGetter(config -> config.scale)
            ).apply(instance, Enchantment::new));

            public final int base;
            public final double scale;

            public Enchantment(int base, double scale) {
                this.base = base;
                this.scale = scale;
            }
        }

        static class ThreeLevelEnchantment {
            public static final Codec<ThreeLevelEnchantment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("1").forGetter(config -> config.lvl1),
                    Codec.INT.fieldOf("2").forGetter(config -> config.lvl2),
                    Codec.INT.fieldOf("3").forGetter(config -> config.lvl3)
            ).apply(instance, ThreeLevelEnchantment::new));

            public final int lvl1;
            public final int lvl2;
            public final int lvl3;

            public ThreeLevelEnchantment(int lvl1, int lvl2, int lvl3) {
                this.lvl1 = lvl1;
                this.lvl2 = lvl2;
                this.lvl3 = lvl3;
            }
        }
    }
}
