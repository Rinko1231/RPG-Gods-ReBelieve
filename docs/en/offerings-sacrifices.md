# Offerings & sacrifices

Offerings are items given at an altar; sacrifices are mobs killed by the player.
Both add (or remove) favor and can run a function. Both support cooldowns and
limited uses.

## Offerings

Location: `data/<namespace>/deity/offering/<deity>/<name>.json`
Registry: `deity:offering` — entry ID `namespace:<deity>/<name>`

| Field | Type | Default | Description |
| --- | --- | --- | --- |
| `item` | item ID or item stack | empty | The item to accept. If empty, the offering does nothing. |
| `favor` | integer | `0` | Favor added (negative removes favor). |
| `trade` | item ID or item stack | none | Item given to the player. If it is the same item as `item`, NBT/enchantments/durability are copied from the offered stack. |
| `minlevel` | integer | `-2147483648` | Minimum favor level required. |
| `maxlevel` | integer | `2147483647` | Maximum favor level allowed. |
| `maxuses` | integer | `16` | Uses before the cooldown is applied. |
| `restocks` | integer | `-1` | Restocks before the offering locks forever. `-1` = infinite. |
| `cooldown` | integer (ticks) | `12000` | Cooldown after `maxuses` is reached. |
| `function` | resource location | none | MC function run when the offering is accepted. |
| `function_text` | string | none | Translation key used as the tooltip for the function. |

An offering is only registered when it has a non-zero `favor`, a `trade`, or a
`function`. Its game ID must be `<deity path>/<name>` (folder rule).

Example — `data/greek/deity/offering/aphrodite/apple.json`:

```json
{ "item": "minecraft:apple", "favor": 4 }
```

Example with a trade and function:

```json
{
  "item": "minecraft:golden_apple",
  "trade": { "id": "minecraft:enchanted_golden_apple", "Count": 1 },
  "favor": 10,
  "maxuses": 1,
  "cooldown": 24000,
  "function": "greek:golden_apple_athena",
  "function_text": "perk.function.golden_apple_athena"
}
```

## Sacrifices

Location: `data/<namespace>/deity/sacrifice/<deity>/<name>.json`
Registry: `deity:sacrifice` — entry ID `namespace:<deity>/<name>`

| Field | Type | Default | Description |
| --- | --- | --- | --- |
| `entity` | entity type ID | **required** | The mob that must be killed. |
| `favor` | integer | `0` | Favor added (negative removes favor). |
| `maxuses` | integer | `16` | Kills before the cooldown is applied. |
| `restocks` | integer | `-1` | Restocks before locking forever. `-1` = infinite. |
| `cooldown` | integer (ticks) | `12000` | Cooldown after `maxuses`. |
| `condition` | condition object | `{"type":"rpggods:true"}` | Extra condition (a single condition; use `and`/`or`). |
| `function` | resource location | none | MC function run on the sacrifice. |
| `translation_key` | string | none | Tooltip key for the function. |

A sacrifice is only registered when it has a non-zero `favor` or a `function`.

Example — `data/greek/deity/sacrifice/ares/zombie.json`:

```json
{
  "entity": "minecraft:zombie",
  "favor": 2,
  "maxuses": 16
}
```

## Cooldowns and uses

`Cooldown` tracks uses, restocks and a tick cooldown. After `maxuses` uses the
offering/sacrifice enters cooldown for `cooldown` ticks and increments
`restocks`; when `restocks` reaches its maximum (`restocks >= 0`) the entry is
locked. The player's cooldown state is saved in the favor capability.

## Common conditions

Any [perk condition](perks.md#conditions) can be used as a sacrifice condition.
Typical choices are `rpggods:patron`, `rpggods:unlocked`, `rpggods:weather` or
`rpggods:location`.
