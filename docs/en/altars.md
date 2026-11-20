# Altars

An altar is a poseable statue. It either belongs to a deity (right-clicking it
opens that deity's favor GUI and accepts offerings) or is purely decorative.

Location: `data/<namespace>/deity/altar/<name>.json`
Registry: `deity:altar` — entry ID `namespace:name`

## Fields

| Field | Type | Default | Description |
| --- | --- | --- | --- |
| `enabled` | boolean | `true` | Reserved; currently unused. |
| `name` | string | empty | Deity ID (e.g. `"greek:athena"`) or a custom display name. Strings that parse as a resource location are treated as deities. |
| `female` | boolean | `false` | Use the female model. Overridden to `true` when the linked deity's `gender` is `female`. |
| `slim` | boolean | `false` | Use slim arms. |
| `light` | integer 0–15 | `0` | Emits this light level while placed (an invisible light block is placed above the altar). |
| `items` | object | empty | Equipment and locks, see below. |
| `material` | resource location | `minecraft:stone` | Reserved for future use. |
| `pose` | pose object | walking pose | Part rotations in **radians**. |
| `pose_locked` | boolean | `false` | Prevent players from changing the pose in the GUI. |

## Altar items

```json
"items": {
  "head": "minecraft:turtle_helmet",
  "chest": { "id": "minecraft:diamond_chestplate", "Count": 1 },
  "legs": "minecraft:iron_leggings",
  "feet": "minecraft:golden_boots",
  "mainhand": "minecraft:trident",
  "offhand": "minecraft:shield",
  "block": "minecraft:prismarine_brick_slab",
  "armor_locked": false,
  "hands_locked": true,
  "block_locked": true
}
```

| Field | Type | Description |
| --- | --- | --- |
| `head`, `chest`, `legs`, `feet`, `mainhand`, `offhand` | item ID or item stack | Starting equipment. |
| `block` | block ID | Block placed under the statue (usually a slab). |
| `armor_locked` | boolean | Armor cannot be changed and is not dropped when the altar breaks. |
| `hands_locked` | boolean | Main/offhand items cannot be changed and are not dropped. |
| `block_locked` | boolean | Base block cannot be changed and is not dropped. |

If every slot is locked and the pose is locked, interacting opens the statue
instead of the altar GUI.

## Pose

The pose is an NBT compound. Each part has `x`, `y`, `z` rotations in radians;
missing parts default to zero. If `pose` is omitted entirely, the walking pose is
used.

Parts: `head`, `body`, `left_arm`, `right_arm`, `left_leg`, `right_leg`, `root`.

```json
"pose": {
  "head": { "x": 0.174533 },
  "left_arm": { "x": -1.047198, "z": -0.174533 },
  "right_arm": { "x": -1.675516 },
  "left_leg": { "x": 0.174533 },
  "right_leg": { "x": -0.174533 }
}
```

## Recipes

Use a custom serializer so the crafted altar remembers its properties:

```json
{
  "type": "rpggods:altar_shapeless",
  "ingredients": [
    { "item": "rpggods:altar" },
    { "tag": "forge:glass_panes" }
  ],
  "result": { "item": "rpggods:altar" },
  "altar": "greek:aphrodite"
}
```

* `rpggods:altar_shaped` follows the normal shaped recipe format (`pattern`,
  `key`, `result`) plus the required `altar` field.
* `rpggods:altar_shapeless` follows the shapeless format (`ingredients`,
  `result`) plus `altar`.
* `altar` is the altar ID (`namespace:name` of the altar JSON), not the deity ID.

## Placing and editing

* `/give @s rpggods:altar{altar:"greek:athena"}` gives the matching altar item.
* The RPG Gods creative tab contains one altar item for every registered altar.
* Right-click with an item to offer it; right-click empty-handed to open the
  favor GUI (deity altar) or the altar editor (decorative altar).
* The altar editor lets you change pose, model, equipment and the custom name;
  changing the name to a player name attempts to use that player's skin.

## Textures

An altar's entity texture is resolved as
`assets/<deity namespace>/textures/altar/<deity path>.png`. For a decorative
altar the default Steve/Alex texture is used (or the player skin from the custom
name). The item texture can be customised with the `rpggods:altar` item model
and its `index` property.
