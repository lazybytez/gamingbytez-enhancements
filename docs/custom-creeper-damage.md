# Custom Creeper Damage

Replaces the damage a creeper explosion deals to a player with a value derived from the armor that
player is wearing. Heavier armor makes a creeper **more** dangerous, not less, so a fully geared
player still has to respect one. A random roll on every hit keeps the outcome variable.

## Damage Formula

```
blast        = clamp(baseDamage, 20, 24)
protection   = max(armorPoints + armorToughness + 0.125 * enchantmentLevels, 13)
armorFactor  = protection / 26
luck         = random value between 0.15 and 1.0
resistance   = max(0, 1 - 0.4 * resistanceLevel)
damage       = blast * armorFactor * luck * resistance
```

The result is the health the player loses, not a number the server reduces again. The listener
searches for the base damage whose reduction leaves exactly that much health gone, asking the
server what it would deal rather than assuming a fixed ratio between the two.

### Armor Inputs

| Input | Contribution |
|---|---|
| Armor points | The armor attribute value, counted in full |
| Armor toughness | The armor toughness attribute value, counted in full |
| Protection enchantment | Each level counts an eighth of an armor point |
| Blast Protection enchantment | Each level counts an eighth of an armor point |

Enchantment levels are weighted down hard. Counted in full, a set of Protection IV added half again
as much as the armor itself and made enchanted netherite far and away the deadliest thing to wear.

### Protection Floor

Protection never counts as less than 13, so a player in light armor or none at all still loses about
three hearts to a blast rather than walking away from it. Leather, gold and chainmail all sit at the
floor; iron and everything above it rise past it.

### Blast Band

Vanilla explosion damage climbs steeply as the distance closes, which made a creeper detonating
against a player a guaranteed kill and one a few blocks away harmless. Blast strength is clamped
into a band of 20 to 24 before the armor factor applies, so distance still decides how hard a hit
lands but no longer decides whether it is survivable at all.

The band applies to charged creepers as well, so a charged creeper is no more lethal to a player
than an ordinary one.

### Resistance and Absorption

Resistance is the one input that lowers the result rather than raising it, at 40% per level. A
single level is enough to put even the heaviest blast below lethal, and Resistance III stops it
entirely.

Absorption is taken off the intended damage before the search runs, so golden apple hearts soak the
blast the way they soak anything else instead of being solved away.

### Luck Multiplier

Every hit rolls a multiplier between `0.15` and `1.0`. The low end is what lets a blast occasionally
barely scratch a player, and the ceiling is what decides how often one kills outright.

## What This Means In Practice

For a player at full health with no Resistance or absorption, against a blast in the middle of the
band:

| Armor | Damage | One shot |
|---|---|---|
| None, leather, gold or chainmail | 1.7 to 11.0 | never |
| Full iron | 1.9 to 12.7 | never |
| Full diamond | 3.6 to 23.7 | ~18% |
| Full netherite | 4.1 to 27.1 | ~31% |
| Full netherite, Protection IV | 4.3 to 28.8 | ~36% |

One shots begin at diamond. Everything below it takes a real bite out of a health bar without ever
being able to finish the job.

## Configuration

This feature has no configuration options.
