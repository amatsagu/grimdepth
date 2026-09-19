package amatsagu.grimdepth.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

	@Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
	private void grimdepth$onAddEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof Bat bat && bat.entityTags().contains("grimdepth:replace_with_vex")) {
			ServerLevel self = (ServerLevel) (Object) this;
			Vex vex = EntityTypes.VEX.create(self, EntitySpawnReason.NATURAL);
			if (vex != null) {
				vex.copyPosition(bat);
				vex.finalizeSpawn(self, self.getCurrentDifficultyAt(bat.blockPosition()), EntitySpawnReason.NATURAL, null);
				self.addFreshEntity(vex);
				bat.discard();
				cir.setReturnValue(false);
				return;
			}
		}

		if (entity instanceof Spider spider && spider.getClass() == Spider.class && spider.entityTags().contains("grimdepth:replace_with_cave_spider")) {
			ServerLevel self = (ServerLevel) (Object) this;
			CaveSpider caveSpider = EntityTypes.CAVE_SPIDER.create(self, EntitySpawnReason.NATURAL);
			if (caveSpider != null) {
				caveSpider.copyPosition(spider);
				caveSpider.finalizeSpawn(self, self.getCurrentDifficultyAt(spider.blockPosition()), EntitySpawnReason.NATURAL, null);
				self.addFreshEntity(caveSpider);
				spider.discard();
				cir.setReturnValue(false);
				return;
			}
		}
	}
}
