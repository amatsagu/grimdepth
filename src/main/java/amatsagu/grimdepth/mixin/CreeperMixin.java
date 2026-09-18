package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.CreeperDestroyLightGoal;
import amatsagu.grimdepth.common.GrimdepthConfig;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

	@Shadow private int maxSwell;
	@Shadow private int swell;

	@Inject(method = "setSwellDir", at = @At("HEAD"))
	private void grimdepth$randomizeFuse(int dir, CallbackInfo ci) {
		Creeper self = (Creeper) (Object) this;
		if (dir > 0 && this.swell == 0 && !self.level().isClientSide()) {
			if (self.getY() < GrimdepthConfig.INSTANCE.general.undergroundYLevel && !self.level().canSeeSky(self.blockPosition())) {
				int minFuse = GrimdepthConfig.INSTANCE.creepers.undergroundMinFuseTicks;
				int maxFuse = GrimdepthConfig.INSTANCE.creepers.undergroundMaxFuseTicks;
				if (maxFuse > minFuse) {
					this.maxSwell = minFuse + self.getRandom().nextInt(maxFuse - minFuse + 1);
				} else {
					this.maxSwell = minFuse;
				}
			}
		}
	}

	@Inject(method = "registerGoals", at = @At("TAIL"))
	private void grimdepth$addLightGoal(CallbackInfo ci) {
		Creeper self = (Creeper) (Object) this;
		self.getGoalSelector().addGoal(4, new CreeperDestroyLightGoal(self, 1.0));
	}
}
