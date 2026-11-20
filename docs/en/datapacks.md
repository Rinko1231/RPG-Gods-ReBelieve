# Data pack layout

RPG Gods registers five **datapack registries** (synced to clients
automatically, no custom packets):

| Registry key | Folder in the pack | Entry ID example |
| --- | --- | --- |
| `deity:deity` | `data/<ns>/deity/deity/<name>.json` | `my:athena` |
| `deity:altar` | `data/<ns>/deity/altar/<name>.json` | `my:athena_altar` |
| `deity:offering` | `data/<ns>/deity/offering/<deity>/<name>.json` | `my:athena/apple` |
| `deity:sacrifice` | `data/<ns>/deity/sacrifice/<deity>/<name>.json` | `my:athena/zombie` |
| `deity:perk` | `data/<ns>/deity/perk/<deity>/<name>.json` | `my:athena/blessing` |

The registry key has the form `<namespace>:<path>`; the folder is the key's
namespace and path (`deity/offering`). The entry ID is the file path relative to
that folder, without `.json`, prefixed with the pack namespace.

> **Naming rule:** offerings, sacrifices and perks are associated with a deity
> by their ID path. A perk at `data/my/deity/perk/athena/blessing.json` gets the
> ID `my:athena/blessing`, whose path contains `athena/`, so it belongs to
> `my:athena`. Keep the folder name equal to the deity path (without namespace).

## pack.mcmeta

```json
{
  "pack": {
    "pack_format": 15,
    "description": "My RPG Gods data pack"
  }
}
```

Put the pack in `saves/<world>/datapacks/<pack name>/` (or a zip), then run
`/reload`. Datapack registries are synced to connected clients automatically.

## Entry ID rules

* Deity: `name` inside the JSON **must** equal the entry ID
  (`data/my/deity/deity/athena.json` → `"name": "my:athena"`). Favor lookups use
  this value, while the registry key uses the file path; a mismatch breaks
  offerings, altars and perks for that deity.
* Altar: `name` is either a deity ID (`"my:athena"`) or a plain custom name
  (`"Guardian of the Gate"`). A string that parses as a resource location is
  treated as a deity.
* Offering/sacrifice/perk: the folder under the registry folder must be the
  deity path. `my:athena/apple` belongs to `my:athena`.

## Items, recipes and functions

* Items: `rpggods:altar` (statue), `rpggods:scroll` (opens the favor GUI),
  `rpggods:brazier` (automatic offering block).
* Altar item NBT: `/give @s rpggods:altar{altar:"my:athena_altar"}`. The mod's
  creative tab (`RPG Gods`) generates one altar item for every registered altar.
* Custom recipes: use recipe type `rpggods:altar_shaped` or
  `rpggods:altar_shapeless` and add an `"altar"` field with the altar ID. See
  [Altars](altars.md#recipes).
* Functions: the `favor` command is available to functions and works with the
  usual `function` perk action. Functions run with permission level 4 when
  triggered by a perk.

## Resource pack hints

* Deity display name: translation key `deity.<namespace>.<path>`
  (e.g. `deity.greek.athena`), optional subtitle key
  `deity.<namespace>.<path>.title`.
* Altar entity texture: `assets/<deity namespace>/textures/altar/<deity path>.png`
  (for `greek:athena`: `assets/greek/textures/altar/athena.png`).
* The altar item uses a generated model by default; `rpggods:altar` exposes an
  `index` item property (0..1) if you want to add model overrides for custom
  altar textures.

## Troubleshooting

* Check `logs/latest.log` for `Couldn't parse` / `Failed to parse` lines — they
  include the file that failed.
* `/favor list` shows every registered deity with its enabled/unlocked state.
* Use `/reload` after editing JSON; a world restart is not required.
* If the pack does not appear, enable it in **Options → Data Packs** (world
  creation/modification screen).
