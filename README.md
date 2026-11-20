![banner](https://github.com/skyjay1/img/blob/master/rpggods/rpggods-logo.png)

[![](http://cf.way2muchnoise.eu/full_rpg-gods_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/rpg-gods)
[![](http://cf.way2muchnoise.eu/versions/Minecraft_rpg-gods_all.svg)](https://www.curseforge.com/minecraft/mc-mods/rpg-gods/files)

# RPG Gods
Minecraft mod that adds data-driven gods, goddesses, and worship systems. These features were originally used in the [Greek Fantasy](https://www.curseforge.com/minecraft/mc-mods/greek-fantasy/files) mod and are adapted with permission.

# ReBelieve — Forge 1.20.1 port
This fork (`ReBelieve-1.20.1` branch) finishes the abandoned Forge 1.20.1 port of
RPG Gods and keeps the data pack format playable on Minecraft 1.20.1 / Forge 47.x.

* **Wiki / data pack documentation**: https://github.com/Rinko1231/RPG-Gods-ReBelieve/wiki
* **Greek mythology data pack (1.20.1)**: [`datapacks/greek`](datapacks/greek) — download: https://github.com/Rinko1231/RPG-Gods-ReBelieve/releases/tag/greek-1.20.1
* **Bundled example pack**: [`examples/rpg-gods-example`](examples/rpg-gods-example)
* **Documentation source (EN / 中文)**: [`docs/en`](docs/en) and [`docs/zh`](docs/zh)
* **Porting notes**: [`PORTING.md`](PORTING.md)

## Building
Requires JDK 17 and the Gradle wrapper:

```
./gradlew build
```

The mod jar is written to `build/libs/`. The dev environment targets Forge
`1.20.1-47.4.0` (the mod itself declares `[47,)`).

## Optional integrations
* **Iron's Spellbooks** — `rpggods:attribute` perk actions can grant any Forge
  attribute, and spell casting / spell damage / spell healing grant favor with
  the test deity `iss:iron`. The integration classes are only loaded when
  Iron's Spellbooks is installed, and data packs that mention missing attributes
  still load (`build.gradle` adds the optional mods as `compileOnly` +
  `runtimeOnly`; pass `-PnoIrons` to run the dev client/server without them).
* Test data pack: [`datapacks/iss-test`](datapacks/iss-test).

## Notes
* Data pack registry keys still use the `deity:` namespace, while perk
  condition/action `type` ids use `rpggods:`.
* The Greek data pack ships as a normal data pack and can be dropped into
  `saves/<world>/datapacks/` or `world/datapacks/`.
* Simplified Chinese translations for the mod UI and the Greek deity names are
  included (`zh_cn`).

# Credits
Select textures created by [Daniel Eddeland](https://opengameart.org/content/lpc-farming-tilesets-magic-animations-and-ui-elements).
