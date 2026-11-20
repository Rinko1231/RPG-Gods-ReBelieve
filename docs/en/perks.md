# Perks

A perk is a **condition + action(s)** rule. When the condition matches and the
player's favor level is inside `range`, the actions run (subject to `chance` and
`cooldown`).

Location: `data/<namespace>/deity/perk/<deity>/<name>.json`
Registry: `deity:perk` — entry ID `namespace:<deity>/<name>`

A perk is ignored unless it has a non-empty `range` and at least one action.

## Fields

| Field | Type | Default | Description |
| --- | --- | --- | --- |
| `icon` | perk icon | empty | Icon shown in the favor GUI. |
| `range` | favor range | empty | `{"deity": "...", "minlevel": n, "maxlevel": n}`. Required for the perk to load. |
| `condition` | condition | `{"type":"rpggods:true"}` | A **single** condition. Combine with `and`/`or`/`not`. |
| `action` | action or list of actions | empty | What the perk does. |
| `chance` | float 0–1 | `1.0` | Chance to run when the condition matches. |
| `cooldown_category` | string | first action's type ID | Perks sharing a category share a cooldown. |
| `cooldown` | long (ticks) | `600` | Cooldown after a successful run (with ±25% random variation). |
| `positive` | boolean | `range.minlevel >= 0` | Controls the green/red feedback message. |

### Perk icon

```json
"icon": { "item": "minecraft:golden_apple", "color": 14277081, "hidden": false, "fancy": false }
```

| Field | Type | Default |
| --- | --- | --- |
| `item` | item ID or item stack | empty |
| `color` | decimal RGB color | `0` (white) |
| `hidden` | boolean | `false` — hide the perk from the GUI |
| `fancy` | boolean | `false` — use the fancy icon border |

### Favor range

```json
"range": { "deity": "greek:athena", "minlevel": 4, "maxlevel": 10 }
```

`minlevel`/`maxlevel` are inclusive and may be negative. They are swapped if
`minlevel > maxlevel` by the codec.

## Conditions

Conditions are **codec records** with a namespaced `type`. All of them can be
nested in `and` / `or` / `not`.

```json
"condition": {
  "type": "rpggods:and",
  "value": [
    { "type": "rpggods:random_tick" },
    { "type": "rpggods:location", "night": true }
  ]
}
```

> **Trigger conditions:** only conditions backed by a game event —
> `random_tick`, the entity hurt/kill/interact conditions, `combat_start`,
> `use_block`, `effect_start` and `ritual` — cause a perk to be evaluated.
> State conditions such as `location`, `time`, `weather`, `pose`, `item`,
> `patron`, `unlocked` and `near_altar` should be combined with a trigger,
> usually `random_tick`. `level_change` is registered but not currently
> dispatched by any event.

### Condition reference

