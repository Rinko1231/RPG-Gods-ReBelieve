# 恩赐

恩赐是“**条件 + 动作**”规则。条件满足且玩家与对应神祇的恩宠等级在 `range` 内时，
动作会执行（受 `chance` 与 `cooldown` 限制）。

位置：`data/<命名空间>/deity/perk/<神祇>/<名称>.json`
注册表：`deity:perk` — 条目 ID `命名空间:<神祇>/<名称>`

没有 `range` 或没有动作的恩赐会被忽略。

## 字段

| 字段 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `icon` | 恩赐图标 | 空 | 恩宠界面中显示的图标。 |
| `range` | 恩宠范围 | 空 | `{"deity": "...", "minlevel": n, "maxlevel": n}`，必填才能加载。 |
| `condition` | 条件 | `{"type":"rpggods:true"}` | **单个**条件，用 `and`/`or`/`not` 组合。 |
| `action` | 动作或动作列表 | 空 | 恩赐执行的内容。 |
| `chance` | 浮点数 0–1 | `1.0` | 条件满足时的触发概率。 |
| `cooldown_category` | 字符串 | 第一个动作的类型 ID | 同类别的恩赐共享冷却。 |
| `cooldown` | 长整数（tick） | `600` | 成功触发后的冷却（有 ±25% 随机浮动）。 |
| `positive` | 布尔 | `range.minlevel >= 0` | 控制绿色/红色反馈文本。 |

### 恩赐图标

```json
"icon": { "item": "minecraft:golden_apple", "color": 14277081, "hidden": false, "fancy": false }
```

| 字段 | 类型 | 默认值 |
| --- | --- | --- |
| `item` | 物品 ID 或物品堆 | 空 |
| `color` | 十进制 RGB 颜色 | `0`（白色） |
| `hidden` | 布尔 | `false`，从界面隐藏该恩赐 |
| `fancy` | 布尔 | `false`，使用华丽边框 |

### 恩宠范围

```json
"range": { "deity": "greek:athena", "minlevel": 4, "maxlevel": 10 }
```

`minlevel`/`maxlevel` 为闭区间，可为负数；若 `minlevel > maxlevel`，编解码器会
自动交换。

## 条件

条件是带命名空间 `type` 的 codec 记录，全部可以嵌套在 `and` / `or` / `not` 中。

```json
"condition": {
  "type": "rpggods:and",
  "value": [
    { "type": "rpggods:random_tick" },
    { "type": "rpggods:location", "night": true }
  ]
}
```

> **触发条件：** 只有由游戏事件驱动的条件才会让恩赐被检查：
> `random_tick`、实体受伤/击杀/交互类条件、`combat_start`、`use_block`、
> `effect_start` 与 `ritual`。状态类条件（`location`、`time`、`weather`、
> `pose`、`item`、`patron`、`unlocked`、`near_altar`）应与触发条件组合使用，
> 通常是 `random_tick`。`level_change` 已注册但当前没有事件调度。

### 条件参考

