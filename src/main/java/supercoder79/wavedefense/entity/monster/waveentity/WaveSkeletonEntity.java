package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.world.entity.EntityTypes;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.SkeletonClass;
import supercoder79.wavedefense.entity.monster.classes.SkeletonClasses;
import supercoder79.wavedefense.entity.monster.classes.StrayClasses;
import supercoder79.wavedefense.game.WdActive;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class WaveSkeletonEntity extends Skeleton implements WaveEntity {
	private final WdActive game;
	private MonsterModifier mod;
	private SkeletonClass skeletonClass;

	public WaveSkeletonEntity(Level world, WdActive game, SkeletonClass skeletonClass) {
		super(EntityTypes.SKELETON, world);
		this.game = game;
		this.setMonsterClass(skeletonClass);

		this.initializeGoals();
		this.setAttributes();
	}

	@Override
	protected void registerGoals() {

	}

	@Override
	public boolean showHealth() {
		return this.getMonsterClass().equals(SkeletonClasses.SUMMONER);
	}

	protected void initializeGoals() {
		this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, this.getMonsterClass().speed(), this.getMonsterClass().attackInterval(), this.getMonsterClass().range()));
		this.goalSelector.addGoal(2, new MoveTowardGameCenterGoal<>(this));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, WaveZombieEntity.class, WaveSkeletonEntity.class));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public void setAttributes() {
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMonsterClass().maxHealth());
		this.setHealth((float) this.getMonsterClass().maxHealth());
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(64d);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float pullProgress) {
		var bow = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
		ItemStack itemStack = this.getProjectile(bow);
		var arrowProjectile = this.getArrow(itemStack, pullProgress, bow);
		arrowProjectile.setBaseDamage(2 * pullProgress * this.getMonsterClass().damageScale());

		// Add modifier effect
		if (this.getMod().effect != null) {
			((Arrow) arrowProjectile).addEffect(this.getMod().effect.get());
		}

		double xDist = target.getX() - this.getX();
		double yDist = target.getY(0.3333333333333333D) - arrowProjectile.getY();
		double zDist = target.getZ() - this.getZ();
		double yScale = Mth.sqrt((float) (xDist * xDist + zDist * zDist));

		arrowProjectile.shoot(xDist, yDist + yScale * 0.20000000298023224D, zDist, this.getMonsterClass().arrowSpeed(), this.getMonsterClass().arrowDivergence());
		this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

		this.level().addFreshEntity(arrowProjectile);
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
