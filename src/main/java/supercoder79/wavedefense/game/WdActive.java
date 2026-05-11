package supercoder79.wavedefense.game;

import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.map.WdMap;
import supercoder79.wavedefense.util.ASCIIProgressBar;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.PlayerLimiter;
import xyz.nucleoid.plasmid.api.game.common.config.PlayerLimiterConfig;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.player.MutablePlayerSet;
import xyz.nucleoid.plasmid.api.game.player.PlayerSet;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.api.util.PlayerRef;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class WdActive {
    public final GameSpace space;
    public final ServerLevel world;
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

    private WdActive(GameSpace space, ServerLevel world, WdMap map, WdConfig config, MutablePlayerSet participants, GlobalWidgets widgets) {
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

    public static void open(GameSpace gameSpace, WdMap map, WdConfig config, ServerLevel world) {
        gameSpace.setActivity(game -> {
            GlobalWidgets widgets = GlobalWidgets.addTo(game);
            WdActive active = new WdActive(gameSpace, world, map, config, gameSpace.getPlayers().participants().copy(world.getServer()), widgets);

            game.setRule(GameRuleType.CRAFTING, EventResult.ALLOW);
            game.setRule(GameRuleType.PORTALS, EventResult.DENY);
            game.setRule(GameRuleType.PVP, EventResult.DENY);
            game.setRule(GameRuleType.BLOCK_DROPS, EventResult.ALLOW);
            game.setRule(GameRuleType.FALL_DAMAGE, EventResult.ALLOW);
            game.setRule(GameRuleType.HUNGER, EventResult.ALLOW);
            game.setRule(GameRuleType.THROW_ITEMS, EventResult.DENY);
            game.setRule(GameRuleType.INTERACTION, EventResult.ALLOW);

            game.listen(GameActivityEvents.ENABLE, active::open);
            game.listen(GameActivityEvents.STATE_UPDATE, state -> state.canPlay(false));
            game.listen(GamePlayerEvents.OFFER, JoinOffer::acceptSpectators);
            game.listen(GamePlayerEvents.ACCEPT, offer -> offer.teleport(world, active.guide.getCenterPos()));
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
        for (ServerPlayer player : this.space.getPlayers().participants()) {
            this.spawnParticipant(player);
        }

        for (ServerPlayer player : this.space.getPlayers().spectators()) {
            this.spawnSpectator(player);
        }
    }

    private void addPlayer(ServerPlayer player) {
        this.spawnSpectator(player);
    }

    private void removePlayer(ServerPlayer player) {
        participants.remove(player);
    }

    private void tick() {
        long time = this.world.getGameTime();

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
            for (ServerPlayer player : this.participants) {
                BlockPos.MutableBlockPos mutable = player.blockPosition().mutable();

                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        for (int y = 0; y <= 2; y++) {

                            BlockPos local = mutable.offset(x, y, z);
                            if (this.world.getBlockState(local).is(Blocks.CHEST)) {
                                if (!this.openedChests.contains(local)) {
                                    this.participants.forEach((participant) -> {
                                        participant.sendSystemMessage(Component.literal(player.getScoreboardName() + " has found a loot chest!"), false);

                                        if (new Random().nextInt(4) == 0) {
                                            participant.sendSystemMessage(Component.literal("You recieved 6 iron and 1 gold!"), false);
                                            participant.getInventory().add(new ItemStack(Items.IRON_INGOT, 6));
                                            participant.getInventory().add(new ItemStack(Items.GOLD_INGOT, 1));
                                        } else {
                                            participant.sendSystemMessage(Component.literal("You recieved 12 iron!"), false);
                                            participant.getInventory().add(new ItemStack(Items.IRON_INGOT, 12));
                                        }
                                    });

                                    // Change glowstone to obsidian
                                    world.setBlockAndUpdate(local.below(), Blocks.OBSIDIAN.defaultBlockState());

                                    this.openedChests.add(local);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Entity entity : world.getAllEntities()) {
            if (entity instanceof WaveEntity) {
                String prefix = ((WaveEntity) entity).getMod().prefix;
                MutableComponent name = Component.literal((prefix + " " + ((WaveEntity) entity).getMonsterClass().name()));

                if (prefix.equals(""))
                    name = Component.literal((((WaveEntity) entity).getMonsterClass().name()));

                if (((WaveEntity) entity).showHealth()) {
                    MutableComponent healthBar = ASCIIProgressBar.get(((Mob) entity).getHealth() / ((Mob) entity).getMaxHealth(), 7);

                    entity.setCustomName(name.append(" ").append(healthBar));
                } else entity.setCustomName(name);
            }
        }
    }

    private InteractionResult onUseItem(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() == Items.COMPASS) {
            WdItemShop.open(player, this);
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    private EventResult onEntityDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof WaveEntity) {
            WdWave activeWave = waveManager.getActiveWave();
            if (activeWave != null) {
                activeWave.onMonsterKilled(((WaveEntity) entity).monsterScore());

                if (source.getEntity() instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer) source.getEntity();

                    player.getInventory().add(new ItemStack(Items.IRON_INGOT, ((WaveEntity) entity).ironCount(entity.getRandom())));
                    player.getInventory().add(new ItemStack(Items.GOLD_INGOT, ((WaveEntity) entity).goldCount(entity.getRandom())));
                    player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }

            return EventResult.DENY;
        }

        return EventResult.PASS;
    }

    private EventResult onPlayerDeath(ServerPlayer player, DamageSource source) {
        this.eliminatePlayer(player);

        if (participants.isEmpty()) {
            // Display win results
            PlayerSet players = space.getPlayers();
            players.sendMessage(Component.literal("All players died....").withStyle(ChatFormatting.DARK_RED));
            players.sendMessage(Component.literal("You made it to wave " + waveManager.getWaveOrdinal() + ".").withStyle(ChatFormatting.DARK_RED));

            // Close game in 10 secs
            this.gameCloseTick = this.world.getGameTime() + (10 * 20);
        }

        return EventResult.DENY;
    }

    // TODO: this doesn't work. The logic has been moved to tick() as a hacky workaround.
    private InteractionResult onUseBlock(ServerPlayer player, InteractionHand hand, BlockHitResult hitResult) {
        if (this.world.getBlockState(hitResult.getBlockPos()).is(Blocks.CHEST)) {
            if (!this.openedChests.contains(hitResult.getBlockPos())) {
                for (ServerPlayer participant : this.participants) {
                    participant.sendSystemMessage(Component.literal(player.getDisplayName() + " has found a loot chest!"), false);
                    participant.sendSystemMessage(Component.literal("You recieved 12 iron."), false);
                    participant.getInventory().add(new ItemStack(Items.IRON_INGOT, 12));
                    participant.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }

                this.openedChests.add(hitResult.getBlockPos());
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    private void spawnParticipant(ServerPlayer player) {
        this.spawnLogic.resetPlayer(player, GameType.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
        this.guide.onAddPlayer(player);

        player.getInventory().add(0,
                ItemStackBuilder.of(Items.IRON_SWORD)
                        .setUnbreakable()
                        .build()
        );

        player.getInventory().add(1,
                ItemStackBuilder.of(Items.BOW)
                        .setUnbreakable()
                        .build()
        );

        player.getInventory().add(2,
                ItemStackBuilder.of(Items.CROSSBOW)
                        .setUnbreakable()
                        .build()
        );

        player.getInventory().add(3,
                ItemStackBuilder.of(Items.COOKED_BEEF)
                        .setCount(8)
                        .build()
        );

        player.getInventory().add(4,
                ItemStackBuilder.of(Items.ARROW)
                        .setCount(8)
                        .build()
        );

        player.getInventory().add(8,
                ItemStackBuilder.of(Items.COMPASS)
                        .setName(Component.literal("Item Shop"))
                        .build()
        );

        player.setItemSlot(EquipmentSlot.HEAD, ItemStackBuilder.of(Items.CHAINMAIL_HELMET).setUnbreakable().build());
        player.setItemSlot(EquipmentSlot.CHEST, ItemStackBuilder.of(Items.CHAINMAIL_CHESTPLATE).setUnbreakable().build());
        player.setItemSlot(EquipmentSlot.LEGS, ItemStackBuilder.of(Items.CHAINMAIL_LEGGINGS).setUnbreakable().build());
        player.setItemSlot(EquipmentSlot.FEET, ItemStackBuilder.of(Items.CHAINMAIL_BOOTS).setUnbreakable().build());

        players.put(PlayerRef.of(player), new WdPlayerProperties());
    }

    private void eliminatePlayer(ServerPlayer player) {
        if (!participants.remove(player)) {
            return;
        }

        Component message = player.getDisplayName().copy().append(" succumbed to the monsters....")
                .withStyle(ChatFormatting.RED);

        PlayerSet players = this.space.getPlayers();
        players.sendMessage(message);
        players.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP);

        this.spawnSpectator(player);
    }

    private void spawnSpectator(ServerPlayer player) {
        this.spawnLogic.resetPlayer(player, GameType.SPECTATOR);
        this.spawnLogic.spawnPlayer(player);
    }

    private void damageFarPlayers(Vec3 centerPos) {
        int maxDistance = this.config.spawnRadius + 5;
        double maxDistance2 = maxDistance * maxDistance;

        List<ServerPlayer> farPlayers = new ArrayList<>();

        for (ServerPlayer player : participants) {
            double deltaX = player.getX() - centerPos.x();
            double deltaZ = player.getZ() - centerPos.z();

            if (deltaX * deltaX + deltaZ * deltaZ > maxDistance2) {
                if (!player.isCreative() && !player.isSpectator()) {
                    farPlayers.add(player);
                }
            }
        }

        for (ServerPlayer player : farPlayers) {
            MutableComponent message = Component.literal("You are too far away from your villager!");
            player.sendSystemMessage(message.withStyle(ChatFormatting.RED), true);

            player.hurtServer(world, player.damageSources().fellOutOfWorld(), 0.5F);
        }
    }

    public PlayerSet getParticipants() {
        return participants;
    }
}
