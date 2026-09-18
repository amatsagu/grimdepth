package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.BackstepHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {

	@Inject(method = "shoot", at = @At("TAIL"))
	private void grimdepth$onShoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectiles, float speed, float inaccuracy, boolean isCrit, LivingEntity target, CallbackInfo ci) {
		BackstepHelper.tryTrigger(level, shooter, weapon);
	}
}
