package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthSpawner;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieMixin {

	@Inject(method = "finalizeSpawn", at = @At("TAIL"))
	private void grimdepth$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
		GrimdepthSpawner.applyZombieSpawn((Zombie) (Object) this, level);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void grimdepth$tickLeader(CallbackInfo ci) {
		Zombie self = (Zombie) (Object) this;
		if (!self.level().isClientSide() && self.entityTags().contains("grimdepth:zombie_leader")) {
			if ((self.tickCount + self.getId()) % 8 == 0) {
				if (self.level() instanceof ServerLevel serverLevel) {
					double radius = GrimdepthConfig.INSTANCE.zombies.leaders.particleProximityRadius;
					if (serverLevel.hasNearbyAlivePlayer(self.getX(), self.getY(), self.getZ(), radius)) {
						serverLevel.sendParticles(
								ParticleTypes.RAID_OMEN,
								self.getRandomX(0.5),
								self.getRandomY() + 0.2,
								self.getRandomZ(0.5),
								1,
								0.0, 0.02, 0.0,
								0.01
						);
					}
				}
			}
		}
	}
}
