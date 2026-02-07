# ChronoSMP (Fabric 1.21.x)

ChronoSMP is a Fabric SMP mod that lets server operators assign one unique custom ability to each player. Abilities are activated by right-clicking a personalized **Sigil** item and are fully configurable through `config/chronosmp.yml`.

## Features
- **15 unique abilities** with custom sigil items and right-click activation.
- **One ability per player** enforced via `/ability` commands.
- **Configurable cooldowns, durations, and ranges** via `config/chronosmp.yml`.
- **Persistent player data** stored in `config/chronosmp-abilities.json`.

## Commands
- `/ability give <player> <ability>` – Assign ability (OP only)
- `/ability remove <player>` – Remove ability (OP only)
- `/ability list` – List all abilities (OP only)
- `/ability reload` – Reload config (OP only)
- `/abilities` – View your current ability

## Installing
1. Install Fabric Loader for Minecraft **1.21.x**.
2. Place the built `chronosmp` jar in your server's `mods/` folder.
3. Start the server once to generate the default config at `config/chronosmp.yml`.

## Custom Item Textures (Resource Pack)
The mod registers item models named after each ability (`chronosmp:shadow_walker`, etc.). To add textures:

1. Create a resource pack (or use an existing server pack).
2. Add textures to: `assets/chronosmp/textures/item/`
   - Example: `assets/chronosmp/textures/item/shadow_walker.png`
3. Ensure the model JSONs exist (these are included in the mod):
   - `assets/chronosmp/models/item/shadow_walker.json`
4. Zip your pack and set it as the server resource pack or distribute it to players.

If you want custom models via `custom_model_data`, you can also override a base item (like a stick) in your pack and point it to the ChronoSMP textures.

## Ability Summary
- **Shadow Walker** – Invisibility + night vision + smoke particles
- **Pyromaniac** – Fire resistance + ignite targets on hit
- **Titan's Might** – Strength + resistance + ground slam
- **Void Bender** – Teleport to where you're looking
- **Storm Caller** – Summon lightning strikes
- **Nature's Ally** – Summon loyal wolf pack
- **Bloodlust** – Lifesteal healing on attacks
- **Phase Shift** – Speed + invisibility (ethereal movement)
- **Time Warp** – Slow enemies in area
- **Dragon Heart** – Temporary flight ability
- **Alchemist** – Create random powerful potions
- **Midas Touch** – Bonus ore drops when mining
- **Berserker** – More damage at lower health
- **Necromancer** – Summon skeleton army
- **Frost Weaver** – Freeze enemies + create ice platforms

## Build
```bash
gradle build
```
