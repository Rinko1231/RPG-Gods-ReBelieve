# RPG Gods example data pack (Forge 1.20.1)

Drop this folder (or a zip of its contents) into `saves/<world>/datapacks/` and run
`/reload`, or copy it into `run/world/datapacks/` for the Gradle `runServer`/`runClient`
world. It registers one deity (`example:athena`), one altar, one offering, one
sacrifice and one perk.

## Layout

```
data/example/deity/
├── deity/athena.json                  # the deity itself
├── altar/athena_altar.json            # altar model/inventory, "name" = deity id
├── offering/athena/apple.json         # item -> favor (folder name = deity path)
├── sacrifice/athena/zombie.json       # entity -> favor (folder name = deity path)
└── perk/athena/test_perk.json         # perk (folder name = deity path)
```

The mod uses datapack registries named `deity:deity`, `deity:altar`,
`deity:offering`, `deity:sacrifice` and `deity:perk`, so the folder layout is
`data/<your namespace>/deity/<registry>/...` and the entry id is
`<your namespace>:<file name>`.

## 1.20.1 schema notes

The 1.20.1 codebase moved perk actions/conditions to codec-driven records, so
the JSON differs slightly from the old 1.19 wiki:

* A perk has a **single** `condition` object (not a list). Use the `and`, `or`
  and `not` conditions to combine conditions.
* Condition/action `type` ids are namespaced: `rpggods:random_tick`,
  `rpggods:patron`, `rpggods:favor`, ... (the registry keys are listed in
  `RGRegistry.PerkConditionReg` / `RGRegistry.PerkActionReg`).
* `rpggods:patron` takes a `deity` field instead of `data`; see
  `PatronCondition.CODEC`. Every action/condition class documents its own
  fields in its `CODEC`.
* Altars still accept the old `female`, `slim`, `items`, `pose`, `pose_locked`
  and `name` fields.

Everything else (`deity`, `altar`, `offering`, `sacrifice`) keeps the same
fields as the original wiki.

## Quick in-game test

1. Load the pack, then run `/favor list` (requires permission level 2); the
   deity should appear.
2. `/give @s rpggods:altar{altar:"example:athena_altar"}` and place the statue.
3. Right-click the statue with an apple to gain favor, or open the Scroll
   (`/give @s rpggods:scroll`) to see the favor GUI.