| Type ID | Fields | Meaning |
| --- | --- | --- |
| `rpggods:true` | — | Always true. |
| `rpggods:false` | — | Always false. |
| `rpggods:not` | `value`: condition | Negates a condition. |
| `rpggods:and` | `value`: condition or list | All children must match. |
| `rpggods:or` | `value`: condition or list | Any child may match. |
| `rpggods:random_tick` | — | Runs on the periodic favor update (every `favor_update_rate` ticks). |
| `rpggods:time` | `range`: int bounds, `period`?: long | Matches `dayTime % period` (or `dayTime`). |
| `rpggods:weather` | `raining`?: bool, `thundering`?: bool | Matches current weather. |
| `rpggods:location` | see below | Biome / dimension / structure / time of day / light / position / block. |
| `rpggods:item` | `item`: item predicate, `slot`?: equipment slot | Matches an equipped item (default mainhand). |
| `rpggods:patron` | `deity`: ID | Player's patron is this deity. |
| `rpggods:unlocked` | `deity`: ID | The deity is unlocked for the player. |
| `rpggods:near_altar` | `altar`?: altar holder set, `distance`?: int bounds (0–4 default, max 64) | Player is near a matching altar. |
| `rpggods:pose` | `pose`: pose ID or list | Player pose (`standing`, `crouching`, `sitting`, `sleeping`, `swimming`, `fall_flying`, `dying`, `spin_attack`). |
| `rpggods:entity_hurt_player` | `entity`: entity holder set | Player was hurt by one of these entities. |
| `rpggods:player_hurt_entity` | `entity`: entity holder set | Player hurt one of these entities. |
| `rpggods:entity_kill_player` | `entity`: entity holder set | Player was killed by one of these entities. |
| `rpggods:player_kill_entity` | `entity`: entity holder set | Player killed one of these entities. |
| `rpggods:player_ride_entity` | `entity`: entity holder set | Player is riding one of these entities. |
| `rpggods:player_interact_entity` | `entity`: entity holder set | Player right-clicked one of these entities. |
| `rpggods:use_block` | `block`?: block holder set | Player right-clicked a matching block. |
| `rpggods:effect_start` | `effect`: mob effect ID | Player gained this effect. |
| `rpggods:combat_start` | `entity`?: entity holder set | Player entered combat (optionally with one of these entities). |
| `rpggods:ritual` | `item`: item predicate | The item burned in front of the altar matches. |
| `rpggods:level_change` | `direction`: `level_up`/`level_down`, `deity`?: ID | Player's favor level changed. |
| `rpggods:spell_cast` | `spell`?: spell ID | A spell was cast (requires Iron's Spellbooks). |
| `rpggods:spell_damage` | `spell`?: spell ID, `min_amount`?: int, `max_amount`?: int | A spell dealt damage; amount is the pre-armor damage. |
| `rpggods:spell_heal` | `min_amount`?: int, `max_amount`?: int | A spell healed; amount is the healed amount. |

#### `rpggods:location`

| Field | Type | Meaning |
| --- | --- | --- |
| `position` | `{x?,y?,z?}` double bounds | Exact/min/max position (`{"min":0,"max":100}` allowed). |
| `offset` | `[x, y, z]` | Position offset. |
| `day` / `night` | boolean | Time of day. |
| `biome` | biome holder set | Biome ID, list, or tag (`"#minecraft:is_ocean"`). |
| `structure` | structure ID | Structure at the player position. |
| `dimension` | dimension ID | Current dimension. |
| `smokey` | boolean | Campfire smoke at the position. |
| `light` | int bounds | Light level. |
| `block` | block predicate | Block at the position. |

Holder sets accept a single ID (`"minecraft:zombie"`), a list
(`["minecraft:zombie","minecraft:skeleton"]`), or a tag
(`"#minecraft:undead"`).

#### Item predicate

Used by `rpggods:item` and `rpggods:ritual`:

```json
"item": { "items": "minecraft:apple" }
"item": { "tag": "forge:crops" }
"item": { "items": ["minecraft:apple","minecraft:golden_apple"], "count": {"min": 1} }
```

Fields: `items` (ID or list), `tag` (tag ID **without** `#`), `count`,
`durability`, `enchantments`, `stored_enchantments`, `potion`, `nbt` (SNBT
string).

#### Spell conditions

`rpggods:spell_cast`, `rpggods:spell_damage` and `rpggods:spell_heal` are fired by
the optional Iron's Spellbooks integration. They do nothing when Iron's
Spellbooks is not installed.

```json
"condition": { "type": "rpggods:spell_cast", "spell": "irons_spellbooks:firebolt" }
"condition": { "type": "rpggods:spell_damage", "spell": "irons_spellbooks:icicle", "min_amount": 5, "max_amount": 9 }
"condition": { "type": "rpggods:spell_damage", "min_amount": 10 }
"condition": { "type": "rpggods:spell_heal", "min_amount": 4 }
```

* `spell` is optional and limits the condition to one spell id.
* `min_amount` / `max_amount` are optional and compare against the event amount
  (spell damage or healing, in half hearts). Damage uses the pre-armor amount
  reported by Iron's Spellbooks' damage event.
* The perks are ordinary data pack perks, so they show up in the favor GUI's
  **Spells** tab with their condition and action tooltips. A `rpggods:favor`
  action is what actually grants favor.
* Cooldowns are controlled by the perk's `cooldown` / `cooldown_category`
  fields. Set `"cooldown": 0` for unlimited testing; use one category per event
  type so a cast does not block damage/heal perks.
* There is no `maxuses`-style cap for spell events; use the perk cooldown (or a
  `chance` below `1.0`) as a throttle.
* Per-hit thresholds are supported. A cumulative “every X total damage” design
  would need a persistent counter and is not built in.

## Actions

Every action object may contain `"hidden": true` to hide it in the perk tooltip.

| Type ID | Fields | Meaning |
| --- | --- | --- |
| `rpggods:function` | `function`: ID, `translation_key`?: string | Run an MC function. |
| `rpggods:potion` | `effect`: effect object, `target`?: `self`/`enemy`/`area`, `radius`?: float | Apply a potion effect. |
| `rpggods:summon` | `id`: entity type, `nbt`?: string, `distance`?: int 0–64 | Summon an entity near the player. |
| `rpggods:item` | `item`: item ID or stack | Give an item. |
| `rpggods:favor` | `deity`: ID, `amount`: int provider, `type`?: `points`/`levels` | Change favor. |
| `rpggods:favor_decay` | `deity`: ID, `amount`: float | Add to the favor decay rate. |
| `rpggods:affinity` | `entity`: entity holder set, `affinity`: `passive`/`hostile`/`flee`/`tame` | Assign AI behavior toward the player. |
| `rpggods:attribute` | `attribute`: attribute ID, `amount`: double, `operation`?: `add_value`/`add_multiplied_base`/`add_multiplied_total` | Passive attribute modifier while the perk's favor range is met. |
| `rpggods:arrow_damage` | `multiplier`: float | Multiply arrow damage. |
| `rpggods:arrow_effect` | `effect`: effect object | Add an effect to the arrow. |
| `rpggods:arrow_count` | `amount`: int provider | Spawn extra arrows. |
| `rpggods:offspring` | `entity`?: holder set, `amount`: int provider | Multiply breeding offspring. |
| `rpggods:crop_age` | `count`: int provider, `amount`: int provider | Grow/wither nearby crops. |
| `rpggods:crop_yield` | `amount`: int provider | Extra crop drops (loot modifier). |
| `rpggods:autosmelt` | — | Smelt drops (loot modifier). |
| `rpggods:unsmelt` | — | Un-smelt drops (loot modifier). |
| `rpggods:merchant_price` | `entity`?: holder set, `amount`: int | Modify villager trade prices. |
| `rpggods:durability` | `slot`: slot or list, `percent`: float −1..1 | Change item durability. |
| `rpggods:damage` | `multiplier`: float | Multiply damage taken/dealt (uses the hurt event). |
| `rpggods:patron` | `patron`: `{deity?, penalty?, decay?, perk_chance?}` | Set or clear the player's patron. |
| `rpggods:unlock` | `deity`: ID | Unlock a deity. |
| `rpggods:xp` | `multiplier`: float | Multiply experience orb value. |

### Attribute action

```json
"action": [
  {
    "type": "rpggods:attribute",
    "attribute": "irons_spellbooks:fire_spell_power",
    "amount": 0.3,
    "operation": "add_multiplied_base"
  }
]
```

Unlike the other actions this is not a one-shot effect. The modifier is applied
while the player's favor level is inside the perk's `range` and removed when the
level leaves the range (or the deity becomes locked/disabled). It is
re-evaluated on favor level changes, player login, respawn, dimension change and
`/reload`.

`attribute` is a plain resource location. If the mod that owns the attribute is
not installed the data pack still loads and the action simply does nothing.
`operation` defaults to `add_value`; the integer ids `0`/`1`/`2` are accepted as
well. Values are raw attribute values: for percentage-style attributes such as
Iron's Spellbooks spell power (base `1.0`), `0.3` means +30%.

### Effect object

```json
"effect": {
  "id": "minecraft:regeneration",
  "duration": 200,
  "amplifier": 1,
  "ambient": false,
  "visible": true,
  "show_icon": true
}
```

`duration` and `amplifier` accept an integer or an IntProvider object.

### IntProvider

Anywhere an “int provider” is accepted you can use:

```json
"amount": 5
"amount": { "type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 5 }
```

### Patron object

```json
"patron": {
  "deity": "greek:athena",
  "penalty": -1000,
  "decay": -0.5,
  "perk_chance": 0.15
}
```

`penalty` is added to the previous patron's favor, `decay` modifies the favor
decay rate, and `perk_chance` modifies the perk chance bonus.

## Examples

Random-tick blessing:

```json
{
  "icon": { "item": "minecraft:golden_apple" },
  "range": { "deity": "greek:athena", "minlevel": 0, "maxlevel": 10 },
  "condition": { "type": "rpggods:random_tick" },
  "action": [
    { "type": "rpggods:potion", "effect": { "id": "minecraft:regeneration", "duration": 100, "amplifier": 0 } }
  ],
  "chance": 0.1,
  "cooldown": 1200
}
```

Ritual that assigns a patron after burning an item:

```json
{
  "range": { "deity": "greek:aphrodite", "minlevel": 3, "maxlevel": 10 },
  "condition": { "type": "rpggods:ritual", "item": { "items": "minecraft:poppy" } },
  "action": [
    { "type": "rpggods:patron", "patron": { "deity": "greek:aphrodite", "penalty": -1000 } }
  ]
}
```

Ritual perks are only checked when the player throws a burning item in front of
an altar; all other actions run through the [favor update](favor.md).
