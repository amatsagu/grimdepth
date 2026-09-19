package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

	@Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true)
	private void grimdepth$scaleNightmareVisibility(ServerLevel level, Entity attacker, CallbackInfoReturnable<Double> cir) {
		if (attacker != null && !(attacker instanceof Enemy)) {
			return;
		}
		MobEffectInstance effect = this.getEffect(GrimdepthEffects.NIGHTMARE_AWARENESS);
		if (effect == null) {
			return;
		}
		double multiplier = GrimdepthConfig.INSTANCE.nightmareAwareness.getAggroMultiplier(effect.getAmplifier());
		cir.setReturnValue(cir.getReturnValueD() * multiplier);
	}
}
