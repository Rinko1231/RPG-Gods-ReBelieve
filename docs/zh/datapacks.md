# 数据包结构

RPG Gods 注册了五个**数据包注册表**（自动同步到客户端，不需要自定义网络包）：

| 注册表键 | 数据包中的目录 | 条目 ID 示例 |
| --- | --- | --- |
| `deity:deity` | `data/<命名空间>/deity/deity/<名称>.json` | `my:athena` |
| `deity:altar` | `data/<命名空间>/deity/altar/<名称>.json` | `my:athena_altar` |
| `deity:offering` | `data/<命名空间>/deity/offering/<神祇>/<名称>.json` | `my:athena/apple` |
| `deity:sacrifice` | `data/<命名空间>/deity/sacrifice/<神祇>/<名称>.json` | `my:athena/zombie` |
| `deity:perk` | `data/<命名空间>/deity/perk/<神祇>/<名称>.json` | `my:athena/blessing` |

注册表键的形式是 `<命名空间>:<路径>`，目录就是命名空间+路径（例如
`deity/offering`）。条目 ID 是文件相对该目录的路径（去掉 `.json`），再加上
数据包自己的命名空间。

> **命名规则：** 供品、献祭与恩赐通过 ID 路径关联神祇。
> `data/my/deity/perk/athena/blessing.json` 的 ID 是 `my:athena/blessing`，
> 路径中包含 `athena/`，因此属于 `my:athena`。子目录名必须等于神祇路径。

## pack.mcmeta

```json
{
  "pack": {
    "pack_format": 15,
    "description": "我的 RPG Gods 数据包"
  }
}
```

把数据包放在 `saves/<世界>/datapacks/<包名>/`（或压缩为 zip），执行
`/reload` 即可。数据包注册表会自动同步给客户端。

## 条目 ID 规则

* 神祇：JSON 中的 `name` **必须**等于条目 ID
  （`data/my/deity/deity/athena.json` → `"name": "my:athena"`）。
  恩宠查询使用该值，而注册表键使用文件路径；两者不一致会导致该神祇的献祭、
  神坛、恩赐无法正常工作。
* 神坛：`name` 可以是神祇 ID（`"my:athena"`）或普通自定义名称
  （`"守门人"`）。能解析为资源位置的字符串会被当作神祇。
* 供品/献祭/恩赐：注册表目录下的子目录必须是神祇路径。
  `my:athena/apple` 属于 `my:athena`。

## 物品、配方与函数

* 物品：`rpggods:altar`（神像）、`rpggods:scroll`（打开恩宠界面）、
  `rpggods:brazier`（自动献祭方块）。
* 神坛物品 NBT：`/give @s rpggods:altar{altar:"my:athena_altar"}`。
  创造模式物品栏 `RPG Gods` 会为每个已注册神坛生成一个物品。
* 自定义配方：使用 `rpggods:altar_shaped` 或 `rpggods:altar_shapeless`，
  并添加 `"altar"` 字段指定神坛 ID。见[神坛](altars.md#配方)。
* 函数：恩赐的 `function` 动作使用 MC 函数；`favor` 命令在函数中可用。
  由恩赐触发的函数会以权限等级 4 执行。

## 资源包提示

* 神祇名称翻译键：`deity.<命名空间>.<路径>`（例如
  `deity.greek.athena`），可选副标题键 `deity.<命名空间>.<路径>.title`。
* 神坛实体贴图：`assets/<神祇命名空间>/textures/altar/<神祇路径>.png`
  （`greek:athena` → `assets/greek/textures/altar/athena.png`）。
* 神坛物品默认使用生成模型；`rpggods:altar` 提供 `index`（0..1）物品属性，
  可用于添加自定义模型覆盖。

## 排查问题

* 查看 `logs/latest.log` 中的 `Couldn't parse` / `Failed to parse` 行，
  日志会指出出错的文件。
* `/favor list` 会列出所有已注册神祇及其启用/解锁状态。
* 修改 JSON 后执行 `/reload`，无需重启世界。
* 如果数据包没有出现，在**选项 → 数据包**中启用它。