| 类型 ID | 字段 | 含义 |
| --- | --- | --- |
| `rpggods:true` | — | 永远为真。 |
| `rpggods:false` | — | 永远为假。 |
| `rpggods:not` | `value`：条件 | 取反。 |
| `rpggods:and` | `value`：条件或列表 | 全部满足。 |
| `rpggods:or` | `value`：条件或列表 | 任一满足。 |
| `rpggods:random_tick` | — | 在周期性恩宠更新时触发（每 `favor_update_rate` tick）。 |
| `rpggods:time` | `range`：整数范围，`period`?：长整数 | 匹配 `dayTime % period`（或 `dayTime`）。 |
| `rpggods:weather` | `raining`?：布尔，`thundering`?：布尔 | 匹配当前天气。 |
| `rpggods:location` | 见下文 | 生物群系/维度/结构/昼夜/光照/坐标/方块。 |
| `rpggods:item` | `item`：物品谓词，`slot`?：装备槽 | 匹配装备物品（默认主手）。 |
| `rpggods:patron` | `deity`：ID | 玩家守护神是该神祇。 |
| `rpggods:unlocked` | `deity`：ID | 玩家已解锁该神祇。 |
| `rpggods:near_altar` | `altar`?：神坛 holder set，`distance`?：整数范围（默认 0–4，最大 64） | 玩家靠近匹配的神坛。 |
| `rpggods:pose` | `pose`：姿势 ID 或列表 | 玩家姿势：`standing`、`crouching`、`sitting`、`sleeping`、`swimming`、`fall_flying`、`dying`、`spin_attack`。 |
| `rpggods:entity_hurt_player` | `entity`：实体 holder set | 玩家被这些实体伤害。 |
| `rpggods:player_hurt_entity` | `entity`：实体 holder set | 玩家伤害了这些实体。 |
| `rpggods:entity_kill_player` | `entity`：实体 holder set | 玩家被这些实体杀死。 |
| `rpggods:player_kill_entity` | `entity`：实体 holder set | 玩家击杀了这些实体。 |
| `rpggods:player_ride_entity` | `entity`：实体 holder set | 玩家骑乘这些实体。 |
| `rpggods:player_interact_entity` | `entity`：实体 holder set | 玩家右键点击这些实体。 |
| `rpggods:use_block` | `block`?：方块 holder set | 玩家右键点击匹配的方块。 |
| `rpggods:effect_start` | `effect`：状态效果 ID | 玩家获得该效果。 |
| `rpggods:combat_start` | `entity`?：实体 holder set | 玩家进入战斗（可限定对手）。 |
| `rpggods:ritual` | `item`：物品谓词 | 在神坛前焚烧的物品匹配。 |
| `rpggods:level_change` | `direction`：`level_up`/`level_down`，`deity`?：ID | 玩家恩宠等级变化。 |

#### `rpggods:location`

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| `position` | `{x?,y?,z?}` 双精度范围 | 精确/最小/最大坐标（可写 `{"min":0,"max":100}`）。 |
| `offset` | `[x, y, z]` | 坐标偏移。 |
| `day` / `night` | 布尔 | 昼夜。 |
| `biome` | 生物群系 holder set | 群系 ID、列表或标签（`"#minecraft:is_ocean"`）。 |
| `structure` | 结构 ID | 玩家所在结构。 |
| `dimension` | 维度 ID | 当前维度。 |
| `smokey` | 布尔 | 该位置是否有营火烟雾。 |
| `light` | 整数范围 | 光照等级。 |
| `block` | 方块谓词 | 该位置的方块。 |

Holder set 接受单个 ID（`"minecraft:zombie"`）、列表
（`["minecraft:zombie","minecraft:skeleton"]`）或标签
（`"#minecraft:undead"`）。

#### 物品谓词

用于 `rpggods:item` 与 `rpggods:ritual`：

```json
"item": { "items": "minecraft:apple" }
"item": { "tag": "forge:crops" }
"item": { "items": ["minecraft:apple","minecraft:golden_apple"], "count": {"min": 1} }
```

字段：`items`（ID 或列表）、`tag`（标签 ID，**不带** `#`）、`count`、
`durability`、`enchantments`、`stored_enchantments`、`potion`、`nbt`（SNBT
字符串）。

## 动作

每个动作对象都可以包含 `"hidden": true` 来隐藏提示文本。

