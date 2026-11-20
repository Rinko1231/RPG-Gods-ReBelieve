# 恩宠、命令与配置

## 恩宠点数与等级

恩宠以每位神祇的**点数**存储，等级由点数换算：

```
等级 = sign(恩宠) * floor((-100 + sqrt(10000 + 40 * |恩宠|)) / 20)
```

某等级所需的点数为：

```
点数(等级) = sign(等级) * 10 * |等级| * (|等级| + 10)
```

每位神祇通过 `minlevel`/`maxlevel` 定义范围，玩家无法超出。恩宠界面显示当前
点数、等级和下一级所需点数。

## 周期更新

每 `favor_update_rate` tick（默认 20）：

1. 以 `random_perk_chance` 的概率检查并运行所有条件为 `rpggods:random_tick`
   的恩赐。
2. 减少恩赐、供品、献祭的冷却。
3. 以 `favor_decay_rate` 的概率，按 `favor_decay_amount` 扣除恩宠（受等级的
   衰减率影响）。

`favor_mode` 决定恩宠归属：

| 模式 | 恩宠归属 |
| --- | --- |
| `player`（默认） | 每位玩家独立。 |
| `team` | 玩家所在的计分板队伍。 |
| `global` | 整个服务器共享一份恩宠。 |

队伍/全局恩宠保存在世界的 `RGSavedData` 中；玩家恩宠是附加在玩家身上的能力，
死亡后保留。

## 守护神

每位玩家最多有一位守护神（通过 `rpggods:patron` 动作或
`/favor set <目标> <神祇> patron` 设置）。更换守护神时：

* 旧守护神的衰减率与恩赐加成都重置为 0；
* 新守护神的 `penalty` 会加到旧守护神的恩宠上；
* 新守护神的 `decay` 与 `perk_chance` 会加到最后选择的 `FavorLevel` 上。

有守护神的玩家会获得其衰减率与恩赐几率修正；恩宠界面会标出守护神。

## 亲和

`rpggods:affinity` 动作为生物设置 AI 行为：

| 亲和 | 效果 |
| --- | --- |
| `passive` | 不攻击满足恩赐范围的玩家。 |
| `hostile` | 攻击满足范围的玩家。 |
| `flee` | 躲避满足范围的玩家。 |
| `tame` | 满足动作条件时，右键可驯服。 |

亲和行为由所有包含 `rpggods:affinity` 动作的恩赐共同决定；检查时会比较恩赐的
`range` 与玩家恩宠。

## 配置

配置文件位于 `config/rpggods-common.toml`。

| 键 | 默认值 | 说明 |
| --- | --- | --- |
| `favor.favor_enabled` | `true` | 恩宠、供品、献祭与恩赐的总开关。 |
| `favor.favor_mode` | `player` | `player`、`team` 或 `global`。 |
| `favor.favor_update_rate` | `20` | 恩宠更新间隔（tick），影响恩赐、衰减与冷却。 |
| `favor.random_perk_chance` | `0.34` | 每次更新检查 `random_tick` 恩赐的概率。 |
| `favor.favor_decay_rate` | `0.006` | 每次更新扣除恩宠的概率。 |
| `favor.favor_decay_amount` | `1` | 每次扣除的点数。 |
| `favor.perk_feedback` | `true` | 恩赐触发时是否提示玩家。 |
| `favor.perk_feedback_chat` | `false` | 用聊天栏而非动作栏提示。 |
| `brazier.brazier_enabled` | `true` | 启用火盆自动献祭。 |
| `brazier.brazier_cooldown` | `8` | 火盆献祭间隔（tick）。 |
| `brazier.brazier_range` | `2` | 搜索神坛的半径（方块）。 |
| `affinity.flee_enabled` | `true` | 启用 `flee` 亲和 AI。 |
| `affinity.hostile_enabled` | `true` | 启用 `hostile` 亲和 AI。 |
| `affinity.passive_enabled` | `true` | 启用 `passive` 亲和 AI。 |
| `affinity.tameable_enabled` | `true` | 启用 `tame` 亲和。 |
| `sitting.sitting_mobs` | 列表 | 使用坐姿的生物 ID。 |

## 命令

所有命令需要权限等级 2。`<目标>` 为一个或多个玩家；`<神祇>` 是神祇 ID，例如
`greek:athena`。

| 命令 | 说明 |
| --- | --- |
| `/favor list` | 列出已注册神祇及其启用/解锁状态。 |
| `/favor add <目标> <神祇> <数量> [points\|levels\|perk_chance\|decay]` | 增加点数（默认等级）或修正值。 |
| `/favor set <目标> <神祇> <数量> [points\|levels\|perk_chance\|decay]` | 设置点数/等级/修正值。 |
| `/favor set <目标> <神祇> patron` | 设置守护神。 |
| `/favor set <目标> <神祇> unlocked <true\|false>` | 解锁或锁定神祇。 |
| `/favor set <目标> enabled <true\|false>` | 启用/禁用恩宠。 |
| `/favor query <目标> <神祇> [points\|levels\|unlocked\|perk_chance\|decay]` | 查询数值。 |
| `/favor query <目标> enabled` | 查询恩宠是否启用。 |
| `/favor query <目标> patron` | 查询守护神。 |
| `/favor cap <目标> <神祇> <最小> <最大> [points\|levels]` | 设置等级/点数范围。 |
| `/favor reset <目标> [<神祇>\|patron\|cooldown]` | 重置恩宠、某神祇恩宠、守护神或冷却。 |

`/favor` 同时注册了重定向，命令方块/函数可使用更短的形式。恩赐触发的函数以
权限等级 4 执行。

## 界面

使用 `rpggods:scroll`，或空手右键神祇神坛打开恩宠界面。

* **概览** — 恩宠点数、等级、守护神与神坛预览。
* **供品** — 可接受的物品与交易，含等级要求与函数提示。
* **献祭** — 可击杀的生物、恩宠与条件。
* **恩赐** — 按解锁等级排列的可拖动网格，含提示、概率与冷却覆盖层。
