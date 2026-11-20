# Overview & quick start

RPG Gods is a data-driven deity, favor and perk system. A data pack defines
**deities**, the **altars** used to contact them, the **offerings** and
**sacrifices** that raise or lower favor, and the **perks** granted when favor
conditions are met.

## Requirements

* Minecraft 1.20.1, Forge 47.x
* A data pack with `pack_format: 15` (plus an optional resource pack for names
  and textures)

## Core concepts

| Concept | What it does |
| --- | --- |
| **Deity** | A named entity (for example `greek:athena`) with favor level bounds, an icon, and locked/unlocked state. |
| **Favor** | Per-player (or per-team/global) points and levels with each deity. Favor unlocks perks, offerings and trades. |
| **Altar** | A placeable statue. Right-clicking it with an offering item gives favor; interacting empty-handed opens the favor GUI. |
| **Offering** | An item accepted at an altar, optionally with a trade result, cooldown, required favor level and an MC function. |
| **Sacrifice** | A mob killed by the player that grants favor, optionally gated by a condition and cooldown. |
| **Perk** | A condition + action(s) combination. When the condition matches and the favor range is satisfied, the action runs. |
| **Patron** | The deity a player is currently devoted to; can modify favor decay and perk chance. |

## Minimal data pack

```
my_pack/
├── pack.mcmeta
└── data/
    └── my/
        ├── deity/
        │   ├── deity/athena.json
        │   ├── altar/athena_altar.json
        │   ├── offering/athena/apple.json
        │   └── perk/athena/blessing.json
        └── ...
```

`pack.mcmeta`:

```json
{
  "pack": { "pack_format": 15, "description": "My gods" }
}
```

`data/my/deity/deity/athena.json`:

```json
{
  "name": "my:athena",
  "icon": "minecraft:trident",
  "unlocked": true,
  "minlevel": -10,
  "maxlevel": 10
}
```

`data/my/deity/altar/athena_altar.json`:

```json
{
  "name": "my:athena",
  "female": true,
  "items": {
    "mainhand": "minecraft:trident",
    "block": "minecraft:quartz_slab"
  }
}
```

`data/my/deity/offering/athena/apple.json`:

```json
{ "item": "minecraft:apple", "favor": 4 }
```

`data/my/deity/perk/athena/blessing.json`:

```json
{
  "icon": { "item": "minecraft:golden_apple" },
  "range": { "deity": "my:athena", "minlevel": 0, "maxlevel": 10 },
  "condition": { "type": "rpggods:random_tick" },
  "action": [
    { "type": "rpggods:favor", "deity": "my:athena", "amount": 1 }
  ],
  "chance": 0.25,
  "cooldown": 600
}
```

Drop the pack into `saves/<world>/datapacks/`, then run `/reload`. Verify with
`/favor list` (permission level 2). Place the altar with
`/give @s rpggods:altar{altar:"my:athena_altar"}`, or use the RPG Gods creative
tab which contains one altar item for every registered altar.

Next: [data pack layout](datapacks.md).
