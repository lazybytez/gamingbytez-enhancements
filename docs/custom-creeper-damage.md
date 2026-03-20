# Custom Creeper Damage

Adjusts the damage a player receives from a creeper explosion based on the armor they are wearing. The more armor and armor-related enchantments a player has, the more the explosion damage is reduced — with a random variance added for unpredictability.

## Damage Formula

```
armorFactor     = (armorPoints + armorToughness + enchantmentBonus) / 5
adjustedFactor  = armorFactor / 10
luck            = random value between 0.5 and 2.0
adjustedDamage  = baseDamage * (luck * adjustedFactor)
```

If the calculated damage is negative or lower than the base damage, the base damage is used as a minimum.

### Armor Inputs

| Input | Description |
|---|---|
| Armor points | Sum of the defence points from all equipped armor pieces |
| Armor toughness | The toughness attribute value of the equipped armor |
| Protection enchantment | Each level adds 1 to the factor |
| Blast Protection enchantment | Each level adds 1 to the factor |

### Luck Multiplier

A random multiplier between `0.5` and `2.0` is applied to the armor factor on each hit, meaning armor provides variable but directionally correct protection.

## Configuration

This feature has no configuration options.
