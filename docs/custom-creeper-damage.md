# Custom Creeper Damage

Replaces the damage a creeper explosion deals to a player with a value derived from the armor that
player is wearing. Heavier armor makes a creeper **more** dangerous, not less, so a fully geared
player still has to respect one. A random roll on every hit keeps the outcome variable.

## Damage Formula

```
protection   = armorPoints + armorToughness + 0.25 * enchantmentLevels
armorFactor  = protection / 50
luck         = random value between 0.5 and 1.25
damage       = baseDamage * armorFactor * luck
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

### Luck Multiplier

Every hit rolls a multiplier between `0.5` and `1.25`. The ceiling is what decides how often a
blast kills outright: a player only dies in one hit when the roll lands in the lethal part of that
window.

## What This Means In Practice

For a creeper detonating about two blocks away, which lands a blast of roughly 28 before reduction:

| Armor | Damage range | Chance of a one shot |
|---|---|---|
| Full netherite, Protection IV | 10.1 to 25.2 | ~34% |
| Full netherite, unenchanted | 9.0 to 22.4 | ~18% |
| Full diamond, unenchanted | 7.8 to 19.6 | never |

A creeper that goes off directly against a player lands a much larger blast and stays lethal for
every set. Distance is what makes a hit survivable.

## Configuration

This feature has no configuration options.
