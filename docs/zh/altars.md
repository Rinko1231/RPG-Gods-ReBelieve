# 神坛

神坛是可摆姿势的神像。属于某位神祇的神坛可以接受供品并打开该神祇的恩宠界面；
也可以做成纯装饰。

位置：`data/<命名空间>/deity/altar/<名称>.json`
注册表：`deity:altar` — 条目 ID `命名空间:名称`

## 字段

| 字段 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `enabled` | 布尔 | `true` | 预留，目前未使用。 |
| `name` | 字符串 | 空 | 神祇 ID（如 `"greek:athena"`）或自定义显示名称；能解析为资源位置的字符串会被当作神祇。 |
| `female` | 布尔 | `false` | 使用女性模型；若神祇 `gender` 为 `female` 则强制为 `true`。 |
| `slim` | 布尔 | `false` | 使用细手臂模型。 |
| `small` | 布尔 | `false` | 使用小个子（幼年体型）模型。 |
| `light` | 整数 0–15 | `0` | 放置时发出的光照等级（会在神坛上方放置隐形光源）。 |
| `items` | 对象 | 空 | 装备与锁定设置，见下文。 |
| `material` | 资源位置 | `minecraft:stone` | 预留。 |
| `pose` | 姿势对象 | 行走姿势 | 各部位旋转，单位为**弧度**。 |
| `pose_locked` | 布尔 | `false` | 禁止玩家在界面中修改姿势。 |

## 神坛物品

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

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `head`、`chest`、`legs`、`feet`、`mainhand`、`offhand` | 物品 ID 或物品堆 | 初始装备。 |
| `block` | 方块 ID | 神像脚下的底座方块（通常是台阶）。 |
| `armor_locked` | 布尔 | 护甲不可更改，破坏神坛时也不掉落。 |
| `hands_locked` | 布尔 | 主/副手物品不可更改且不掉落。 |
| `block_locked` | 布尔 | 底座不可更改且不掉落。 |

如果所有槽位与姿势都锁定，右键将打开雕像编辑界面而不是神坛界面。

## 姿势

姿势是一个 NBT 复合标签。每个部位有 `x`、`y`、`z` 旋转（弧度），未定义的部分
默认为 0。若完全省略 `pose`，则使用行走姿势。

部位：`head`、`body`、`left_arm`、`right_arm`、`left_leg`、`right_leg`、`root`。

```json
"pose": {
  "head": { "x": 0.174533 },
  "left_arm": { "x": -1.047198, "z": -0.174533 },
  "right_arm": { "x": -1.675516 },
  "left_leg": { "x": 0.174533 },
  "right_leg": { "x": -0.174533 }
}
```

## 配方

使用自定义序列化器，让合成出的神坛保留属性：

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

* `rpggods:altar_shaped`：普通有序配方格式（`pattern`、`key`、`result`），
  外加必填的 `altar` 字段。
* `rpggods:altar_shapeless`：普通无序配方格式（`ingredients`、`result`），
  外加 `altar` 字段。
* `altar` 填神坛 ID（神坛 JSON 的 `命名空间:名称`），不是神祇 ID。

## 放置与编辑

* `/give @s rpggods:altar{altar:"greek:athena"}` 获得对应神坛物品。
* RPG Gods 创造模式物品栏会为每个已注册神坛生成物品。
* 手持物品右键献上；空手右键打开恩宠界面（神祇神坛）或神坛编辑界面（装饰神坛）。
* 编辑界面可调整姿势、模型、装备与自定义名称；把名称改成玩家名会尝试使用该玩家的皮肤。

## 贴图

神坛实体贴图路径为
`assets/<神祇命名空间>/textures/altar/<神祇路径>.png`。
装饰神坛使用默认Steve/Alex 贴图（或自定义名称对应的玩家皮肤）。
物品贴图可通过`rpggods:altar` 物品模型及其 `index` 属性自定义。
