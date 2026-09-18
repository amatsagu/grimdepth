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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private boolean needsUpdate;

	@Unique
	private boolean grimdepth$lastHasNv = false;
	@Unique
	private int grimdepth$lastNvAmp = -1;
	@Unique
	private float grimdepth$lastGamma = -1.0F;

	@Inject(method = "extract", at = @At("HEAD"))
	private void grimdepth$detectLightmapChange(LightmapRenderState renderState, float partialTick, CallbackInfo ci) {
		if (this.minecraft.player == null) {
			return;
		}

		LocalPlayer player = this.minecraft.player;
		boolean hasNv = player.hasEffect(MobEffects.NIGHT_VISION);
		int amp = hasNv ? player.getEffect(MobEffects.NIGHT_VISION).getAmplifier() : -1;
		float gamma = this.minecraft.options.gamma().get().floatValue();

		if (hasNv != this.grimdepth$lastHasNv || amp != this.grimdepth$lastNvAmp || Math.abs(gamma - this.grimdepth$lastGamma) > 1e-4F) {
			this.needsUpdate = true;
			this.grimdepth$lastHasNv = hasNv;
			this.grimdepth$lastNvAmp = amp;
			this.grimdepth$lastGamma = gamma;
		}
	}

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

		// Anti-cheat: prevent artificially boosted brightness in configs
		if (rawGamma > (float) config.antiCheatMaxGamma) {
			rawGamma = (float) config.antiCheatResetGamma;
			this.minecraft.options.gamma().set(config.antiCheatResetGamma);
			this.minecraft.options.save();
		} else if (rawGamma < 0.0F) {
			rawGamma = 0.0F;
			this.minecraft.options.gamma().set(0.0D);
			this.minecraft.options.save();
		}

		float darknessScale = this.minecraft.options.darknessEffectScale().get().floatValue();
		float darkness = player.getEffectBlendFactor(MobEffects.DARKNESS, partialTick) * darknessScale;

		// Maximum brightness cap (200% = 2.0F)
		float maxCap = (float) config.maxBrightnessCap;

		// Effective gamma with night vision boost: clamped to [0.0, 2.0] so it never goes negative or exceeds 200%
		float effectiveGamma = Mth.clamp(rawGamma + nvBoost, 0.0F, maxCap);

		float scale = (float) config.brightnessDarkeningScale;
		float brightness = Math.max(0.0F, effectiveGamma - darkness) * scale;

		// Hard clamp: brightness cannot be negative and cannot exceed 200% (2.0F)
		renderState.brightness = Mth.clamp(brightness, 0.0F, maxCap);
	}
}
