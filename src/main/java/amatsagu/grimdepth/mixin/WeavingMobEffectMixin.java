package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.SpiderCobwebHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.WeavingMobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeavingMobEffect.class)
public abstract class WeavingMobEffectMixin {

	@Inject(method = "onMobRemoved", at = @At("HEAD"))
	private void grimdepth$onMobRemoved(ServerLevel level, LivingEntity entity, int amplifier, Entity.RemovalReason reason, CallbackInfo ci) {
		if (reason == Entity.RemovalReason.KILLED && entity instanceof Spider && entity.entityTags().contains("grimdepth:deepslate_spider")) {
			if (!level.getGameRules().get(GameRules.MOB_GRIEFING)) {
				((WeavingMobEffect) (Object) this).spawnCobwebsRandomlyAround(level, entity.getRandom(), entity.blockPosition());
			}
		}
	}

	@Redirect(
			method = "spawnCobwebsRandomlyAround",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
			)
	)
	private boolean grimdepth$trackCobwebPlacement(ServerLevel level, BlockPos pos, BlockState state) {
		boolean placed = level.setBlockAndUpdate(pos, state);
		if (placed && pos.getY() <= GrimdepthConfig.INSTANCE.general.deepslateYLevel) {
			SpiderCobwebHelper.trackCobweb(level, pos);
		}
		return placed;
	}
}
