package supercoder79.wavedefense.game;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.util.ASCIIProgressBar;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.MutablePlayerSet;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.*;

public final class WdActive {
    public final GameSpace space;
    public final ServerWorld world;
    public final WdMap map;
    public final WdConfig config;
    public final WdWaveManager waveManager;
    public final HashMap<PlayerRef, WdPlayerProperties> players = new HashMap<>();
    public final WdBar bar;
    public final WdGuide guide;
    public final int groupSize;
    private final MutablePlayerSet participants;
    private final WdSpawnLogic spawnLogic;
    private final Set<BlockPos> openedChests = new HashSet<>();
    public int averageGroupSize;
    private long gameCloseTick = Long.MAX_VALUE;

    private WdActive(GameSpace space, ServerWorld world, WdMap map, WdConfig config, MutablePlayerSet participants, GlobalWidgets widgets) {
        this.space = space;
        this.world = world;
        this.map = map;
        this.config = config;
        this.participants = participants;

        this.spawnLogic = new WdSpawnLogic(this.world, config);
        this.waveManager = new WdWaveManager(this);
        this.bar = WdBar.create(widgets);

        this.guide = new WdGuide(this);

        this.groupSize = participants.size();
        this.averageGroupSize = groupSize;
    }

    public static void open(GameSpace gameSpace, WdMap map, WdConfig config, ServerWorld world) {
        gameSpace.setActivity(game -> {
            GlobalWidgets widgets = GlobalWidgets.addTo(game);
            WdActive active = new WdActive(gameSpace, world, map, config, gameSpace.getPlayers().copy(world.getServer()), widgets);

            game.setRule(GameRuleType.CRAFTING, ActionResult.SUCCESS);
            game.setRule(GameRuleType.PORTALS, ActionResult.FAIL);
            game.setRule(GameRuleType.PVP, ActionResult.FAIL);
            game.setRule(GameRuleType.BLOCK_DROPS, ActionResult.SUCCESS);
            game.setRule(GameRuleType.FALL_DAMAGE, ActionResult.SUCCESS);
            game.setRule(GameRuleType.HUNGER, ActionResult.SUCCESS);
            game.setRule(GameRuleType.THROW_ITEMS, ActionResult.FAIL);
            game.setRule(GameRuleType.INTERACTION, ActionResult.SUCCESS);

            game.listen(GameActivityEvents.ENABLE, active::open);
            game.listen(GamePlayerEvents.OFFER, offer -> offer.accept(world, active.guide.getCenterPos()));
            game.listen(GamePlayerEvents.ADD, active::addPlayer);
            game.listen(GamePlayerEvents.REMOVE, active::removePlayer);

            game.listen(GameActivityEvents.TICK, active::tick);
            game.listen(ItemUseEvent.EVENT, active::onUseItem);

            game.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);
            game.listen(EntityDeathEvent.EVENT, active::onEntityDeath);
            game.listen(BlockUseEvent.EVENT, active::onUseBlock);
        });
    }

    private void open() {
        for (ServerPlayerEntity player : this.participants) {
            this.spawnParticipant(player);
        }
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnSpectator(player);
    }

    private void removePlayer(ServerPlayerEntity player) {
        participants.remove(player);
    }

    private void tick() {
        long time = this.world.getTime();

        if (time > gameCloseTick) {
            this.space.close(GameCloseReason.FINISHED);
            return;
        }

        this.guide.tick(time, waveManager.isActive());
        this.waveManager.tick(time, guide.getProgressBlocks());

        this.damageFarPlayers(guide.getCenterPos());

        this.bar.tick(waveManager.getActiveWave());

        // This is a horrifically cursed workaround for UseBlockListener not working. I'm sorry.
        if (time % 20 == 0) {
            for (ServerPlayerEntity player : this.participants) {
                BlockPos.Mutable mutable = player.getBlockPos().mutableCopy();

                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        for (int y = 0; y <= 2; y++) {

                            BlockPos local = mutable.add(x, y, z);
                            if (this.world.getBlockState(local).isOf(Blocks.CHEST)) {
                                if (!this.openedChests.contains(local)) {
                                    this.participants.forEach((participant) -> {
                                        participant.sendMessage(new LiteralText(player.getEntityName() + " has found a loot chest!"), false);

                                        if (new Random().nextInt(4) == 0) {
                                            participant.sendMessage(new LiteralText("You recieved 6 iron and 1 gold!"), false);
                                            participant.getInventory().insertStack(new ItemStack(Items.IRON_INGOT, 6));
                                            participant.getInventory().insertStack(new ItemStack(Items.GOLD_INGOT, 1));
                                        } else {
                                            participant.sendMessage(new LiteralText("You recieved 12 iron!"), false);
                                            participant.getInventory().insertStack(new ItemStack(Items.IRON_INGOT, 12));
                                        }
                                    });

                                    // Change glowstone to obsidian
                                    world.setBlockState(local.down(), Blocks.OBSIDIAN.getDefaultState());

                                    this.openedChests.add(local);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof WaveEntity) {
                String prefix = ((WaveEntity) entity).getMod().prefix;
                MutableText name = new LiteralText((prefix + " " + ((WaveEntity) entity).getMonsterClass().name()));

                if (prefix.equals(""))
                    name = new LiteralText((((WaveEntity) entity).getMonsterClass().name()));

                if (((WaveEntity) entity).showHealth()) {
                    MutableText healthBar = ASCIIProgressBar.get(((MobEntity) entity).getHealth() / ((MobEntity) entity).getMaxHealth(), 7);

                    entity.setCustomName(name.append(" ").append(healthBar));
                } else entity.setCustomName(name);
            }
        }
    }

    private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() == Items.COMPASS) {
            WdItemShop.open(player, this);
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(stack);
    }

    private ActionResult onEntityDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof WaveEntity) {
            WdWave activeWave = waveManager.getActiveWave();
            if (activeWave != null) {
                activeWave.onMonsterKilled(((WaveEntity) entity).monsterScore());

                if (source.getAttacker() instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();

                    player.getInventory().insertStack(new ItemStack(Items.IRON_INGOT, ((WaveEntity) entity).ironCount()));
                    player.getInventory().insertStack(new ItemStack(Items.GOLD_INGOT, ((WaveEntity) entity).goldCount()));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }

            return ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        this.eliminatePlayer(player);

        if (participants.isEmpty()) {
            // Display win results
            PlayerSet players = space.getPlayers();
            players.sendMessage(new LiteralText("All players died....").formatted(Formatting.DARK_RED));
            players.sendMessage(new LiteralText("You made it to wave " + waveManager.getWaveOrdinal() + ".").formatted(Formatting.DARK_RED));

            // Close game in 10 secs
            this.gameCloseTick = this.world.getTime() + (10 * 20);
        }

        return ActionResult.FAIL;
    }

    // TODO: this doesn't work. The logic has been moved to tick() as a hacky workaround.
    private ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (this.world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.CHEST)) {
            if (!this.openedChests.contains(hitResult.getBlockPos())) {
                for (ServerPlayerEntity participant : this.participants) {
                    participant.sendMessage(new LiteralText(player.getDisplayName() + " has found a loot chest!"), false);
                    participant.sendMessage(new LiteralText("You recieved 12 iron."), false);
                    participant.getInventory().insertStack(new ItemStack(Items.IRON_INGOT, 12));
                    participant.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }

                this.openedChests.add(hitResult.getBlockPos());
            }

            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    private void spawnParticipant(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
        this.guide.onAddPlayer(player);

        player.getInventory().insertStack(0,
                ItemStackBuilder.of(Items.IRON_SWORD)
                        .setUnbreakable()
                        .build()
        );

        player.getInventory().insertStack(1,
                ItemStackBuilder.of(Items.BOW)
                        .setUnbreakable()
                        .build()
        );

        player.getInventory().insertStack(2,
                ItemStackBuilder.of(Items.CROSSBOW)
                        .setUnbreakable()
                        .build()
        );

        player.getInventory().insertStack(3,
                ItemStackBuilder.of(Items.COOKED_BEEF)
                        .setCount(8)
                        .build()
        );

        player.getInventory().insertStack(4,
                ItemStackBuilder.of(Items.ARROW)
                        .setCount(8)
                        .build()
        );

        player.getInventory().insertStack(8,
                ItemStackBuilder.of(Items.COMPASS)
                        .setName(new LiteralText("Item Shop"))
                        .build()
        );

        player.getInventory().armor.set(3, ItemStackBuilder.of(Items.CHAINMAIL_HELMET).setUnbreakable().build());
        player.getInventory().armor.set(2, ItemStackBuilder.of(Items.CHAINMAIL_CHESTPLATE).setUnbreakable().build());
        player.getInventory().armor.set(1, ItemStackBuilder.of(Items.CHAINMAIL_LEGGINGS).setUnbreakable().build());
        player.getInventory().armor.set(0, ItemStackBuilder.of(Items.CHAINMAIL_BOOTS).setUnbreakable().build());

        players.put(PlayerRef.of(player), new WdPlayerProperties());
    }

    private void eliminatePlayer(ServerPlayerEntity player) {
        if (!participants.remove(player)) {
            return;
        }

        Text message = player.getDisplayName().shallowCopy().append(" succumbed to the monsters....")
                .formatted(Formatting.RED);

        PlayerSet players = this.space.getPlayers();
        players.sendMessage(message);
        players.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);

        this.spawnSpectator(player);
    }

    private void spawnSpectator(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
        this.spawnLogic.spawnPlayer(player);
    }

    private void damageFarPlayers(Vec3d centerPos) {
        int maxDistance = this.config.spawnRadius + 5;
        double maxDistance2 = maxDistance * maxDistance;

        List<ServerPlayerEntity> farPlayers = new ArrayList<>();

        for (ServerPlayerEntity player : participants) {
            double deltaX = player.getX() - centerPos.getX();
            double deltaZ = player.getZ() - centerPos.getZ();

            if (deltaX * deltaX + deltaZ * deltaZ > maxDistance2) {
                if (!player.isCreative() && !player.isSpectator()) {
                    farPlayers.add(player);
                }
            }
        }

        for (ServerPlayerEntity player : farPlayers) {
            LiteralText message = new LiteralText("You are too far away from your villager!");
            player.sendMessage(message.formatted(Formatting.RED), true);

            player.damage(DamageSource.OUT_OF_WORLD, 0.5F);
        }
    }

    public PlayerSet getParticipants() {
        return participants;
    }
}
