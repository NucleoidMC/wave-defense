package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.SkeletonClass;
import supercoder79.wavedefense.entity.monster.classes.StrayClasses;
import supercoder79.wavedefense.entity.projectile.WizardsSnowballEntity;
import supercoder79.wavedefense.game.WdActive;

public class WaveStrayEntity extends StrayEntity implements WaveEntity {
	private final WdActive game;
	private MonsterModifier mod;
	private SkeletonClass skeletonClass;

	public WaveStrayEntity(World world, WdActive game, SkeletonClass skeletonClass) {
		super(EntityType.STRAY, world);
		this.game = game;
		this.setMonsterClass(skeletonClass);

		this.initializeGoals();
		this.setAttributes();
	}

	@Override
	public void tick() {
		super.tick();
		if (getMonsterClass() == StrayClasses.WIZARD)
			wizardTick();
	}

	private int shootSnowballTimer = 60;

	private void wizardTick() {
		PlayerEntity closestPlayer = world.getClosestPlayer(this, 64);
		shootSnowballTimer--;

		if (shootSnowballTimer == 4)
			this.setAttacking(true);

		if (shootSnowballTimer == 36)
			this.setAttacking(false);

		if (closestPlayer != null && shootSnowballTimer <= 0 && shootSnowballTimer % 10 == 0) {
			WizardsSnowballEntity snowball = new WizardsSnowballEntity(world, this);
			snowball.setVelocity(closestPlayer.getPos().subtract(this.getPos()).multiply(0.09));
			world.spawnEntity(snowball);

			if (shootSnowballTimer <= -20)
			shootSnowballTimer = 60;
		}
	}

	@Override
	protected void initGoals() {

	}

	@Override
	public boolean showHealth() {
		return getMonsterClass().equals(StrayClasses.WIZARD);
	}

	protected void initializeGoals() {
		this.goalSelector.add(1, new BowAttackGoal<>(this, this.getMonsterClass().speed(), this.getMonsterClass().attackInterval(), this.getMonsterClass().range()));
		this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
	}

	public void setAttributes() {
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getMonsterClass().maxHealth());
		this.setHealth((float) this.getMonsterClass().maxHealth());
		this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(64d);
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
		PersistentProjectileEntity arrowProjectile = this.createArrowProjectile(itemStack, pullProgress);
		arrowProjectile.setDamage(arrowProjectile.getDamage() * this.getMonsterClass().damageScale());

		// Add modifier effect
		if (this.getMod().effect != null) {
			((ArrowEntity) arrowProjectile).addEffect(this.getMod().effect.get());
		}

		double xDist = target.getX() - this.getX();
		double yDist = target.getBodyY(0.3333333333333333D) - arrowProjectile.getY();
		double zDist = target.getZ() - this.getZ();
		double yScale = MathHelper.sqrt(xDist * xDist + zDist * zDist);

		arrowProjectile.setVelocity(xDist, yDist + yScale * 0.20000000298023224D, zDist, this.getMonsterClass().arrowSpeed(), this.getMonsterClass().arrowDivergence());
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

		this.world.spawnEntity(arrowProjectile);
	}

	@Override
	public int ironCount() {
		return this.getMonsterClass().ironCount() + this.getMod().ironBonus;
	}

	@Override
	public int goldCount() {
		return this.getMonsterClass().goldCount();
	}

	@Override
	public int monsterScore() {
		return this.getMonsterClass().monsterPoints();
	}

	@Override
	public WdActive getGame() {
		return game;
	}

	public SkeletonClass getMonsterClass() {
		return skeletonClass;
	}

	public void setMonsterClass(SkeletonClass skeletonClass) {
		this.skeletonClass = skeletonClass;
	}

	public MonsterModifier getMod() {
		return mod;
	}

	@Override
	public void setMod(MonsterModifier mod) {
		this.mod = mod;
	}
}
