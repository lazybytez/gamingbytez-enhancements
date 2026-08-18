# Minecart Portals

Lets server operators define portals that teleport minecart passengers from one location to another. When a minecart carrying exactly one player crosses a portal's detector rail, the player is instantly teleported to the exit location.

## How Portals Work

Each portal has two parts:

- **Entry point**: a Detector Rail. When the rail is powered by a minecart, the portal activates and teleports the passenger.
- **Exit point**: a normal Rail where the player is placed after teleportation.

Both locations must be set before a portal is active. Portals are saved to `minecart_portals.yaml` in the plugin data folder and persist across restarts.

## Commands

**Aliases:** `/minecartportals`, `/gbmcp`

**Permission:** `gamingbytez.minecartportals.admin`, granted to operators by default. A sender without
it does not see the command at all.

Running the command without a subcommand prints the help listing under a heading naming the
invoked command. The listing is read from the live command tree, so it always matches the
subcommands below and shows the alias you typed.

Every subcommand that changes a portal writes `minecart_portals.yaml` immediately afterwards, so
there is no save command. A successful write is silent. If the write fails, the operator is warned
that the change is only kept in memory and would be lost on a restart. Any later write action, such
as adding a portal or moving an entry or exit point, tries a fresh save of the full portal list, so
a stranded change is persisted along with it as soon as any write succeeds again.

---

### `add <name>`

Creates a new portal with the given name. The name must be alphanumeric and at most 16 characters.

```
/gbmcp add mainstation
```

The portal is created without entry/exit locations. Use `entry` and `exit` to configure them.

---

### `entry <name>`

Sets the entry point for a portal to your current standing position. You must be standing on a **Detector Rail**.

```
/gbmcp entry mainstation
```

---

### `exit <name>`

Sets the exit point for a portal to your current standing position. You must be standing on a normal **Rail**.

```
/gbmcp exit mainstation
```

---

### `delete <name>`

Removes a portal permanently.

```
/gbmcp delete mainstation
```

---

### `list`

Lists all registered portals, one bullet per portal, under a heading carrying the portal count.

Each entry is interactive: clicking a name puts the inspect command for that portal into your chat
input, using whichever label you ran the listing with, and hovering over it shows that portal's
entry location.

```
/gbmcp list
```

---

### `inspect <name>`

Shows the entry location and the exit location of a portal as labelled fields. A location that has
not been set yet is shown as `not set`.

```
/gbmcp inspect mainstation
```

---

### `reload`

Reloads all portals from disk, overwriting the in-memory state.

```
/gbmcp reload
```

## Setup Guide

1. Build a Detector Rail at the entry location and a normal Rail at the exit location.
2. Create a portal: `/gbmcp add myportal`
3. Stand on the Detector Rail and set the entry: `/gbmcp entry myportal`
4. Stand on the exit Rail and set the exit: `/gbmcp exit myportal`
5. Ride a minecart over the Detector Rail and you will be teleported.

## Notes

- A minecart must contain **exactly one player** to trigger the portal.
- The Detector Rail (entry) and the Rail (exit) of a registered portal are protected. Breaking one as a player is cancelled with a message asking you to delete the portal first, and explosions and pistons cannot remove them either. Use `delete` to take a portal out of service.
- Portal data is loaded and saved asynchronously to avoid server lag.
