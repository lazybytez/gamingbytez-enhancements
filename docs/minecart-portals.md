# Minecart Portals

Lets server operators define portals that teleport minecart passengers from one location to another. When a minecart carrying exactly one player crosses a portal's detector rail, the player is instantly teleported to the exit location.

## How Portals Work

Each portal has two parts:

- **Entry point** — a Detector Rail. When the rail is powered by a minecart, the portal activates and teleports the passenger.
- **Exit point** — a normal Rail where the player is placed after teleportation.

Both locations must be set before a portal is active. Portals are saved to `minecart_portals.yaml` in the plugin data folder and persist across restarts.

## Commands

**Aliases:** `/minecartportals`, `/gbmcp`

**Permission:** Operator (`op`) required for all subcommands.

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

Lists the names of all registered portals.

```
/gbmcp list
```

---

### `inspect <name>`

Shows the name, entry location, and exit location of a portal.

```
/gbmcp inspect mainstation
```

---

### `save`

Saves all portals to disk immediately. Portals are also saved automatically after each modification.

```
/gbmcp save
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
5. Ride a minecart over the Detector Rail — you will be teleported.

## Notes

- A minecart must contain **exactly one player** to trigger the portal.
- If the Detector Rail (entry) or Rail (exit) is broken, the corresponding portal location is automatically cleared.
- Portal data is loaded and saved asynchronously to avoid server lag.
