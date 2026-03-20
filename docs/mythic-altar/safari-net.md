# Mythic Altar — Safari Net

The Safari Net is a craftable item that lets players capture and relocate any mob. Throw it at a mob for a 50% chance to catch it; right-click to release it at your location.

See [overview.md](overview.md) for how to build and use the altar.

---

## Custom Item

- **Appearance:** Snowball with enchantment glint
- **Display name:** Safari Net (empty) / Safari Net (Entity Name) (occupied)
- **Max stack size:** 1
- **Lore (empty):** "A mystical net that can capture creatures." / "Throw to catch entities!" / "50% success rate"
- **Lore (occupied):** Shows the name of the captured entity; "Right-click to release!"

---

## Crafting Recipe

Place the following items into the altar. The four outer ingredients can go in **any** combination of the four outer pedestals — position does not matter, only item identity.

| Pedestal | Item |
|---|---|
| Center | Snowball |
| Any outer pedestal | Golden Apple |
| Any outer pedestal | Diamond |
| Any outer pedestal | Redstone Block |
| Any outer pedestal | Phantom Membrane |

**Result:** One Safari Net drops at the center pedestal. Aqua helix particle effect plays.

---

## Usage

### Catching a Mob

1. Hold the Safari Net in your hand.
2. Right-click (throw) the snowball at a mob.
3. There is a **50% chance** per throw of successfully capturing the mob.
4. On success, the mob despawns and is stored inside the net. The item name updates to show the captured entity.
5. On failure, the Safari Net is returned as a normal item drop.

### Releasing a Mob

1. Hold the occupied Safari Net in your hand.
2. Right-click to release the captured mob at your feet.
3. The mob spawns with all its state preserved (name, health, equipment, etc.).
4. The Safari Net resets to its empty state.

---

## Notes

- The Safari Net preserves full entity state using Paper's `EntitySnapshot` API, including custom names, health, age, color, size, and other NBT data.
- If entity data is unreadable (e.g. data from a previous plugin version), a fresh entity of the correct type is spawned instead.
- You cannot stack Safari Nets.
