# 概览与快速开始

RPG Gods 是一个由数据包驱动的神祇信仰系统。数据包定义**神祇**、用于沟通神祇的
**神坛**、提升/降低恩宠的**供品**与**献祭**，以及满足条件时触发的**恩赐**。

## 前置要求

* Minecraft 1.20.1，Forge 47.x
* `pack_format: 15` 的数据包（可选：提供名称与贴图的资源包）

## 核心概念

| 概念 | 作用 |
| --- | --- |
| **神祇（Deity）** | 例如 `greek:athena`，拥有恩宠等级上下限、图标、解锁状态。 |
| **恩宠（Favor）** | 玩家（或队伍/全局）对每位神祇的点数与等级；恩宠解锁供品、交易与恩赐。 |
| **神坛（Altar）** | 可摆放的神像；手持供品右键献上，空手右键打开恩宠界面。 |
| **供品（Offering）** | 神坛接受的物品，可附带交易产物、冷却、等级要求和 MC 函数。 |
| **献祭（Sacrifice）** | 玩家击杀指定生物获得恩宠，可附带条件与冷却。 |
| **恩赐（Perk）** | “条件 + 动作”规则；条件满足且恩宠等级在范围内时执行动作。 |
| **守护神（Patron）** | 玩家当前信奉的神祇，可影响恩宠衰减与恩赐几率。 |

## 最小数据包

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

`pack.mcmeta`：

```json
{
  "pack": { "pack_format": 15, "description": "我的神祇" }
}
```

`data/my/deity/deity/athena.json`：

```json
{
  "name": "my:athena",
  "icon": "minecraft:trident",
  "unlocked": true,
  "minlevel": -10,
  "maxlevel": 10
}
```

`data/my/deity/altar/athena_altar.json`：

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

`data/my/deity/offering/athena/apple.json`：

```json
{ "item": "minecraft:apple", "favor": 4 }
```

`data/my/deity/perk/athena/blessing.json`：

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

把数据包放进 `saves/<世界>/datapacks/`，然后执行 `/reload`。用 `/favor list`
（需要权限等级 2）检查是否加载。放置神坛：

```
/give @s rpggods:altar{altar:"my:athena_altar"}
```

也可以直接从 RPG Gods 创造模式物品栏中取用（每个已注册神坛都会生成一个物品）。

下一篇：[数据包结构](datapacks.md)。
