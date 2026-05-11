package supercoder79.wavedefense.entity.monster.waveentity;

import org.joml.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public final class WaveWitchEntity extends Witch implements WaveEntity {
    private final WdActive game;
    private MonsterClass monsterClass;

    public WaveWitchEntity(Level world, WdActive game, MonsterClass monsterClass) {
        super(EntityType.WITCH, world);
        this.game = game;
        this.setMonsterClass(monsterClass);

        this.goalSelector.addGoal(0, new MoveTowardGameCenterGoal<>(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Mob.class, 1, false, false, (e, a) -> !(e instanceof Witch)));

        this.setAttributes();
    }

    double particleSpawnY = 0;
    int state = 0;
    int stateTimer = 200;

    @Override
    public void tick() {
        super.tick();

        particleSpawnY += 0.1d;
        if (particleSpawnY > 1.5d)
            particleSpawnY = -0.5d;

        stateTimer--;
        if (stateTimer <= 0) {
            stateTimer = 200;
            state = random.nextInt(4);
        }

        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;
        float scale = 0.9f;

        switch (state) {
            // heal
            case 0:
                red = 1.0f;
                green = 0.5f;
                blue = 0.5f;
                break;

            // speed
            case 1:
                red = 0.5f;
                green = 1f;
                blue = 1f;
                scale = 0.7f;
                break;

            // invisibility
            case 2:
                red = 0.5f;
                green = 0.5f;
                blue = 1f;
                scale = 0.6f;
                break;

            // poison
            case 3:
                red = 0.5f;
                green = 1.0f;
                blue = 0.5f;
                scale = 0.8f;
                break;
        }

        if (particleSpawnY >= 0 && stateTimer % 2 == 0) {
            for (Entity entity : this.level().getEntities(this,
                    new AABB(this.position().subtract(3, 3, 3),
                            this.position().add(3, 3, 3)), e -> !(e.equals(this)))) {

                Mob mob;
                Player player;

                if (entity instanceof Mob) {
                    mob = (Mob) entity;

                    switch (state) {
                        case 0:
                            mob.heal(0.15f);
                            break;
                        case 1:
                            mob.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 0));
                            break;
                        case 2:
                            mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 15, 0));
                            stateTimer--;
                    }
                }
                else if (entity instanceof Player) {
                    player = (Player) entity;

                    if (state == 3) {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1));
                    }
                }
            }
        }

        ((ServerLevel) this.level()).sendParticles(
                new DustParticleOptions(ARGB.colorFromFloat(1, red, green, blue), scale + stateTimer / 150f),
                this.getX(), this.getY() + particleSpawnY + 0.3, this.getZ(),
                2, 0.2, 0.0, 0.2, 0.1
        );
    }

    public void setAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMonsterClass().maxHealth());
        this.setHealth((float) this.getMonsterClass().maxHealth());

        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
    }

    @Override
    public boolean showHealth() {
        return true;
    }

    @Override
    public int ironCount(RandomSource random) {
        return this.getMonsterClass().ironCount(random) + this.getMod().ironBonus;
    }

    @Override
    public int goldCount(RandomSource random) {
        return this.getMonsterClass().goldCount(random);
    }

    @Override
    public int monsterScore() {
        return this.getMonsterClass().monsterPoints();
    }

    @Override
    public WdActive getGame() {
        return game;
    }

    @Override
    public MonsterClass getMonsterClass() {
        return monsterClass;
    }

    public void setMonsterClass(MonsterClass monsterClass) {
        this.monsterClass = monsterClass;
    }

    public MonsterModifier getMod() {
        return MonsterModifier.NORMAL;
    }

    public void setMod(MonsterModifier mod) {

    }
}
