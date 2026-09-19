package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.BackstepHelper;
import amatsagu.grimdepth.common.GrimdepthSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
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
	private void grimdepth$onPerformRangedAttack(LivingEntity target, float distanceFactor, CallbackInfo ci) {
		AbstractSkeleton self = (AbstractSkeleton) (Object) this;
		if (self.level() instanceof ServerLevel serverLevel) {
			ItemStack weapon = self.getMainHandItem();
			if (!weapon.is(Items.BOW) && !weapon.is(Items.CROSSBOW)) {
				weapon = self.getOffhandItem();
			}
			BackstepHelper.tryTrigger(serverLevel, self, weapon, target);
		}
	}
}
