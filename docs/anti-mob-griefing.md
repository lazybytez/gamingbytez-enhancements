# Anti Mob Griefing

Selectively prevents specific mob types from destroying blocks, items, and certain entities. Unlike the vanilla `mobGriefing` gamerule, this feature targets only the most destructive mobs while leaving normal mob behaviour intact.

## Mobs That Cannot Grief

The following mobs are blocked from performing griefing actions:

- Creeper
- Enderman
- Ghast
- Wither
- Ender Dragon
- Blaze
- Wither Skeleton
- Piglin
- Warden

## Protected Entities

The following entity types are protected from damage caused by mobs in the griefing list:

- Armor Stands
- Hanging entities (item frames, paintings, etc.)
- Minecarts (all variants)

## What Is Blocked

| Action | Example |
|---|---|
| Block explosions | Creeper/Ghast/Wither explosions no longer destroy blocks |
| Block changes | Endermen can no longer pick up blocks |
| Hanging entity breaks | Item frames and paintings cannot be destroyed by these mobs |
| Direct entity damage | These mobs cannot destroy armor stands or minecarts |
| Projectile damage | Projectiles shot by these mobs cannot destroy protected entities |

> Mobs still deal normal damage to players and other non-protected entities.

## Configuration

This feature has no configuration options.
