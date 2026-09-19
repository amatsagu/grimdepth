package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "nightVisionScale", at = @At("HEAD"), cancellable = true)
	private static void grimdepth$disableNightVisionScale(LivingEntity entity, float partialTick, CallbackInfoReturnable<Float> cir) {
		if (!GrimdepthConfig.INSTANCE.lighting.disableNightVisionFlashingShader) {
			return;
		}
		cir.setReturnValue(0.0F);
	}
}
