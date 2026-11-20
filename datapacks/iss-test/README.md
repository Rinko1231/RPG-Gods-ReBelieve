# iss:iron test data pack

Test data pack for the optional **Iron's Spellbooks** integration:

* adds the deity `iss:iron` ("Iron"), unlocked by default
* offerings / sacrifices use only vanilla items, so the pack still loads cleanly
  when Iron's Spellbooks is not installed
* perk `iss:iron/fire_spell_power` grants **+30% fire spell power**
  (`irons_spellbooks:fire_spell_power`, `add_multiplied_base`) at favor level 3
  and above; the modifier is removed again when favor drops below 3
* spell perks (visible in the favor GUI's **Perks** tab):
  * `spell_cast_firebolt` — casting `irons_spellbooks:firebolt` grants 5 favor
  * `spell_damage_small` — `irons_spellbooks:icicle` dealing 5–9 damage grants 1 favor
  * `spell_damage_large` — `irons_spellbooks:icicle` dealing 10+ damage grants 2 favor
  * `spell_heal` — any spell healing 1+ grants 2 favor

All spell perks use `"cooldown": 0` so they can be tested without waiting. The
perk `cooldown` / `cooldown_category` fields are the data pack's throttle; there
is no built-in max-uses limit for spell events.

Note: the attribute id is `irons_spellbooks:fire_spell_power`. Any attribute id
can be used in an `rpggods:attribute` action; missing attributes are ignored.

## Install

Copy the `iss-test` folder into `saves/<world>/datapacks/iss` (or
`world/datapacks/iss` for a server) and reload.

Give yourself some favor to test:

```
/favor set @s iss:iron 2
/favor set @s iss:iron 3
```

The `fire_spell_power` attribute should appear at level 3 and disappear when you
set the level back to 2. Without Iron's Spellbooks the attribute perk is still
valid but the attribute lookup silently does nothing, and the spell perks never
fire.
