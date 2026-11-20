# RPG Gods - Forge 1.20.1 port

This workspace is a community port of [skyjay1/RPG-Gods](https://github.com/skyjay1/RPG-Gods)
to Minecraft 1.20.1 / Forge 47.x. The original author started a 1.20.1 branch
(`main-1.20.1`) and left it mid-refactor; this port completes that branch so the
mod compiles, loads and keeps the data-pack driven design.

## Build / run

Requirements: JDK 17 (the project is configured for Java 17) and an internet
connection for the first Gradle run.

```bat
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.1
gradlew.bat build          :: builds build/libs/rpggods-20.1.0.0.jar
gradlew.bat runClient      :: dev client
gradlew.bat runServer      :: dev server (run/eula.txt must contain eula=true)
```

The Gradle wrapper is pinned to Gradle 8.8 and Forge is `1.20.1-47.2.20`
(any Forge 47.x works, update `forge_version` in `gradle.properties`).
JEI 15.2.0.27 is included as a compile/runtime dependency for the JEI plugin;
remove the two JEI lines from `build.gradle` if you do not want it.

## What was completed

* Migrated the 1.19 architecture to 1.20.1 datapack registries
  (`deity:deity`, `deity:altar`, `deity:offering`, `deity:sacrifice`,
  `deity:perk`) with automatic client sync, replacing the old JSON reload
  listeners and custom sync packets.
* Replaced all remaining references to the removed static maps
  (`RPGGods.DEITY_MAP`, `ALTAR_MAP`, `OFFERING_MAP`, `PERK_MAP`,
  `SACRIFICE_MAP`, `DEITY_HELPER`, `AFFINITY`) with registry lookups, including
  favor/commands/AI/items/structure processor.
* Populated and invalidated the `DeityContainer` cache on server start,
  datapack sync/reload, and client GUI open.
* Ported the Favor GUI (`FavorScreen`) to 1.20.1 `GuiGraphics` and the new data
  model, and ported `ScrollButton` to the 1.20.1 button API.
* Ported `AltarScreen`, `AltarEntity`, `AltarItem`, `ScrollItem` and the
  brazier/light block to registry access; restored the `female` altar property.
* Added a creative tab (`RPG Gods`) containing the Scroll, Brazier and one
  altar item per registered altar, so the mod is usable without commands.
* Fixed the loot modifier ids (`rpggods:crop_yield`), added startup logging of
  loaded datapack entries.
* Composite perk conditions (`and`/`or`/`not`) are now indexed under every
  nested condition codec, so perks that combine a trigger such as
  `rpggods:random_tick` with a state check such as `rpggods:location` actually run
  when the trigger fires.

## Data packs

The mod is still fully data-pack driven. Folders follow the registry keys, e.g.

```
data/<namespace>/deity/deity/<name>.json
data/<namespace>/deity/altar/<name>.json
data/<namespace>/deity/offering/<deity>/<name>.json
data/<namespace>/deity/sacrifice/<deity>/<name>.json
data/<namespace>/deity/perk/<deity>/<name>.json
```

A ready-to-use example lives in `examples/rpg-gods-example/` (copy it into
`run/world/datapacks/` or any world's `datapacks/` folder).

**Important:** the 1.20.1 branch changes the perk JSON format compared to the
old 1.19 wiki. Conditions/actions are now codec records with namespaced type ids
(`rpggods:random_tick`, `rpggods:favor`, ...), and a perk has one `condition`
object (use `and`/`or`/`not` for composites). See the example pack's README.

## Greek mythology data pack (ported)

The original `Datapack-19.1.0` release from the upstream repository has been
converted to the 1.20.1 schema and is installed in
`run/saves/新的世界/datapacks/greek/` (the old `rpgtest` pack was removed). A
copy of the converted pack is kept in `datapacks/greek/` for reuse.

Conversion summary (script: `reference/greek/convert.py`):

* 13 deities, 13 altars, 197 offerings, 107 sacrifices, 217 perks, 13 recipes
  and 6 functions.
* `condition` lists became a single `rpggods:and` composite; every old
  condition/action type was mapped to the 1.20.1 codec records
  (`rpggods:location`, `rpggods:item`, `rpggods:potion`, `rpggods:favor`, ...).
* Old `data` fields were expanded into the new fields (`item` predicates,
  `deity`, `effect`, `slot`/`percent`, `amount`, `id`/`nbt` for summons, ...).
* Deity `gender` is filled in from each matching altar's `female` flag.
* `weather clear 0` / `weather thunder 0` became `1` because 1.20.1 rejects a
  zero duration.
* `LocationCondition` now accepts biome tags/holder sets, because the Greek pack
  uses conditions such as `#minecraft:is_ocean`.

The Greek resource pack (`assets/greek`) is merged into the mod so deity names,
altar entity textures and the `greek` item models are available without enabling
a separate resource pack. A `zh_cn` file was added with Chinese names for the
Greek deities and their titles.

Verified on a copy of the save: the server reports
`Loaded 13 deities, 14 altars, 197 offerings, 107 sacrifices, 217 perks`,
`/favor list` lists all 13 deities, and `summon rpggods:altar` with
`Deity:"greek:athena"` works.

## Chinese translation status

There is no RPG Gods project in the CFPA
[Minecraft-Mod-Language-Package](https://github.com/CFPAOrg/Minecraft-Mod-Language-Package/tree/main/projects),
and the upstream repository only ships `en_us.json`. No Chinese translation was
found to import, so this port ships its own `zh_cn` files:

* `assets/rpggods/lang/zh_cn.json` — complete translation of the mod UI,
  commands, perk conditions/actions and tooltips (same key set as `en_us`).
* `assets/greek/lang/zh_cn.json` — Chinese names and titles for the Greek
  deities.

## Documentation

A bilingual 1.20.1 data pack wiki lives in [`docs/`](docs/README.md):

* English: `docs/en/` — overview, pack layout, deities, altars,
  offerings/sacrifices, perks, favor/commands/config, 1.19 migration.
* 中文：`docs/zh/` — 同样的页面。

## Verification status

* `gradlew build` is clean and produces `build/libs/rpggods-20.1.0.0.jar`
  with the access transformer, mixin config, refmap and the merged Greek assets.
* A dedicated server starts with the mod (`Done`, no mod errors), the test pack
  reports `Loaded 1 deities, 2 altars, 1 offerings, 1 sacrifices, 1 perks`, and
  `/favor list` plus altar summoning work.
* The converted Greek pack reports
  `Loaded 13 deities, 14 altars, 197 offerings, 107 sacrifices, 217 perks` and
  all 13 deities are visible through `/favor list`.
* `runClient` (rendering/GUI visuals) is left to the user as agreed; the client
  code compiles against 1.20.1 and the mixin target was verified against the
  mapped 1.20.1 `LivingEntityRenderer`.