| 类型 ID | 字段 | 含义 |
| --- | --- | --- |
| `rpggods:function` | `function`：ID，`translation_key`?：字符串 | 执行 MC 函数。 |
| `rpggods:potion` | `effect`：效果对象，`target`?：`self`/`enemy`/`area`，`radius`?：浮点 | 施加状态效果。 |
| `rpggods:summon` | `id`：实体类型，`nbt`?：字符串，`distance`?：0–64 | 在玩家附近召唤实体。 |
| `rpggods:item` | `item`：物品 ID 或物品堆 | 给予物品。 |
| `rpggods:favor` | `deity`：ID，`amount`：整数提供器，`type`?：`points`/`levels` | 改变恩宠。 |
| `rpggods:favor_decay` | `deity`：ID，`amount`：浮点 | 增加恩宠衰减率。 |
| `rpggods:affinity` | `entity`：实体 holder set，`affinity`：`passive`/`hostile`/`flee`/`tame` | 设置对玩家的 AI 行为。 |
| `rpggods:attribute` | `attribute`：属性 ID，`amount`：浮点，`operation`?：`add_value`/`add_multiplied_base`/`add_multiplied_total` | 在恩宠范围内持续提供属性修饰符。 |
| `rpggods:arrow_damage` | `multiplier`：浮点 | 箭矢伤害倍率。 |
| `rpggods:arrow_effect` | `effect`：效果对象 | 给箭矢添加效果。 |
| `rpggods:arrow_count` | `amount`：整数提供器 | 额外生成箭矢。 |
| `rpggods:offspring` | `entity`?：holder set，`amount`：整数提供器 | 繁殖后代数量。 |
| `rpggods:crop_age` | `count`：整数提供器，`amount`：整数提供器 | 使附近作物生长/枯萎。 |
| `rpggods:crop_yield` | `amount`：整数提供器 | 额外作物掉落（战利品修改器）。 |
| `rpggods:autosmelt` | — | 自动熔炼掉落（战利品修改器）。 |
| `rpggods:unsmelt` | — | 反熔炼掉落（战利品修改器）。 |
| `rpggods:merchant_price` | `entity`?：holder set，`amount`：整数 | 修改村民交易价格。 |
| `rpggods:durability` | `slot`：装备槽或列表，`percent`：浮点 −1..1 | 改变物品耐久。 |
| `rpggods:damage` | `multiplier`：浮点 | 伤害倍率（使用受伤事件）。 |
| `rpggods:patron` | `patron`：`{deity?, penalty?, decay?, perk_chance?}` | 设置或清除守护神。 |
| `rpggods:unlock` | `deity`：ID | 解锁神祇。 |
| `rpggods:xp` | `multiplier`：浮点 | 经验球价值倍率。 |

### 属性动作

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

与其他动作不同，它不是一次性效果：只要玩家的恩宠等级位于恩赐的 `range` 内，
修饰符就会一直存在；等级离开范围（或神祇被锁定/禁用）时移除。恩宠等级变化、
玩家登录、重生、切换维度以及 `/reload` 时都会重新结算。

`attribute` 就是普通的资源位置。即使拥有该属性的模组没有安装，数据包仍会正常
加载，这个动作只是不产生效果。`operation` 默认为 `add_value`，也接受整数
`0`/`1`/`2`。数值是属性的原始值：对于铁魔法法强这类百分比属性（基础值
`1.0`），`0.3` 表示 +30%。

### 效果对象

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

`duration` 与 `amplifier` 接受整数或整数提供器对象。

### 整数提供器

所有“整数提供器”位置都可以使用：

```json
"amount": 5
"amount": { "type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 5 }
```

### 守护神对象

```json
"patron": {
  "deity": "greek:athena",
  "penalty": -1000,
  "decay": -0.5,
  "perk_chance": 0.15
}
```

`penalty` 会加到旧守护神的恩宠上，`decay` 修改恩宠衰减率，`perk_chance` 修改
恩赐几率加成。

## 示例

随机触发祝福：

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

焚烧物品后指定守护神的仪式：

```json
{
  "range": { "deity": "greek:aphrodite", "minlevel": 3, "maxlevel": 10 },
  "condition": { "type": "rpggods:ritual", "item": { "items": "minecraft:poppy" } },
  "action": [
    { "type": "rpggods:patron", "patron": { "deity": "greek:aphrodite", "penalty": -1000 } }
  ]
}
```

仪式类恩赐只在玩家把燃烧的物品扔到神坛前时检查；其他动作则在
[恩宠更新](favor.md)时运行。
