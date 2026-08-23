# Custom Creeper Damage

Replaces the damage a creeper explosion deals to a player with a value derived from the armor that
player is wearing. Heavier armor makes a creeper **more** dangerous, not less, so a fully geared
player still has to respect one. A random roll on every hit keeps the outcome variable.

## Damage Formula

```
blast        = min(baseDamage, 29)
protection   = armorPoints + armorToughness + 0.25 * enchantmentLevels
armorFactor  = protection / 50
luck         = random value between 0.5 and 1.25
damage       = blast * armorFactor * luck
```

A player wearing no armor at all takes a flat tenth of the blast instead, scaled by the same luck
roll.

The result is the damage the player actually takes. The listener converts it into the base damage
the server needs so that vanilla armor reduction does not apply a second time on top.

### Armor Inputs

| Input | Contribution |
|---|---|
| Armor points | The armor attribute value, counted in full |
| Armor toughness | The armor toughness attribute value, counted in full |
| Protection enchantment | Each level counts a quarter of an armor point |
| Blast Protection enchantment | Each level counts a quarter of an armor point |

Enchantment levels are weighted down because a full set of Protection IV would otherwise add half
again as much as the armor itself, which made enchanted netherite the most lethal thing to wear.

### Blast Cap

Vanilla explosion damage climbs steeply as the distance closes, which made a creeper detonating
against a player a guaranteed kill for every armor set. Blast strength is therefore capped at 29
before the armor factor is applied, so the last couple of blocks of an approach stop mattering and
a point blank hit carries roughly the same risk as one from a few steps away.

The cap applies to charged creepers as well, so a charged creeper is no more lethal to a player
than an ordinary one.

### Luck Multiplier

Every hit rolls a multiplier between `0.5` and `1.25`. The ceiling is what decides how often a
blast kills outright: a player only dies in one hit when the roll lands in the lethal part of that
window.

## What This Means In Practice

For a player at full health, against a creeper a few steps away (a blast of roughly 28) and one
detonating against the player (43, capped to 29):

| Armor | Damage, a few steps | One shot | Damage, point blank | One shot |
|---|---|---|---|---|
| Full netherite, Protection IV | 10.1 to 25.2 | ~34% | 10.4 to 26.1 | ~39% |
| Full netherite, unenchanted | 9.0 to 22.4 | ~18% | 9.3 to 23.2 | ~23% |
| Full diamond, unenchanted | 7.8 to 19.6 | never | 8.1 to 20.3 | ~2% |

Distance still decides how much damage a blast deals, up to the cap. What it no longer decides is
whether the hit is survivable at all.

## Configuration

This feature has no configuration options.
