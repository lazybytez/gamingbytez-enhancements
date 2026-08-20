# Configuration

All plugin settings live in `config.yml` inside the plugin data folder (`plugins/gamingbytez-enhancements/config.yml`). The file is created with defaults on first load and is never overwritten on subsequent starts — edit it freely.

## Supported Minecraft Version

This release is built for **Minecraft 26.2** on a PaperMC server, and requires **Java 25**, which is
the minimum Minecraft itself requires from 26.1 onwards. The plugin supports one Minecraft version at
a time.

On any other version the plugin **still loads and enables every feature**, and prints a warning
naming both the version it was built for and the version it found:

```
==================================================================
  UNSUPPORTED MINECRAFT VERSION
  This plugin was built for Minecraft 26.2, but the
  server is running 26.3.
  The plugin will keep running anyway and may misbehave.
==================================================================
```

Running anyway is deliberate. Without an actual error or a known incompatibility, refusing to load
would be the worse outcome for an operator, so the plugin warns instead of disabling itself. Treat
the warning as a reason to check for an update rather than as a failure.

Hotfixes do not trigger it: a build for `26.2` stays quiet on `26.2.1` and `26.2.4`, because a hotfix
does not change the plugin API. A different drop, such as `26.3`, does trigger it. So does a version
the plugin cannot read at all, since a version it cannot identify is one it cannot vouch for.

## Feature Toggles

Each feature can be independently enabled or disabled. Set a feature's key to `false` to disable it completely; disabled features are skipped in every lifecycle phase (load, enable, disable) and have no runtime impact.

```yaml
features:
  TemporaryCart: true
  ChatBot: true
  FarmlandProtection: true
  AntiMobGriefing: true
  CustomCreeperDamage: true
  MythicAltar: true
  CustomLoot: true
  MinecartPortal: true
```

All features default to `true` when the key is absent.

| Key | Feature |
|---|---|
| `TemporaryCart` | [Temporary Cart](temporary-cart.md) |
| `ChatBot` | [Chat Bot](chatbot.md) |
| `FarmlandProtection` | [Farmland Protection](farmland-protection.md) |
| `AntiMobGriefing` | [Anti Mob Griefing](anti-mob-griefing.md) |
| `CustomCreeperDamage` | [Custom Creeper Damage](custom-creeper-damage.md) |
| `MythicAltar` | [Mythic Altar](mythic-altar/overview.md) |
| `CustomLoot` | [Custom Loot](custom-loot.md) |
| `MinecartPortal` | [Minecart Portals](minecart-portals.md) |

## Chat Bot & OpenAI

See [Chat Bot](chatbot.md#configuration) for the full `chatbot` and `openai` config reference.
