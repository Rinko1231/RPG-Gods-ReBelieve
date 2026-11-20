# 供品与献祭

供品是在神坛献上的物品；献祭是玩家杀死的生物。两者都能增加（或减少）恩宠，并可
执行函数，同时支持冷却与使用次数限制。

## 供品

位置：`data/<命名空间>/deity/offering/<神祇>/<名称>.json`
注册表：`deity:offering` — 条目 ID `命名空间:<神祇>/<名称>`

| 字段 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `item` | 物品 ID 或物品堆 | 空 | 接受的物品；为空时供品不生效。 |
| `favor` | 整数 | `0` | 增加（负数则减少）的恩宠。 |
| `trade` | 物品 ID 或物品堆 | 无 | 给予玩家的物品。若与 `item` 相同，会复制所献物品的 NBT/附魔/耐久。 |
| `minlevel` | 整数 | `-2147483648` | 所需最低恩宠等级。 |
| `maxlevel` | 整数 | `2147483647` | 允许的最高恩宠等级。 |
| `maxuses` | 整数 | `16` | 触发冷却前的使用次数。 |
| `restocks` | 整数 | `-1` | 永久锁定前的补货次数，`-1` 表示无限。 |
| `cooldown` | 整数（tick） | `12000` | 达到 `maxuses` 后的冷却。 |
| `function` | 资源位置 | 无 | 接受供品时执行的 MC 函数。 |
| `function_text` | 字符串 | 无 | 函数提示使用的翻译键。 |

只有 `favor` 非零、存在 `trade` 或存在 `function` 的供品才会注册。
游戏内 ID 必须是 `<神祇路径>/<名称>`（目录规则）。

示例 — `data/greek/deity/offering/aphrodite/apple.json`：

```json
{ "item": "minecraft:apple", "favor": 4 }
```

带交易与函数的示例：

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

## 献祭

位置：`data/<命名空间>/deity/sacrifice/<神祇>/<名称>.json`
注册表：`deity:sacrifice` — 条目 ID `命名空间:<神祇>/<名称>`

| 字段 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `entity` | 实体类型 ID | **必填** | 必须击杀的生物。 |
| `favor` | 整数 | `0` | 增加（负数则减少）的恩宠。 |
| `maxuses` | 整数 | `16` | 触发冷却前的击杀次数。 |
| `restocks` | 整数 | `-1` | 永久锁定前的补货次数，`-1` 表示无限。 |
| `cooldown` | 整数（tick） | `12000` | 达到 `maxuses` 后的冷却。 |
| `condition` | 条件对象 | `{"type":"rpggods:true"}` | 附加条件（单个条件，可用 `and`/`or` 组合）。 |
| `function` | 资源位置 | 无 | 献祭时执行的 MC 函数。 |
| `translation_key` | 字符串 | 无 | 函数提示使用的翻译键。 |

只有 `favor` 非零或存在 `function` 的献祭才会注册。

示例 — `data/greek/deity/sacrifice/ares/zombie.json`：

```json
{
  "entity": "minecraft:zombie",
  "favor": 2,
  "maxuses": 16
}
```

## 冷却与次数

`Cooldown` 记录使用次数、补货次数与 tick 冷却。达到 `maxuses` 后，供品/献祭进入
`cooldown` tick 的冷却并增加 `restocks`；当 `restocks` 达到上限（`restocks >= 0`）
后永久锁定。玩家的冷却状态保存在恩宠能力数据中。

## 常用条件

任何[恩赐条件](perks.md#条件)都可以作为献祭条件。常用的是 `rpggods:patron`、
`rpggods:unlocked`、`rpggods:weather` 或 `rpggods:location`。
