# Mythic Altar — Time Rituals

These recipes change the time of day in the current world. Each ritual broadcasts a message to all players on completion. If the target time is already active, the ingredients are returned and no effect is applied.

See [overview.md](overview.md) for how to build and use the altar.

---

## Day Ritual

Sets the world time to dawn (tick 0).

### Ingredients

| Pedestal | Item |
|---|---|
| Center | Torch |
| North-West | Torch |
| North-East | Torch |
| South-West | Torch |
| South-East | Torch |

### Result

- World time is set to `0` (sunrise).
- All five Torches are consumed.
- White particle lines animate from the outer pedestals to the center.

---

## Night Ritual

Sets the world time to night (tick 14000).

### Ingredients

| Pedestal | Item |
|---|---|
| Center | Redstone Torch |
| North-West | Ink Sac |
| North-East | Ink Sac |
| South-West | Ink Sac |
| South-East | Ink Sac |

### Result

- World time is set to `14000` (late evening).
- All ingredients are consumed.
- Black particle lines animate from the outer pedestals to the center.
