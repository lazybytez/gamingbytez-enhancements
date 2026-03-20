<div align="center">

# GamingBytez Enhancements

[![gh-license-badge][gh-license-badge]][gh-license]
[![discord-badge][discord-badge]][discord]

[![gh-contributors-badge][gh-contributors-badge]][gh-contributors]
[![gh-stars-badge][gh-stars-badge]][gh-stars]

</div>

## Description

GamingBytez Enhancements is an open-source Paper plugin for Minecraft 1.21+. It provides a collection of gameplay features including a custom crafting altar, entity capture mechanics, a chat bot, minecart portals, and various quality-of-life improvements.

The plugin is built around a modular feature system — each feature is self-contained, independently managed, and follows a consistent lifecycle.

## Features

| Feature | Description |
|---|---|
| [Mythic Altar](docs/mythic-altar/overview.md) | Custom multiblock crafting altar with weather/time rituals and unique items |
| [Minecart Portals](docs/minecart-portals.md) | Admin-configurable portals that teleport minecart passengers between locations |
| [Chat Bot](docs/chatbot.md) | In-game chat bot with static responses and optional AI integration |
| [Temporary Cart](docs/temporary-cart.md) | Spawns a temporary minecart when right-clicking a rail |
| [Farmland Protection](docs/farmland-protection.md) | Prevents farmland from being trampled by players and mobs |
| [Anti Mob Griefing](docs/anti-mob-griefing.md) | Prevents specific mobs from destroying blocks, items, and entities |
| [Custom Creeper Damage](docs/custom-creeper-damage.md) | Scales creeper explosion damage based on the player's equipped armor |
| [Custom Loot](docs/custom-loot.md) | Adds extra loot drops to Husks and Endermen |

## Requirements

- Java 21+
- [Paper](https://papermc.io/) 1.21.4+
- Maven 3.x (for building)

## Getting Started

Clone the repository and build the plugin:

```bash
git clone https://github.com/lazybytez/gamingbytez-enhancements.git
cd gamingbytez-enhancements
mvn clean package
```

The compiled JAR will be in `target/`. Copy it into your Paper server's `plugins/` directory and restart the server.

For full feature documentation, see the [docs/](docs/) directory.

## Configuration

The main configuration file (`config.yml`) is generated on first run. It currently exposes settings for the [Chat Bot](docs/chatbot.md) feature and its optional OpenAI integration.

## Contributing

Contributions are welcome. Please read the [Contributing Guide][gh-contribute] before opening a pull request.

### Developer Certificate of Origin (DCO)

All contributions must be signed off against the [Developer Certificate of Origin (DCO)][dco] to certify that you wrote the code and have the right to submit it under the project's license. Add the following to every commit:

```
Signed-off-by: Your Name <your@email.com>
```

The easiest way is to use `git commit -s`, which adds the sign-off automatically. Pull requests without a DCO sign-off on every commit will not be accepted.

### License Headers

Every Java source file must include the project's AGPL license header. Run the following command before submitting a PR to apply or update headers automatically:

```bash
mvn license:format
```

CI will reject any PR with missing or incorrect headers via `mvn license:check`.

### Commit Messages

```
prefix(scope): subject with max 50 chars
```

**Prefixes:** `feat`, `fix`, `build`, `chore`, `ci`, `docs`, `perf`, `refactor`, `revert`, `style`, `test`

**Scopes:** `deps` (dependencies), `devops` (technical processes), or a feature-specific name.

**Example:**
```
feat(chatbot): add conversation context tracking
```

### Branches

| Branch | Usage |
|---|---|
| `main` | Default branch |
| `feature/*` | New features |
| `fix/*` | Bug fixes |

### Recommended IDE

- [IntelliJ IDEA](https://www.jetbrains.com/idea/)

## Useful Links

[License][gh-license] -
[Contributing][gh-contribute] -
[Code of Conduct][gh-codeofconduct] -
[Issues][gh-issues] -
[Pull Requests][gh-pulls]

<hr>

###### Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors | Licensed under the [AGPL-3.0 license][gh-license].

<!-- Variables -->

[gh-license-badge]: https://img.shields.io/github/license/lazybytez/gamingbytez-enhancements?logo=mit&style=for-the-badge&colorA=302D41&colorB=eba0ac

[gh-license]: https://github.com/lazybytez/gamingbytez-enhancements/blob/main/LICENSE

[discord-badge]: https://img.shields.io/discord/735171597362659328?label=Discord&logo=discord&style=for-the-badge&colorA=302D41&colorB=b4befe

[discord]: https://discord.gg/bcV6TN2k9V

[gh-contributors-badge]: https://img.shields.io/github/contributors/lazybytez/gamingbytez-enhancements?style=for-the-badge&colorA=302D41&colorB=cba6f7

[gh-contributors]: https://github.com/lazybytez/gamingbytez-enhancements/graphs/contributors

[gh-stars-badge]: https://img.shields.io/github/stars/lazybytez/gamingbytez-enhancements?colorA=302D41&colorB=f9e2af&style=for-the-badge

[gh-stars]: https://github.com/lazybytez/gamingbytez-enhancements/stargazers

[gh-contribute]: https://github.com/lazybytez/.github/blob/main/docs/CONTRIBUTING.md

[gh-codeofconduct]: https://github.com/lazybytez/.github/blob/main/docs/CODE_OF_CONDUCT.md

[gh-issues]: https://github.com/lazybytez/gamingbytez-enhancements/issues

[gh-pulls]: https://github.com/lazybytez/gamingbytez-enhancements/pulls

[gh-team]: https://github.com/lazybytez

[dco]: https://github.com/lazybytez/gamingbytez-enhancements/blob/main/DCO
