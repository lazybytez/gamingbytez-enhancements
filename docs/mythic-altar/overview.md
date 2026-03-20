# Mythic Altar — Overview

The Mythic Altar is a multiblock crafting station that unlocks powerful rituals and unique custom items. Recipes are performed by placing items into the altar's glow item frames (pedestals) and interacting with the center pedestal.

## Altar Structure

The altar spans a 7×7 area (from the center block) and uses two layers of blocks.

### Layer 1 — Surface (y = 0)

This is the layer you stand on and place items into.

```
. . . . . . .
. . . . . . .
. . E . E . .
. . . D . . .
. . E . E . .
. . . . . . .
. . . . . . .
```

| Symbol | Block |
|---|---|
| `D` | Diamond Block (center) |
| `E` | Emerald Block (pedestal bases, offset ±2 on X and Z) |

### Layer 2 — Foundation (y = −1, directly below the surface)

This layer is placed one block below the surface blocks.

```
G Q Q Q Q Q G
Q P . P . P Q
Q . L S L . Q
Q P S C S P Q
Q . L S L . Q
Q P . P . P Q
G Q Q Q Q Q G
```

| Symbol | Block |
|---|---|
| `C` | Chiseled Quartz Block (center) |
| `S` | Sea Lantern |
| `L` | Chiseled Quartz Block (inner ring) |
| `P` | Quartz Pillar |
| `Q` | Quartz Stairs |
| `G` | Gold Block (corners) |

> The full foundation spans from −3 to +3 on both the X and Z axes relative to the center.

### Pedestals

Five Glow Item Frames must be placed on top of the surface blocks, one block above (y = +1 relative to the surface):

| Pedestal | Position (relative to center) |
|---|---|
| Center | (0, +1, 0) — above the Diamond Block |
| North-West | (+2, +1, −2) — above NW Emerald Block |
| North-East | (+2, +1, +2) — above NE Emerald Block |
| South-West | (−2, +1, −2) — above SW Emerald Block |
| South-East | (−2, +1, +2) — above SE Emerald Block |

## How to Use the Altar

1. Build the structure exactly as specified above.
2. Place all five **Glow Item Frames** on top of the corresponding surface blocks.
3. Put the recipe items into the item frames. Place the center ingredient **last** — placing it triggers the recipe check.
4. If the altar structure is valid and the ingredients match a recipe, a particle effect plays and the result item drops at the center pedestal.
5. The altar locks for **5 minutes** after a successful craft to prevent spam. Items are returned if the recipe was already in effect (e.g. weather was already clear).

## Available Recipes

| Category | Recipes |
|---|---|
| [Weather Rituals](weather-recipes.md) | Clear weather, start rain, start thunderstorm |
| [Time Rituals](time-recipes.md) | Set time to day, set time to night |
| [XP Bottle System](xp-bottle-recipes.md) | Craft Experience Gem, craft Magic XP Bottle, fill Magic XP Bottle |
| [Safari Net](safari-net.md) | Craft a Safari Net to capture and transport entities |
