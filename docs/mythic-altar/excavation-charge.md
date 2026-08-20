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
- **Lore:** Shows the current shape and level, plus that level's size and centre damage, read live
  from the blast level table so the lore never drifts from what the charge does, and the usage
  hints for cycling and setting it off.

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
below) and centre damage is the damage dealt to a living entity standing at the detonation point.

| Level | Size | Centre Damage |
|---|---|---|
| 1 | 8 | 10.0 |
| 2 | 16 | 18.0 |
| 3 | 24 | 26.0 |
| 4 | 32 | 34.0 |
| 5 | 64 | 42.0 |

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
| Tunnel | A square corridor of length `size`, cross section grows with the level | 8 x 8 x 32 = 2,048 blocks |

**Every shape digs away from the charge rather than around it.** The charge sits in the top layer
of the volume and the cuboid, the sphere and the cylinder all hang below it; the tunnel bores
forward along the direction the charge was placed facing. A volume centred on the charge would
spend half its height on the air above a player's head, which on open ground carves a shallow
square instead of the pit the shape promises.

The sphere and the cylinder measure one block more across than their `size` suggests, because a
round volume cannot be both symmetric about its axis and an even number of blocks wide.

The tunnel's cross section follows its own smaller scale, so it stays a corridor rather than a
cavern: 3 by 3 at level 1, 4 by 4 at level 2, 6 by 6 at level 3 and 8 by 8 at levels 4 and 5,
which is wide enough for two rail lines with room for decoration. Level 5 spends its whole growth
on length instead, boring 64 blocks. On a horizontal bore the corridor's floor sits on the layer
the charge was placed on and its height extends upwards, so the digger stands on the new floor
instead of falling into it.

**The lowest layer of the world is never carved**, whatever it is made of. It is bedrock in an
ordinary world, but a flat or custom world can floor itself with anything, and removing that layer
would open the world into the void.

---

## Controls

| Action | Effect |
|---|---|
| Shift + right click, held in hand | Cycles the blast shape to the next one and shows it on the action bar |
| Right click a block, held in hand, not sneaking | Places the charge as an end crystal above the clicked block, facing the cardinal direction the player was looking (used by the Tunnel shape) |
| Left click, or any damage, on a placed charge | Arms a fuse. A charge a player sets off this way gets a 5 second fuse (100 ticks), during which the outline of the volume it is about to carve is drawn in particles that run green at level 1 through amber and red to deep red at level 5; vanilla explosion damage to the crystal is always cancelled so it never triggers a second, uncarved vanilla blast |
| Right click a placed charge | Collects it back into the player's inventory, preserving its shape and level; drops it on the ground instead if the inventory is full |
| Redstone signal next to a placed charge | Arms the normal fuse when a redstone component within two blocks **switches on**, so a charge can be wired to a lever, a button or a clock. A signal that is already on does nothing |

A charge already counting down ignores further hits, so it cannot be re-armed or have a second
fuse stacked on top of the first.

**A charge reacts to a signal switching on, not to a signal being on.** Placing a charge where power
is already present does nothing, and a signal that stays on does not arm it again. That is what
stops a charge from detonating the instant it is placed in an already wired area. To set one off,
flip a lever, press a button, or let a clock tick.

The countdown draws the volume as a dense fill of its surface with the boundary edges marked in
larger particles, all in the level's colour, so the exact reach of the blast is readable before it
goes off.

Placed charges fall: a charge loses its support when the block under it is mined away or carved
out by a neighbouring blast, and it then drops until it lands on the next solid block below.

---

## Audit Log

Every action on a charge is written to the server log with the `Excavation Charge:` prefix, so the
whole trail of an incident is one grep away:

- Who placed a charge, with its level, shape, facing and position.
- Who collected a placed charge back up, and from where.
- Who ignited a charge, or what did: a non player ignition names the damage cause, a redstone
  ignition names the position of the component whose signal rose.
