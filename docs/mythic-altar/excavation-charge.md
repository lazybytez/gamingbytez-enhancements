# Mythic Altar: Excavation Charge

The Excavation Charge is a craftable explosive that carves a precise, chosen shape out of the world
instead of a random vanilla crater. It solves the problem of controlled, large scale terraforming:
mining a straight tunnel, hollowing a sphere for a base, or clearing a cuboid building plot,
without the unpredictable block loss and lag spikes of stacking TNT.

See [overview.md](overview.md) for how to build and use the altar.

---

## Custom Item

- **Appearance:** End Crystal with enchantment glint
- **Display name:** Excavation Charge
- **Max stack size:** 1
- **Lore:** Shows the current shape and level, plus that level's size, centre damage and chain
  reach, read live from the blast level table so the lore never drifts from what the charge does.

---

## Crafting Recipe

Place the following items into the altar. The four outer ingredients can go in **any** combination
of the four outer pedestals, position does not matter, only item identity.

| Pedestal | Item |
|---|---|
| Center | End Crystal (plain, not an existing Excavation Charge) |
| Any outer pedestal | TNT |
| Any outer pedestal | Obsidian |
| Any outer pedestal | Echo Shard |
| Any outer pedestal | Diamond |

**Result:** One level 1, cuboid shaped Excavation Charge drops at the center pedestal.

---

## Upgrade Recipe

Raises an existing Excavation Charge by one blast level while keeping its current shape. The center
pedestal takes the Excavation Charge itself, not a plain end crystal.

| Pedestal | Item |
|---|---|
| Center | An existing Excavation Charge, below the maximum level |
| Any outer pedestal | TNT |
| Any outer pedestal | Obsidian |
| Any outer pedestal | Echo Shard |
| Any outer pedestal | Diamond Block |

**Result:** A new Excavation Charge at the next blast level, in the same shape as the input charge,
drops at the center pedestal. A charge already at the maximum level cannot be upgraded further.

Note the outer ingredients differ from the crafting recipe only in the last slot: a Diamond Block
here instead of a loose Diamond.

---

## Blast Levels

Every stat below scales with the level stored on the charge. Size is the full width of the
carved volume in blocks (the exact meaning of "width" depends on the shape, see Blast Shapes
below), centre damage is the damage dealt to a living entity standing at the detonation point,
and chain reach is the straight line distance within which a detonating charge wakes its
neighbours.

| Level | Size | Centre Damage | Chain Reach |
|---|---|---|---|
| 1 | 8 | 10.0 | 6 blocks |
| 2 | 16 | 18.0 | 8 blocks |
| 3 | 24 | 26.0 | 10 blocks |
| 4 | 32 | 34.0 | 12 blocks |

Damage falls off linearly from the centre value to zero at the edge of the volume, and a caught
entity is knocked away from the detonation point in proportion to the damage it took.

---

## Blast Shapes

Shift plus right click while holding a charge cycles through the four shapes in this order,
wrapping back to Cuboid after Tunnel. All four figures below are the real volumes the geometry
code produces at level 4 (size 32), verified against the project's geometry test suite, not the
round numbers the level table alone would suggest.

| Shape | How it is measured | Volume at level 4 |
|---|---|---|
| Cuboid | Spans `size` blocks on every axis, sunk below the charge | 32 x 32 x 32 = 32,768 blocks |
| Sphere | A ball of diameter `size` hanging below the charge | 33 blocks across, 17,077 blocks |
| Cylinder | A shaft of diameter `size` sunk `size` blocks deep | 33 blocks across, 32 blocks deep, 25,504 blocks |
| Tunnel | Fixed 3 by 3 cross section, only the length scales with `size` | 3 x 3 x 32 = 288 blocks |

**Every shape digs away from the charge rather than around it.** The charge sits in the top layer
of the volume and the cuboid, the sphere and the cylinder all hang below it; the tunnel bores
forward along the direction the charge was placed facing. A volume centred on the charge would
spend half its height on the air above a player's head, which on open ground carves a shallow
square instead of the pit the shape promises.

The sphere and the cylinder measure one block more across than their `size` suggests, because a
round volume cannot be both symmetric about its axis and an even number of blocks wide. Only the
tunnel keeps its width fixed at 3 blocks regardless of level, since it is meant to stay a walkable
mining corridor and not grow into a cavern.

**The lowest layer of the world is never carved**, whatever it is made of. It is bedrock in an
ordinary world, but a flat or custom world can floor itself with anything, and removing that layer
would open the world into the void.

