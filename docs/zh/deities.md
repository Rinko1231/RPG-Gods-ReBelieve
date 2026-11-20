# 神祇

神祇是数据包的核心。它决定恩宠等级范围、恩宠界面图标，以及世界初始的
解锁/启用状态。

位置：`data/<命名空间>/deity/deity/<名称>.json`
注册表：`deity:deity` — 条目 ID `命名空间:名称`

## 字段

| 字段 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `name` | 资源位置 | **必填** | 神祇 ID，必须等于条目 ID。 |
| `icon` | 物品 ID 或物品堆 | 空 | 恩宠界面图标（如 `"minecraft:trident"` 或 `{"id":"minecraft:diamond_helmet","Count":1}`）。 |
| `gender` | `male`、`female`、`other` | `male` | 用于称谓/解锁文本；神坛模型仍由神坛的 `female` 控制。 |
| `unlocked` | 布尔 | `true` | 玩家是否一开始就解锁该神祇。 |
| `enabled` | 布尔 | `true` | 为 `false` 时该神祇不提供恩宠或恩赐。 |
| `minlevel` | 整数 | `-10` | 最低恩宠等级。 |
| `maxlevel` | 整数 | `10` | 最高恩宠等级。 |

恩宠按玩家（或队伍/全局，见[配置](favor.md#配置)）存储。
`minlevel`/`maxlevel` 决定有效等级范围与界面中的上下限。

## 示例

`data/greek/deity/deity/athena.json`：

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

## 相关文件

一位神祇通常有对应的[神坛](altars.md)，供品、献祭与恩赐放在以神祇路径命名的
子目录：

```
data/greek/deity/deity/athena.json
data/greek/deity/altar/athena.json
data/greek/deity/offering/athena/apple.json
data/greek/deity/sacrifice/athena/zombie.json
data/greek/deity/perk/athena/blessing.json
```
