package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class)
public class TargetGoalMixin {
	@Shadow
	@Final
	protected Mob mob;

	@Shadow
	protected LivingEntity targetMob;

	@Inject(method = "getFollowDistance", at = @At("RETURN"), cancellable = true)
	private void grimdepth$scaleFollowDistance(CallbackInfoReturnable<Double> cir) {
		if (!GrimdepthConfig.INSTANCE.nightmareAwareness.enabled) {
			return;
		}
		if (this.mob instanceof Enemy) {
			LivingEntity target = this.mob.getTarget();
			if (target == null) {
				target = this.targetMob;
			}
			if (target != null) {
				MobEffectInstance effect = target.getEffect(GrimdepthEffects.NIGHTMARE_AWARENESS);
				if (effect != null) {
					double multiplier = GrimdepthConfig.INSTANCE.nightmareAwareness.getAggroMultiplier(effect.getAmplifier());
					cir.setReturnValue(cir.getReturnValueD() * multiplier);
				}
			}
		}
	}
}
