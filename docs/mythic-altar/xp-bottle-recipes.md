# Mythic Altar — XP Bottle System

The XP bottle system introduces three custom items and three altar recipes that allow players to harvest, store, and consume large amounts of experience.

See [overview.md](overview.md) for how to build and use the altar.

---

## Custom Items

### Essence of Spawner

A powder obtained by breaking a **Monster Spawner** block in Survival mode. One Essence of Spawner drops per spawner broken. It is used as an ingredient to craft Experience Gems.

- **Appearance:** Gray Dye with enchantment glint
- **Display name:** Essence of Spawner

---

### Experience Gem

A gem used to craft and refill the Magic XP Bottle. Crafted from a Diamond and four Essences of Spawner at the Mythic Altar.

- **Appearance:** Diamond with enchantment glint
- **Display name:** Experience Gem

---

### Magic XP Bottle

A custom experience bottle that stores an arbitrary amount of experience points. Right-clicking the bottle consumes up to **450 XP** from the bottle and applies it to the player. The bottle can be recharged at the Mythic Altar.

- **Appearance:** Experience Bottle (thrown potion bottle) with enchantment glint
- **Display name:** Magic XP Bottle
- **Max stack size:** 1
- **Lore:** Shows the current stored experience amount

---

## Recipes

### Craft Experience Gem

Converts a Diamond and four Essences of Spawner into one Experience Gem.

| Pedestal | Item |
|---|---|
| Center | Diamond (vanilla, **not** an Experience Gem) |
| North-West | Essence of Spawner |
| North-East | Essence of Spawner |
| South-West | Essence of Spawner |
| South-East | Essence of Spawner |

**Result:** One Experience Gem drops at the center pedestal. Silver helix particle effect plays.

---

### Craft Magic XP Bottle

Creates an empty Magic XP Bottle from a Glass Bottle, two Netherite Blocks, and two Experience Gems.

The two Netherite Blocks must be placed on **opposite** pedestals (either NW+SE or NE+SW), and the two Experience Gems must fill the remaining opposite pair.

**Option A:**
| Pedestal | Item |
|---|---|
| Center | Glass Bottle |
| North-West | Netherite Block |
| South-East | Netherite Block |
| North-East | Experience Gem |
| South-West | Experience Gem |

**Option B:**
| Pedestal | Item |
|---|---|
| Center | Glass Bottle |
| North-East | Netherite Block |
| South-West | Netherite Block |
| North-West | Experience Gem |
| South-East | Experience Gem |

**Result:** One Magic XP Bottle (empty) drops at the center pedestal. Red helix particle effect plays.

---

### Fill Magic XP Bottle

Transfers all of the activating player's current experience into a Magic XP Bottle, adding it to any experience already stored.

> **Warning:** This recipe drains your **entire XP bar** — all levels and progress. The experience is stored in the bottle, not lost.

| Pedestal | Item |
|---|---|
| Center | Magic XP Bottle (empty or partially filled) |
| Two opposite pedestals | Eye of Ender (any diagonal pair) |
| Remaining two pedestals | Lapis Lazuli (opposite pair) |

**Option A:**
| Pedestal | Item |
|---|---|
| Center | Magic XP Bottle |
| North-West | Eye of Ender |
| South-East | Eye of Ender |
| North-East | Lapis Lazuli |
| South-West | Lapis Lazuli |

**Option B:**
| Pedestal | Item |
|---|---|
| Center | Magic XP Bottle |
| North-East | Eye of Ender |
| South-West | Eye of Ender |
| North-West | Lapis Lazuli |
| South-East | Lapis Lazuli |

**Result:** All ingredients are consumed. A filled (or further filled) Magic XP Bottle drops at the center pedestal. The player's XP is set to 0. A DNA helix particle effect plays and a level-up sound is played.
