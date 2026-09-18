package amatsagu.grimdepth.mixin;

import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwellGoal.class)
public abstract class SwellGoalMixin {
	@Shadow @Final private Creeper creeper;

	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	private void grimdepth$onlySwellWithTarget(CallbackInfoReturnable<Boolean> cir) {
		if (this.creeper.getTarget() == null) {
			cir.setReturnValue(false);
		}
	}
}
