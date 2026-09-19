package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.BackstepHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {

	@Inject(
			method = "releaseUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;"
			)
	)
	private void grimdepth$onTridentThrow(ItemStack stack, Level level, LivingEntity entity, int timeLeft, CallbackInfoReturnable<Boolean> cir) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		BackstepHelper.tryTrigger(serverLevel, entity, stack);
	}
}