---

## Controls

| Action | Effect |
|---|---|
| Shift + right click, held in hand | Cycles the blast shape to the next one and shows it on the action bar |
| Right click a block, held in hand, not sneaking | Places the charge as an end crystal above the clicked block, facing the cardinal direction the player was looking (used by the Tunnel shape) |
| Left click, or any damage, on a placed charge | Arms a fuse. A charge a player sets off this way gets a 5 second fuse (100 ticks), during which the outline of the volume it is about to carve is drawn in particles that run green at level 1 through amber to red at level 4; vanilla explosion damage to the crystal is always cancelled so it never triggers a second, uncarved vanilla blast |
| Right click a placed charge | Collects it back into the player's inventory, preserving its shape and level; drops it on the ground instead if the inventory is full |

A charge already counting down ignores further hits, so it cannot be re-armed or have a second
fuse stacked on top of the first.

---

## Chain Detonation

A detonating charge wakes every other placed charge within its level's chain reach, and those
charges arm with a short 0.5 second fuse (10 ticks) instead of the 5 second player fuse, so a
cascade reads as one continuous chain reaction rather than a string of separate explosions.

The whole cascade, counting the charge that started it, is capped at **5 members**. The cap
applies to the cascade as a whole rather than to how many neighbours a single charge may wake,
because a per charge limit is unbounded through branching: with a limit of two neighbours each,
the initiator wakes two, each of those wakes two more, and the chain grows without end while every
individual step still looks compliant. Once the cascade has recruited its fifth member, no further
charge is woken even if more sit within reach.

---

## What Survives a Blast

A block is destroyed only if all of the following hold:

- It is not air, cave air, void air, water or lava. Liquids are left in place entirely: an Excavation Charge does not turn water or lava into air, it simply skips them.
- It is not on the explicit deny list: **Bedrock**, Reinforced Deepslate, Barrier, Light, Command
  Block, Chain Command Block, Repeating Command Block, Structure Block, Jigsaw, End Portal, End
  Portal Frame, End Gateway and Nether Portal never break, regardless of their blast resistance.
- Its blast resistance does not exceed **Obsidian's**. Obsidian's own resistance is used as the
  ceiling of what an Excavation Charge can destroy, so obsidian itself sits exactly on that ceiling and is
  destroyed along with everything weaker than it; only blocks tougher than obsidian, such as
  bedrock, survive on resistance alone (bedrock is also denied explicitly, since its blast
  resistance already exceeds the ceiling by a wide margin).

---

## Drops

- A container block, such as a chest or a barrel, empties its full inventory onto the ground at
  its own location before it is removed. Nothing inside a container is voided.
- Every other destroyed block has a flat **5% chance** of dropping its normal item drops. The
  remaining 95% of destroyed blocks are voided outright, with no item drop, which is what keeps a
  level 4 blast from flooding the ground with tens of thousands of items.
- Whatever does drop is tallied by material rather than dropped block by block, and the
  consolidated stacks are thrown at the detonation point only once the whole blast has finished
  carving.

---

## Why a Large Blast Is Not Instant

Carving is spread across ticks rather than performed in a single instant, which is what keeps a
level 4 blast from freezing the server for the tick it goes off in. One repeating, one tick task
serves every Excavation Charge blast currently in flight and spends a single shared allowance of 500
blocks per tick across all of them, round robin, so a chain of several charges costs a tick exactly
as much as a single blast does.

For a single level 4 cuboid, the largest single shape at 32,768 blocks, that allowance carves the
whole volume in about 66 ticks, a little over three seconds, slow enough to watch the wave travel
outwards. The safety property shows up once charges
chain: because the 500 block allowance is shared rather than duplicated per blast, a full 5
member cascade of level 4 charges can stretch a detonation across several seconds instead of
demanding tens of thousands of block updates from a single tick.

If the server stops while blasts are still in flight, the scheduler carves every remaining block of
every queued blast synchronously right away rather than leaving a half carved volume behind, since
the plan describing the rest of the blast exists only in memory. Blocks whose chunk has unloaded in
the meantime are skipped, because loading a chunk back just to empty it would cost more than
leaving it as it is.

There is also a ceiling on how much carving may be queued at once, because every blast holds its
whole plan in memory until it has been carved. A detonation that would push the queue past it does
not go off at all: the charge stays where it was placed and can be set off again once the queue has
drained. A line in the server log records each refusal.