- Which blast chain ignited which charge, so a cascade can be walked back to the player who
  started it.
- Every detonation, with its level, shape, position, the number of blocks carved and the corner to
  corner bounding box of the removed region, so the affected area can be judged without replaying
  the blast.

Block removals themselves are additionally picked up by block loggers such as CoreProtect as an
end crystal explosion. Attributing them there to the igniting player would require compiling
against the logger's API, which this plugin deliberately does not depend on, so the who and where
live in the plugin's own log lines instead.

---

## Chain Detonation

A detonating charge wakes every other placed charge standing inside the volume its blast carves,
so a chain fires exactly where the explosion visibly reaches: a charge the blast digs out goes off
with it, and a charge one block outside the carved shape stays untouched. The woken charges arm
with a short 0.5 second fuse (10 ticks) instead of the 5 second player fuse, so a cascade reads as
one continuous chain reaction rather than a string of separate explosions.

The whole cascade, counting the charge that started it, is capped at **5 members**. The cap
applies to the cascade as a whole rather than to how many neighbours a single charge may wake,
because a per charge limit is unbounded through branching: with a limit of two neighbours each,
the initiator wakes two, each of those wakes two more, and the chain grows without end while every
individual step still looks compliant. Once the cascade has recruited its fifth member, no further
charge is woken even if more sit inside a blast.

---

## What Survives a Blast

A block is destroyed only if all of the following hold:

- It is not air, cave air or void air. Water and lava are carved out like any other block, so a hole
  dug into an ocean or a lava lake comes out as the shape it promised. Once the blast has finished
  carving, the liquid along the rim of the hole is nudged, so an ocean or a lava lake flows back in
  over the following seconds.
- It is not on the explicit deny list: **Bedrock**, Reinforced Deepslate, Barrier, Light, Command
  Block, Chain Command Block, Repeating Command Block, Structure Block, Jigsaw, End Portal, End
  Portal Frame and End Gateway never break, regardless of their blast resistance.
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
  large blast from flooding the ground with tens of thousands of items.
- Whatever does drop is tallied by material rather than dropped block by block, and the
  consolidated stacks are thrown at the detonation point only once the whole blast has finished
  carving.

---

## Why a Large Blast Is Not Instant

Carving is paced as a shock wave: a wavefront starts at the charge and travels outwards at a
fixed speed per level, and each tick removes exactly the blocks the wave has reached. The inner
shells fall in quick succession and the wide outer shells go in single sweeps, so the blast reads
as one expanding explosion rather than a crawl. The wavefront is slightly faster on higher levels,
so a big blast reads as a stronger shock while its greater size still makes the whole carve last
longer.

| Level | Wave speed | Full carve takes about |
|---|---|---|
| 1 | 0.9 blocks per tick | half a second |
| 2 | 1.0 blocks per tick | one second |
| 3 | 1.1 blocks per tick | one and a third seconds |
| 4 | 1.25 blocks per tick | one and a half seconds |
| 5 | 1.5 blocks per tick | two and a half seconds |

A single shared per tick ceiling sits above the pacing as a safety net. A small blast never
touches it, only the widest shells of a level 5 blast lean on it briefly, and when several large
blasts land their widest shells in the same tick, the ceiling bounds the total work so a chain of
large charges cannot pile tens of thousands of block updates into a single tick. Under that load
the waves slow down instead of the server.

If the server stops while blasts are still in flight, the scheduler carves every remaining block of
every queued blast synchronously right away rather than leaving a half carved volume behind, since
the plan describing the rest of the blast exists only in memory. Blocks whose chunk has unloaded in
the meantime are skipped, because loading a chunk back just to empty it would cost more than
leaving it as it is.

There is also a ceiling on how much carving may be queued at once, because every blast holds its
whole plan in memory until it has been carved. A detonation that would push the queue past it does
not go off at all: the charge stays where it was placed and can be set off again once the queue has
drained. A line in the server log records each refusal.
