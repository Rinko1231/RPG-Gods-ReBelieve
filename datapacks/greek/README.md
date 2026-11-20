# Greek mythology data pack (1.20.1)

A port of the upstream Greek mythology add-on to the RPG Gods 1.20.1 data pack
format (`pack_format: 15`).

* 13 deities, 14 altars, 197 offerings, 107 sacrifices and 217 perks
* Deity progression: Zeus unlocks Hera, Poseidon and Hades at favor levels
  4 / 5 / 6; those three then unlock the remaining gods
* All perk condition/action `type` ids use the `rpggods:` namespace
  (data pack registry keys still use `deity:`)

## Install

1. Copy the `greek` folder into `saves/<world>/datapacks/` (or
   `world/datapacks/` for a server).
2. Reload the world (`/reload` or re-open the world).
3. Check the server log for
   `Linked 13 deities, 197 offerings, 107 sacrifices, 217 perks`.

The matching names and altar textures are bundled with the mod
(`assets/greek`), so no separate resource pack is required.

## Source

Converted from the upstream `Datapack-19.1.0` release of the RPG Gods Greek
add-on, with the 1.19 → 1.20.1 changes described in
[`PORTING.md`](../../PORTING.md).
