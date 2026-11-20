# Migrating a 1.19 data pack

The file layout, registry folders and most top-level fields are unchanged. The
big difference is the **perk schema**: actions and conditions are now codec
records with namespaced IDs, and a perk has a single `condition` object.

## Checklist

1. Set `pack.mcmeta` `"pack_format": 15`.
2. Make every `name` in `deity/deity/*.json` equal the entry ID
   (`data/<ns>/deity/deity/<name>.json` → `<ns>:<name>`).
3. Convert perk conditions/actions (see below).
4. Convert sacrifice `condition` to a single condition object.
5. `weather ... 0` in functions is invalid in 1.20.1 — use `1`.
6. Optionally add `gender` to deities (otherwise `male`).

## Condition changes

Old conditions used `{"type": "night"}` / `{"type": "biome", "data": "..."}`.
New conditions are namespaced and use named fields:

| Old 1.19 | New 1.20.1 |
| --- | --- |
| `{"type":"random_tick"}` | `{"type":"rpggods:random_tick"}` |
| `{"type":"day"}` | `{"type":"rpggods:location","day":true}` |
| `{"type":"night"}` | `{"type":"rpggods:location","night":true}` |
| `{"type":"biome","data":"minecraft:plains"}` | `{"type":"rpggods:location","biome":"minecraft:plains"}` |
| `{"type":"biome","data":"#minecraft:is_ocean"}` | `{"type":"rpggods:location","biome":"#minecraft:is_ocean"}` |
| `{"type":"dimension","data":"minecraft:overworld"}` | `{"type":"rpggods:location","dimension":"minecraft:overworld"}` |
| `{"type":"structure","data":"minecraft:fortress"}` | `{"type":"rpggods:location","structure":"minecraft:fortress"}` |
| `{"type":"mainhand_item","data":"minecraft:apple"}` | `{"type":"rpggods:item","item":{"items":"minecraft:apple"}}` |
| `{"type":"mainhand_item","data":"#forge:crops"}` | `{"type":"rpggods:item","item":{"tag":"forge:crops"}}` |
| `{"type":"ritual","data":"minecraft:poppy"}` | `{"type":"rpggods:ritual","item":{"items":"minecraft:poppy"}}` |
| `{"type":"effect_start","data":"minecraft:poison"}` | `{"type":"rpggods:effect_start","effect":"minecraft:poison"}` |
| `{"type":"enter_combat"}` | `{"type":"rpggods:combat_start"}` |
| `{"type":"entity_hurt_player","data":"minecraft:arrow"}` | `{"type":"rpggods:entity_hurt_player","entity":"minecraft:arrow"}` |
| `{"type":"player_hurt_entity","data":"minecraft:player"}` | `{"type":"rpggods:player_hurt_entity","entity":"minecraft:player"}` |
| `{"type":"entity_killed_player","data":"..."}` | `{"type":"rpggods:entity_kill_player","entity":"..."}` |
| `{"type":"player_killed_entity","data":"..."}` | `{"type":"rpggods:player_kill_entity","entity":"..."}` |
| `{"type":"player_ride_entity","data":"..."}` | `{"type":"rpggods:player_ride_entity","entity":"..."}` |
| `{"type":"player_interact_entity","data":"..."}` | `{"type":"rpggods:player_interact_entity","entity":"..."}` |
| `{"type":"player_interact_block","data":"..."}` | `{"type":"rpggods:use_block","block":"..."}` |
| `{"type":"player_crouching"}` | `{"type":"rpggods:pose","pose":"crouching"}` |
| `{"type":"patron","data":"greek:athena"}` | `{"type":"rpggods:patron","deity":"greek:athena"}` |
| `{"type":"unlocked","data":"greek:athena"}` | `{"type":"rpggods:unlocked","deity":"greek:athena"}` |
| `{"type":"near_altar","data":"greek:athena"}` | `{"type":"rpggods:near_altar","altar":"greek:athena"}` |
| `{"type":"level_up","data":"greek:athena"}` | `{"type":"rpggods:level_change","direction":"level_up","deity":"greek:athena"}` |
| `{"type":"level_down",...}` | `{"type":"rpggods:level_change","direction":"level_down",...}` |

