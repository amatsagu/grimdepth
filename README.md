<div align="center">
  <h1>
    <img src="src/main/resources/assets/grimdepth/icon.png" alt="Grimdepth Logo" width="48" height="48" align="middle" />
    <sub>Grimdepth</sub>
  </h1>
  <h3>A modern Fabric mod that makes deep underground just bit more scary.</h3>
</div>

## About this mod
This mod was originally a datapack that I've created to slightly boost monsters deep underground, make them just a bit more scary to deal with - so even player in end game gear can find them challenging at times.

**Grimdepth** works mostly at `Y < 0` in overworld and attempts to blend well with Vanilla. It only adds 2 new enchants, everything else is still from base game. Almost everything can be configured in `grimdepth.json` that is generated in game files after first launch.

## List of features
- Adds 2 new enchants to make up for the increased difficulty:
  * `Armor Piercer`: Makes weapons ignore 10%/20%/30% of enemy's armor. It's a rare enchantment found in dungeons (mob spawner dungeon, trial chambers, underwater ruins/pyramids, desert pyramids, jungle temples) and very rarely traded by villagers (librarians). Works with any swords, axes, bows, crossbows, tridents, spears & maces. Attacks and projectiles also leave an ominous blue skull particle trail.
  * `Backstep`: Similarly to Armor Piercer, it's a rare enchant found in same dungeons and very rarely appears in village trades. It allows players to better avoid danger by gently pushing them backward and granting a short burst of movement speed after successfully releasing an arrow from a bow, firing from a crossbow, or throwing a trident. It includes safe-floor checks so you won't accidentally recoil into lava, fire, or off cliffs.
- Zombie Leaders are now more special. Grimdepth takes existing Vanilla mechanic and extends it:
  * All Zombie Leaders are now marked with death particles, making them easier to spot in the dark.
  * Zombie Leaders receive a slight bonus to movement speed & jump boost and are always granted an iron sword + 2 random pieces of iron/chainmail equipment.
  * They are more likely to spawn closer to bedrock layer in the overworld, but there's a limit of max 2 near the player in combat.
- In deep underground caves (starting from `Y < 0`), Skeletons and Zombies scale in danger the deeper you explore:
  * **Skeletons**: Skeletons will always use bows, but have an increasing chance to spawn with enchanted bows (Power or Armor Piercer). In deepslate caverns, enchanted skeletons are also guaranteed the **Backstep** enchantment — when they shoot their bow, they will actively leap backward away from the player if safe ground is available behind them.
  * **Zombies**: Zombies may be found with random pieces of equipment they had before turning into a monster (shovels, pickaxes, or swords), favoring primitive stone tools higher up and advanced iron tools deep down.
- **Underground Creepers**:
  * **Unpredictable Fuses**: Underground creepers can detonate noticeably faster than surface creepers, keeping encounters tense and unpredictable.
  * **Light Hunters**: Deep underground creepers actively seek out and detonate near light sources (torches, lanterns, glowstone, lit redstone lamps, copper bulbs) to plunge caverns back into darkness. Combat with players always takes priority, but unlit areas will quickly return to the shadows.
- **Deepslate Spiders & Cobweb Ambushes**:
  * All spiders spawning in deepslate (`Y <= 0`) are **smaller**, making them nimbler and harder to hit in tight cave passages.
  * When defeated, they trigger the **Weaving** effect, randomly scattering cobweb blocks around their death point.
  * **Cobweb Ambush**: Breaking any of these naturally spawned cobwebs in deepslate has a chance to immediately disturb another small, angry spider lurking in the webs to attack.
- **Atmospheric Darkness & Reworked Night Vision**:
  * **Atmospheric Cave Ambience**: Caves are calibrated to feel naturally darker and more atmospheric, with deeper shadows and richer contrast underground.
  * **Reworked Night Vision**: Replaces the artificial fullbright ambient wash and end-of-potion flashing shader with a natural, immersive vision boost that scales with effect level.
- **Nightmare Awareness**:
  * An ominous, harmful status effect marked by red raid omen skull particles.
  * **Mining Triggers**: Mining naturally generated deepslate ores deep underground has a chance to inflict Nightmare Awareness. The chance increases when mining in pitch darkness, and is significantly higher for rare veins like Deepslate Diamonds and Emeralds. Player-placed ores are tracked and will never trigger the effect.
  * **Dark Ambushes**: While afflicted (lasting for a short time, refreshing and increasing in level if triggered again), hostile monsters periodically manifest from nearby unlit darkness and immediately hunt down the player.
  * **Escalating Threat**: Higher effect levels summon deadlier creatures:
    - **Level 1**: Zombies, Skeletons, Spiders.
    - **Level 2**: Level 1 pool + Creepers, Husks.
    - **Level 3+**: Level 2 pool + Witches, Cave Spiders.
- **Compatibility & Recommended Mods**:
  * **[Penchant](https://github.com/ThePotatoArchivist/Penchant)** (Optional): Zero-dependency integration. When installed, Armor Piercer levels up through weapon durability use, and Backstep is treated as an instant max-level rare enchantment.
  * **[Let Me Despawn](https://modrinth.com/mod/lmd)** (Recommended): Recommended companion mod that allows underground mobs with equipped tools or picked-up items to despawn naturally and drop their loot, preventing cave entity buildup.

---

## Technical Details & Configuration
For technical architecture, mixin details, and the full `grimdepth.json` configuration reference, see [tech.md](tech.md).