# Custom Creeper Damage

Replaces the damage a creeper explosion deals to a player with a value derived from the armor that
player is wearing. Heavier armor makes a creeper **more** dangerous, not less, so a fully geared
player still has to respect one. A random roll on every hit keeps the outcome variable.

## Damage Formula

```
blast        = clamp(baseDamage, 20, 24)
worn         = max(armorPoints + armorToughness + 0.125 * enchantmentLevels, 13)
protection   = worn <= 28 ? worn : 28 + (worn - 28) * 0.25
armorFactor  = protection / 27.9
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

### Protection Floor and Soft Cap

Protection never counts as less than 13, so a player in light armor or none at all still loses about
three hearts to a blast rather than walking away from it. Leather, gold and chainmail all sit at the
floor; iron and everything above it rise past it.

Above the 28 points a diamond set carries, the curve flattens to a quarter of its slope. Without it
the gap between diamond and an enchanted netherite set was wide enough that one of them had to sit
well outside the intended range of odds, and flattening the top is what holds all three inside it.

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

For a player at full health with no Resistance or absorption, against a creeper detonating close
enough to reach the top of the band, which is most of them:

| Armor | Damage | One shot |
|---|---|---|
| None, leather, gold or chainmail | 1.7 to 11.2 | never |
| Full iron | 1.9 to 12.9 | never |
| Full diamond | 3.6 to 24.1 | ~20% |
| Full netherite | 3.7 to 25.0 | ~23% |
| Full netherite, Protection IV | 3.8 to 25.4 | ~25% |

One shots begin at diamond and never pass a quarter of hits. Everything below diamond takes a real
bite out of a health bar without ever being able to finish the job. A creeper that goes off at the
bottom of the band is far gentler, killing a diamond clad player well under one time in a hundred.

## Configuration

This feature has no configuration options.