A perk's old `condition` **list** is an AND: a single entry becomes that
condition, multiple entries become:

```json
"condition": { "type": "rpggods:and", "value": [ ... ] }
```

## Action changes

| Old 1.19 | New 1.20.1 |
| --- | --- |
| `{"type":"function","id":"ns:fn","data":"tooltip.key"}` | `{"type":"rpggods:function","function":"ns:fn","translation_key":"tooltip.key"}` |
| `{"type":"potion","tag":{...}}` | `{"type":"rpggods:potion","effect":{"id":"minecraft:regeneration","duration":200,"amplifier":0}}` |
| `{"type":"arrow_effect","tag":{...}}` | `{"type":"rpggods:arrow_effect","effect":{...}}` |
| `{"type":"summon","tag":{"id":"minecraft:zombie"},"multiplier":8}` | `{"type":"rpggods:summon","id":"minecraft:zombie","distance":8}` |
| `{"type":"item","item":{...}}` | `{"type":"rpggods:item","item":{...}}` |
| `{"type":"favor","id":"greek:zeus","favor":10}` | `{"type":"rpggods:favor","deity":"greek:zeus","amount":10}` |
| `{"type":"affinity","affinity":{"type":"tame","entity":"minecraft:bee"}}` | `{"type":"rpggods:affinity","entity":"minecraft:bee","affinity":"tame"}` |
| `{"type":"arrow_damage","multiplier":0.8}` | `{"type":"rpggods:arrow_damage","multiplier":0.8}` |
| `{"type":"arrow_count","multiplier":3}` | `{"type":"rpggods:arrow_count","amount":3}` |
| `{"type":"offspring","multiplier":2}` | `{"type":"rpggods:offspring","amount":2}` |
| `{"type":"crop_growth","multiplier":2}` | `{"type":"rpggods:crop_age","count":1,"amount":2}` |
| `{"type":"crop_harvest","multiplier":1}` | `{"type":"rpggods:crop_yield","amount":1}` |
| `{"type":"autosmelt"}` | `{"type":"rpggods:autosmelt"}` |
| `{"type":"unsmelt"}` | `{"type":"rpggods:unsmelt"}` |
| `{"type":"special_price","multiplier":-2}` | `{"type":"rpggods:merchant_price","amount":-2}` |
| `{"type":"durability","data":"chest","multiplier":0.1}` | `{"type":"rpggods:durability","slot":"chest","percent":0.1}` |
| `{"type":"damage","multiplier":0.9}` | `{"type":"rpggods:damage","multiplier":0.9}` |
| `{"type":"patron","patron":{...}}` | `{"type":"rpggods:patron","patron":{...}}` |
| `{"type":"unlock","id":"greek:ares"}` | `{"type":"rpggods:unlock","deity":"greek:ares"}` |
| `{"type":"add_decay","id":"greek:zeus","multiplier":-0.2}` | `{"type":"rpggods:favor_decay","deity":"greek:zeus","amount":-0.2}` |
| `{"type":"xp","multiplier":0.9}` | `{"type":"rpggods:xp","multiplier":0.9}` |

Any action may still have `"hidden": true`.

## Other notes

* `offering` / `sacrifice` field names are unchanged; sacrifice `condition` is
  now a single condition object instead of a list.
* `altar` JSON keeps `female`, `slim`, `items`, `pose` and `pose_locked`.
* `deity` JSON gained `gender` (`male`/`female`/`other`); `name` is still
  required.
* Datapack registries are synced automatically, so old custom sync packets and
  JSON reload listeners no longer exist. `/reload` is enough.
* `weather clear 0` / `weather thunder 0` must become `1` in 1.20.1.
