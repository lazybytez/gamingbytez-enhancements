# Custom Creeper Damage

Replaces the damage a creeper explosion deals to a player with a value derived from the armor that
player is wearing. Heavier armor makes a creeper **more** dangerous, not less, so a fully geared
player still has to respect one. A random roll on every hit keeps the outcome variable.

## Damage Formula

```
blast        = min(baseDamage, 29)
protection   = max(armorPoints + armorToughness + 0.25 * enchantmentLevels, 22)
armorFactor  = protection / 42
luck         = random value between 0.15 and 1.0
damage       = blast * armorFactor * luck
```

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

### Protection Floor

Protection never counts as less than 22, so a player in light armor or none at all still takes a
serious hit rather than walking away from a creeper. Everything up to and including a full iron set
sits at the floor and takes the same damage; diamond and netherite rise above it.

### Blast Cap

Vanilla explosion damage climbs steeply as the distance closes, which made a creeper detonating
against a player a guaranteed kill for every armor set. Blast strength is therefore capped at 29
before the armor factor is applied, so the last couple of blocks of an approach stop mattering and
a point blank hit carries roughly the same risk as one from a few steps away.

The cap applies to charged creepers as well, so a charged creeper is no more lethal to a player
than an ordinary one.

### Luck Multiplier

Every hit rolls a multiplier between `0.15` and `1.0`. The low end is what lets a blast occasionally
barely scratch a player, and the ceiling is what decides how often one kills outright.

## What This Means In Practice

For a player at full health, against a creeper a few steps away (a blast of roughly 28) and one
detonating against the player (43, capped to 29):

| Armor | Damage, a few steps | One shot | Damage, point blank | One shot |
|---|---|---|---|---|
| Full netherite, Protection IV | 3.6 to 24.0 | ~20% | 3.7 to 24.9 | ~23% |
| Full netherite, unenchanted | 3.2 to 21.3 | ~7% | 3.3 to 22.1 | ~11% |
| Full diamond, unenchanted | 2.8 to 18.7 | never | 2.9 to 19.3 | never |
| Iron or lighter, including none | 2.2 to 14.7 | never | 2.3 to 15.2 | never |

Average damage runs from about 8 at the protection floor to about 14 in enchanted netherite.
Distance still decides how much damage a blast deals, up to the cap. What it no longer decides is
whether the hit is survivable at all.

## Configuration

This feature has no configuration options.
