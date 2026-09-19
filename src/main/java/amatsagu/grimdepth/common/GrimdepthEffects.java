package amatsagu.grimdepth.common;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class GrimdepthEffects {
	public static class NightmareAwarenessEffect extends MobEffect {
		public NightmareAwarenessEffect() {
			super(MobEffectCategory.HARMFUL, 0x8a0303, ParticleTypes.RAID_OMEN);
			this.withSoundOnAdded(SoundEvents.APPLY_EFFECT_RAID_OMEN);
		}

		@Override
		public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
			return true;
		}

		@Override
		public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
			if (entity instanceof Player player && !player.isSpectator() && !player.isCreative()) {
				NightmareAwarenessHelper.tickNightmareAwareness(level, player, amplifier);
			}
			return true;
		}
	}

	public static final Holder<MobEffect> NIGHTMARE_AWARENESS = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			Identifier.fromNamespaceAndPath(Grimdepth.MOD_ID, "nightmare_awareness"),
			new NightmareAwarenessEffect()
	);

	public static void init() {
		// Triggers static initialization
	}
}
