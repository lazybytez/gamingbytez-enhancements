# Configuration

All plugin settings live in `config.yml` inside the plugin data folder (`plugins/gamingbytez-enhancements/config.yml`). The file is created with defaults on first load and is never overwritten on subsequent starts — edit it freely.

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
