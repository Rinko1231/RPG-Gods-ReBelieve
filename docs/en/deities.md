# Deities

A deity is the central object of a data pack. It owns favor bounds, an icon in
the favor GUI, and the locked/unlocked state at the start of a world.

Location: `data/<namespace>/deity/deity/<name>.json`
Registry: `deity:deity` — entry ID `namespace:name`

## Fields

| Field | Type | Default | Description |
| --- | --- | --- | --- |
| `name` | resource location | **required** | Deity ID. Must equal the entry ID. |
| `icon` | item ID or item stack | empty | Icon shown in the favor GUI (for example `"minecraft:trident"` or `{"id":"minecraft:diamond_helmet","Count":1}`). |
| `gender` | `male`, `female`, `other` | `male` | Used for pronouns/unlock messages. An altar's `female` flag still controls the statue model. |
| `unlocked` | boolean | `true` | Whether players start with this deity unlocked. |
| `enabled` | boolean | `true` | When `false`, the deity grants no favor or perks. |
| `minlevel` | integer | `-10` | Minimum favor level. |
| `maxlevel` | integer | `10` | Maximum favor level. |

Favor is stored per player (or per team/global, see
[configuration](favor.md#configuration)). The `minlevel`/`maxlevel` values define
the valid level range and the bounds shown in the GUI.

## Example

`data/greek/deity/deity/athena.json`:

```json
{
  "name": "greek:athena",
  "unlocked": false,
  "icon": {
    "id": "minecraft:shield",
    "Count": 1
  },
  "gender": "female",
  "minlevel": -10,
  "maxlevel": 10
}
```

## Related files

A deity usually has an [altar](altars.md), and its offerings, sacrifices and
perks live in subfolders named after the deity path:

```
data/greek/deity/deity/athena.json
data/greek/deity/altar/athena.json
data/greek/deity/offering/athena/apple.json
data/greek/deity/sacrifice/athena/zombie.json
data/greek/deity/perk/athena/blessing.json
```
