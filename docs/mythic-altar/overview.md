# Mythic Altar — Overview

The Mythic Altar is a multiblock crafting station that unlocks powerful rituals and unique custom items. Recipes are performed by placing items into the altar's glow item frames (pedestals) and interacting with the center pedestal.

## Altar Structure

The altar uses two block layers. All coordinates are relative to the center Diamond Block.

### Layer 1 — Surface (y = 0)

Only 5 blocks make up this layer. The rest of the surface is open air.

```
     X: -2  -1   0  +1  +2
Z=+2:    E   .   .   .   E
Z=+1:    .   .   .   .   .
Z= 0:    .   .   D   .   .
Z=-1:    .   .   .   .   .
Z=-2:    E   .   .   .   E
```

| Symbol | Block |
|---|---|
| `D` | Diamond Block — center of the altar |
| `E` | Emerald Block — pedestal base at each corner (±2 on both X and Z) |

### Layer 2 — Foundation (y = −1, directly below the surface)

This full 7×7 layer sits one block below the surface.

```
     X: -3  -2  -1   0  +1  +2  +3
Z=+3:    G   P   Q   Q   Q   P   G
Z=+2:    P   C   C   C   C   C   P
Z=+1:    Q   C   S   C   S   C   Q
Z= 0:    Q   C   C   C   C   C   Q
Z=-1:    Q   C   S   ·   S   C   Q
Z=-2:    P   C   C   C   C   C   P
Z=-3:    G   P   Q   Q   Q   P   G
```

| Symbol | Block |
|---|---|
| `G` | Gold Block |
| `P` | Quartz Pillar |
| `Q` | Quartz Stairs |
| `C` | Chiseled Quartz Block |
| `S` | Sea Lantern |
| `·` | Empty — no block required at this position (x=0, z=−1) |

> The structure validator checks only the defined positions. The empty cell at `·` can be any block or air.

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
