package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {
	@Shadow
	@Final
	private OptionInstance<Double> gamma;

	@ModifyArg(
			method = "<init>",
			slice = @Slice(
					from = @At(value = "CONSTANT", args = "stringValue=options.gamma")
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Lnet/minecraft/client/OptionInstance$ValueUpdateListener;)V",
					ordinal = 0
			),
			index = 4
	)
	private Object grimdepth$modifyDefaultGamma(Object initialValue) {
		return GrimdepthConfig.INSTANCE.lighting.defaultGamma;
	}

	@ModifyArg(
			method = "<init>",
			slice = @Slice(
					from = @At(value = "CONSTANT", args = "stringValue=options.gamma")
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Lnet/minecraft/client/OptionInstance$ValueUpdateListener;)V",
					ordinal = 0
			),
			index = 2
	)
	private OptionInstance.CaptionBasedToString<Double> grimdepth$modifyGammaCaption(OptionInstance.CaptionBasedToString<Double> original) {
		return (caption, value) -> {
			int val = (int) (value * 100.0);
			if (val == 0) {
				return Options.genericValueLabel(caption, Component.translatable("options.gamma.min"));
			} else if (val == 75) {
				return Options.genericValueLabel(caption, Component.translatable("options.gamma.default"));
			} else if (val == 100) {
				return Options.genericValueLabel(caption, Component.translatable("options.gamma.max"));
			} else {
				return Options.genericValueLabel(caption, val);
			}
		};
	}

	@Inject(method = "load", at = @At("TAIL"))
	private void grimdepth$migrateDefaultGamma(CallbackInfo ci) {
		if (!GrimdepthConfig.INSTANCE.lighting.gammaMigratedTo75) {
			if (this.gamma.get() == 0.5D) {
				this.gamma.set(0.75D);
			}
			GrimdepthConfig.INSTANCE.lighting.gammaMigratedTo75 = true;
			GrimdepthConfig.save();
		}
	}
}
