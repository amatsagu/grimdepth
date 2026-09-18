package amatsagu.grimdepth.common;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GrimdepthEffects {
	public static class NightmareAwarenessEffect extends MobEffect {
		public NightmareAwarenessEffect() {
			super(MobEffectCategory.HARMFUL, 0x8a0303, ParticleTypes.RAID_OMEN);
			this.withSoundOnAdded(SoundEvents.APPLY_EFFECT_RAID_OMEN);
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
