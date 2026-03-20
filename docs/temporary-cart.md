# Temporary Cart

Spawns a temporary minecart when a player right-clicks a rail without holding a minecart item in hand.

## How It Works

1. Right-click any rail type without a minecart in your main hand.
2. A minecart spawns at that location for you to ride immediately.
3. When you exit the minecart, it is automatically removed.

The minecart is tied to the player who spawned it. It is cleaned up automatically in all of the following situations:

- Player exits the minecart
- Player disconnects from the server
- Player dies and respawns
- Player travels through a portal or changes dimension
- The minecart is destroyed

## Cooldown

There is a 1-second cooldown per player to prevent rapid spawning. The cooldown resets on death.

## Permissions

No permissions are required. Any player can use this feature.

## Configuration

This feature has no configuration options.
