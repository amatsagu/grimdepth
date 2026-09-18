package amatsagu.grimdepth.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class GrimdepthTags {
	public static final TagKey<Block> CREEPER_LIGHT_SOURCES = TagKey.create(
			Registries.BLOCK,
			Identifier.fromNamespaceAndPath("grimdepth", "creeper_light_sources")
	);
}
