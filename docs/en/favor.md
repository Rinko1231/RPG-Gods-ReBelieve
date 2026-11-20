# Favor, commands & configuration

## Favor points and levels

Favor is stored as **points** for each deity. Levels are derived from points:

```
level = sign(favor) * floor((-100 + sqrt(10000 + 40 * |favor|)) / 20)
```

and the points required for a level are:

```
points(level) = sign(level) * 10 * |level| * (|level| + 10)
```

Each deity defines `minlevel`/`maxlevel`; players cannot go outside that range.
The favor GUI shows the current points, current level, and the points needed for
the next level.

## Periodic updates

Every `favor_update_rate` ticks (default 20):

1. With `random_perk_chance` probability, all perks whose condition is
   `rpggods:random_tick` are evaluated and run.
2. Cooldowns for perks, offerings and sacrifices decrease.
3. With `favor_decay_rate` probability, favor is depleted by
   `favor_decay_amount` points (scaled by the level's decay rate).

`favor_mode` chooses who owns favor:

| Mode | Favor owner |
| --- | --- |
| `player` (default) | Every player separately. |
| `team` | The player's scoreboard team. |
| `global` | One shared favor for the whole server. |

Team/global favor is stored in the world's `RGSavedData`; player favor is a
capability attached to the player and survives death.

## Patron

A player can have one patron deity (`rpggods:patron` action, or the `/favor set
<targets> <deity> patron` command). Changing patron:

* resets the previous patron's decay/perk bonus to 0,
* applies the new patron's `penalty` to the previous patron's favor,
* adds the new patron's `decay` and `perk_chance` to the new deity's
  `FavorLevel`.

A player with a patron gets the patron's decay and perk chance modifiers; the
favor GUI marks the patron.

## Affinity

`rpggods:affinity` actions assign AI behavior for a mob type:

| Affinity | Effect |
| --- | --- |
| `passive` | The mob does not attack players that satisfy the perk's range. |
| `hostile` | The mob attacks players that satisfy the range. |
| `flee` | The mob avoids players that satisfy the range. |
| `tame` | Right-clicking the mob tames it (if the action's conditions match). |

Affinity behavior is resolved from all perks that contain an
`rpggods:affinity` action; the perk's `range` is checked against the player's
favor.

## Configuration

The config file is `config/rpggods-common.toml`.

| Key | Default | Description |
| --- | --- | --- |
| `favor.favor_enabled` | `true` | Master switch for favor, offerings, sacrifices and perks. |
| `favor.favor_mode` | `player` | `player`, `team` or `global`. |
| `favor.favor_update_rate` | `20` | Ticks between favor updates (perks, decay, cooldowns). |
| `favor.random_perk_chance` | `0.34` | Chance per update to evaluate `random_tick` perks. |
| `favor.favor_decay_rate` | `0.006` | Chance per update to deplete favor. |
| `favor.favor_decay_amount` | `1` | Points removed per depletion. |
| `favor.perk_feedback` | `true` | Notify the player when a perk runs. |
| `favor.perk_feedback_chat` | `false` | Send feedback in chat instead of the action bar. |
| `brazier.brazier_enabled` | `true` | Enable automatic offerings using a brazier. |
| `brazier.brazier_cooldown` | `8` | Ticks between brazier offerings. |
| `brazier.brazier_range` | `2` | Blocks to search for an altar. |
| `affinity.flee_enabled` | `true` | Enable `flee` affinity AI. |
| `affinity.hostile_enabled` | `true` | Enable `hostile` affinity AI. |
| `affinity.passive_enabled` | `true` | Enable `passive` affinity AI. |
| `affinity.tameable_enabled` | `true` | Enable `tame` affinity. |
| `sitting.sitting_mobs` | list | Mob IDs that use a sitting pose. |

## Commands

All commands require permission level 2. `<targets>` is one or more players;
`<deity>` is a deity ID such as `greek:athena`.

| Command | Description |
| --- | --- |
| `/favor list` | List registered deities and their enabled/unlocked state. |
| `/favor add <targets> <deity> <amount> [points\|levels\|perk_chance\|decay]` | Add points (default levels) or modifiers. |
| `/favor set <targets> <deity> <amount> [points\|levels\|perk_chance\|decay]` | Set points/levels/modifiers. |
| `/favor set <targets> <deity> patron` | Set the player's patron. |
| `/favor set <targets> <deity> unlocked <true\|false>` | Unlock or lock a deity. |
| `/favor set <targets> enabled <true\|false>` | Enable/disable favor entirely. |
| `/favor query <target> <deity> [points\|levels\|unlocked\|perk_chance\|decay]` | Query a value. |
| `/favor query <target> enabled` | Query whether favor is enabled. |
| `/favor query <target> patron` | Query the patron. |
| `/favor cap <targets> <deity> <min> <max> [points\|levels]` | Set level/point bounds. |
| `/favor reset <targets> [<deity>\|patron\|cooldown]` | Reset favor, a deity's favor, patron or cooldowns. |

`/favor` is also registered as a redirect so command blocks/functions can use
the shorter form. Perk functions run with permission level 4.

## GUI

Open the favor GUI with the `rpggods:scroll`, or by right-clicking a deity altar
with an empty hand.

* **Summary** — favor points, level, patron, and an altar preview.
* **Offerings** — accepted items and trades, with level requirements and
  functions.
* **Sacrifices** — mobs to kill, their favor and conditions.
* **Perks** — draggable grid of perks by unlock level, with tooltips, chance and
  cooldown overlays.
