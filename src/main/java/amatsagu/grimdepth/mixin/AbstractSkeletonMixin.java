package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.BackstepHelper;
import amatsagu.grimdepth.common.GrimdepthSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin {

	@Inject(method = "finalizeSpawn", at = @At("TAIL"))
	private void grimdepth$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
		GrimdepthSpawner.applySkeletonSpawn((AbstractSkeleton) (Object) this, level);
	}

	@Inject(method = "performRangedAttack", at = @At("TAIL"))
	private void grimdepth$onSkeletonPerformRangedAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
		AbstractSkeleton skeleton = (AbstractSkeleton) (Object) this;
		if (!(skeleton.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		ItemStack weapon = skeleton.getItemInHand(ProjectileUtil.getWeaponHoldingHand(skeleton, Items.BOW));
		if (weapon.isEmpty()) {
			weapon = skeleton.getMainHandItem();
		}
		BackstepHelper.tryTrigger(serverLevel, skeleton, weapon, target);
	}
}
