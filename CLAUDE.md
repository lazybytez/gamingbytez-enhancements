# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GamingBytez Enhancements is a modular Minecraft Paper/Spigot plugin (Java 21, Paper API 1.21.4) for a private server. The plugin provides custom game mechanics and enhancements through a feature-based architecture.

## AI Assistance Guidelines

When working with this repository, adhere to these critical requirements:

- **Maintain code quality and style consistency** - Follow existing patterns, naming conventions, and formatting
- **Never generate unclear code** - If generated code is complex or unclear, refactor it for maintainability
- **Follow existing architectural patterns** - Use established design patterns (Template Method, Strategy, Registry) unless explicitly introducing improvements
- **Security is paramount** - Review all code for vulnerabilities, edge cases, and proper error handling
- **No inline comments** - Use JavaDoc blocks only, matching the existing codebase style
- **Validate assumptions** - When uncertain about implementation details, ask for clarification rather than guessing

## Build & Development Commands

```bash
# Build the plugin (creates shaded JAR in target/)
mvn clean package

# Compile only
mvn compile

# Clean build artifacts
mvn clean

# Install to local Maven repository
mvn install
```

The compiled plugin JAR will be in `target/gamingbytez-enhancements-<version>.jar` and can be dropped into a Paper/Spigot server's `plugins/` directory.

## Commit Message Convention

Required format: `prefix(scope): commit subject with max 50 chars`

**Prefixes:** `feat`, `fix`, `build`, `chore`, `ci`, `docs`, `perf`, `refactor`, `revert`, `style`, `test`

**Scopes:** `deps` (dependencies), `devops` (technical processes), or feature-specific scopes

**Example:** `feat(chatbot): add new conversation context tracking`

## Architecture

### Feature-Based System

The plugin uses a centralized feature abstraction pattern. All features implement the `Feature` interface (typically extending `AbstractFeature`) and follow this lifecycle:

```
onLoad() → onEnable() → onDisable()
```

**Main Entry Point:** `src/main/java/de/lazybytez/gamingbytezenhancements/EnhancementsPlugin.java`

Features are registered in the `features` array in `EnhancementsPlugin`. The plugin orchestrates their lifecycle sequentially during server startup/shutdown.

### Feature Registration

To add a new feature:

1. Create package under `src/main/java/de/lazybytez/gamingbytezenhancements/feature/<featurename>/`
2. Create feature class extending `AbstractFeature`
3. Implement `onEnable()` to register event listeners via `this.registerEvent(listener)`
4. Implement `getName()` for identification
5. Add to `features` array in `EnhancementsPlugin.java`

**Example:**
```java
public class MyFeature extends AbstractFeature {
    public MyFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvent(new MyEventListener());
    }

    @Override
    public String getName() {
        return "MyFeature";
    }
}
```

### Current Features (8 Total)

1. **TemporaryCartFeature** - Temporary minecart spawning with cooldown system
2. **ChatBotFeature** - In-game chat bot with AI integration (action registry + strategy pattern)
3. **FarmlandProtectionFeature** - Prevents farmland trampling
4. **AntiMobGriefingFeature** - Selective mob griefing prevention
5. **CustomCreeperDamageFeature** - Armor-based creeper damage calculation
6. **MythicAltarFeature** - Custom crafting altar system with recipes for weather/time control
7. **CustomLootFeature** - Custom entity loot drops (currently Husk-specific)
8. **MinecartPortalFeature** - Portal system for minecarts with Brigadier commands

### Feature Organization Patterns

Common subdirectory structure within feature packages:
- `event/` or `listener/` - Bukkit event listeners
- `actions/` - Strategy pattern implementations (e.g., ChatBotAction)
- `command/` - Command handlers
- `model/` - Data models
- `service/` or `util/` - Business logic and utilities

### Complex Feature Examples

**MythicAltarFeature** (`feature/mythicaltar/`):
- Uses recipe registry pattern (`CompletableRecipeRegistry`)
- Implements structure validation for multiblock altars
- Particle effect system for visual feedback
- Recipes include weather control (sun, rain, thunderstorm) and time manipulation

**MinecartPortalFeature** (`feature/minecartportal/`):
- Persistent configuration using `minecart_portals.yaml`
- Brigadier-based commands: `/minecartportals` (alias `/gbmcp`)
- Thread-safe portal management with `CopyOnWriteArrayList`
- Async configuration I/O using Bukkit scheduler

**ChatBotFeature** (`feature/chatbot/`):
- Multiple weighted actions with chance-based triggering
- ExecutorService for async message processing
- OpenAI integration for AI-powered responses
- Rate limiting (60s between AI requests)

## Configuration

### Main Config (`config.yml`)

Contains chatbot settings and OpenAI credentials:
```yaml
openai:
  apiUrl: "https://api.openai.com/v1/chat/completions"
  apiKey: ""
  organizationId: ""
  model: "gpt-3.5-turbo"
  temperature: 1.0
chatbot:
  enable_ai_answers: false
```

### Feature-Specific Configs

Features can create dedicated config files (e.g., `minecart_portals.yaml`). Use Bukkit's `ConfigurationSerialization` interface for complex objects.

## OpenAI Integration

**Location:** `src/main/java/de/lazybytez/gamingbytezenhancements/lib/openai/`

Custom HTTP client (`OpenAiClient`) for OpenAI-compatible APIs. Used exclusively by ChatBotFeature's `ChatGPTAction`. Supports custom API URLs and tracks token usage. No external HTTP libraries - uses Java's `HttpURLConnection`.

## Thread Safety

The plugin uses several concurrent patterns:
- `ConcurrentHashMap` for cooldown tracking in TemporaryCartFeature
- `CopyOnWriteArrayList` for action/portal registries
- `ExecutorService` for async operations in ChatBotFeature
- Bukkit scheduler for delayed tasks and async file I/O
- Synchronized methods in `PortalConfiguration` for save/load operations

## Plugin Metadata

**File:** `src/main/resources/paper-plugin.yml`
```yaml
name: gamingbytez-enhancements
version: '1.10.1'
main: de.lazybytez.gamingbytezenhancements.EnhancementsPlugin
api-version: '1.21'
```

## Dependencies

**Maven (pom.xml):**
- Paper API 1.21.4-R0.1-SNAPSHOT (provided scope)
- Maven Shade Plugin for creating uber-JARs

No additional runtime dependencies are bundled.

## Design Patterns in Use

- **Template Method** - AbstractFeature provides lifecycle template
- **Strategy** - ChatBotAction implementations
- **Registry** - Recipe and protection registries
- **Manager** - State management (TemporaryCartManager, PortalConfiguration)
- **Dependency Injection** - Features receive plugin instance via constructor
- **Interface Segregation** - Specialized interfaces (AltarInterface, CompletableRecipeInterface)

## Branch Naming

- `main` - Default branch (use for PRs)
- `feature/*` - New features
- `fix/*` - Bug fixes