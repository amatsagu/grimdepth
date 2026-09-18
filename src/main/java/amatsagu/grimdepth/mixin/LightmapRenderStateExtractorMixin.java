package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "extract", at = @At("TAIL"))
	private void grimdepth$adjustBrightnessAndNightVision(LightmapRenderState renderState, float partialTick, CallbackInfo ci) {
		if (this.minecraft.player == null) {
			return;
		}

		LocalPlayer player = this.minecraft.player;
		GrimdepthConfig.LightingConfig config = GrimdepthConfig.INSTANCE.lighting;

		if (player.hasEffect(MobEffects.NIGHT_VISION)) {
			float waterVision = player.getWaterVision();
			if (waterVision > 0.0F && player.hasEffect(MobEffects.CONDUIT_POWER)) {
				renderState.nightVisionEffectIntensity = waterVision;
			} else {
				renderState.nightVisionEffectIntensity = 0.0F;
			}
		}

		float nvBoost = 0.0F;
		MobEffectInstance nvEffect = player.getEffect(MobEffects.NIGHT_VISION);
		if (nvEffect != null) {
			int amplifier = Math.max(0, nvEffect.getAmplifier());
			nvBoost = (float) (config.nightVisionBaseBoost + config.nightVisionPerLevelBoost * amplifier);
		}

		float rawGamma = this.minecraft.options.gamma().get().floatValue();
		float darknessScale = this.minecraft.options.darknessEffectScale().get().floatValue();
		float darkness = player.getEffectBlendFactor(MobEffects.DARKNESS, partialTick) * darknessScale;

		float effectiveGamma = rawGamma + nvBoost;
		float scale = (float) config.brightnessDarkeningScale;
		float brightness = Math.max(0.0F, effectiveGamma - darkness) * scale;

		renderState.brightness = Mth.clamp(brightness, 0.0F, 1.0F);
	}
}
