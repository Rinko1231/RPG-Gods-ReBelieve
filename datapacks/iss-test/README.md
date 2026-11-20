# iss:iron test data pack

Test data pack for the optional **Iron's Spellbooks** integration:

* adds the deity `iss:iron` ("Iron"), unlocked by default
* offerings / sacrifices use only vanilla items, so the pack still loads cleanly
  when Iron's Spellbooks is not installed
* perk `iss:iron/fire_magic_power` grants **+30% fire spell power**
  (`irons_spellbooks:fire_spell_power`, `add_multiplied_base`) at favor level 3
  and above; the modifier is removed again when favor drops below 3
  * older Iron's Spellbooks versions called this attribute
    `irons_spellbooks:fire_magic_power`; the data pack ships the current
    `fire_spell_power` id, but any attribute id can be used in an
    `rpggods:attribute` action
* while Iron's Spellbooks is installed, casting a spell, dealing spell damage and
  spell healing each add favor with `iss:iron`

## Install

Copy the `iss-test` folder into `saves/<world>/datapacks/iss` (or
`world/datapacks/iss` for a server) and reload.

Give yourself some favor to test:

```
/favor set @s iss:iron 2
/favor set @s iss:iron 3
```

The `fire_magic_power` attribute should appear at level 3 and disappear when you
set the level back to 2. Without Iron's Spellbooks the perk is still valid but
the attribute lookup silently does nothing.
