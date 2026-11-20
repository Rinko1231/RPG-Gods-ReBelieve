# 从 1.19 数据包迁移

目录结构、注册表目录以及大多数顶层字段保持不变。最大的区别是**恩赐格式**：
动作与条件现在是带命名空间 ID 的 codec 记录，并且恩赐只有一个 `condition` 对象。

## 检查清单

1. 把 `pack.mcmeta` 的 `"pack_format"` 改为 `15`。
2. 确保 `deity/deity/*.json` 中的 `name` 等于条目 ID
   （`data/<命名空间>/deity/deity/<名称>.json` → `<命名空间>:<名称>`）。
3. 转换恩赐的条件/动作（见下表）。
4. 把献祭的 `condition` 改成单个条件对象。
5. 1.20.1 中函数里的 `weather ... 0` 无效，请改成 `1`。
6. 可选：为神祇添加 `gender`（默认 `male`）。

## 条件变化

旧条件使用 `{"type": "night"}` / `{"type": "biome", "data": "..."}`；新条件带
命名空间并使用命名字段：

| 旧 1.19 | 新 1.20.1 |
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

恩赐旧的 `condition` **列表**表示 AND：单条则直接使用，多条则转换为：

```json
"condition": { "type": "rpggods:and", "value": [ ... ] }
```

## 动作变化

| 旧 1.19 | 新 1.20.1 |
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

任何动作仍可带有 `"hidden": true`。

## 其他说明

* `offering` / `sacrifice` 字段名不变；献祭的 `condition` 现在是单个条件对象。
* `altar` JSON 保留 `female`、`slim`、`items`、`pose`、`pose_locked`。
* `deity` JSON 新增 `gender`（`male`/`female`/`other`）；`name` 仍为必填。
* 数据包注册表会自动同步，旧的自定义同步包与 JSON reload 监听器已不存在，
  只需 `/reload`。
* 1.20.1 中 `weather clear 0` / `weather thunder 0` 必须改为 `1`。
