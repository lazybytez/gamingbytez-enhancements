# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GamingBytez Enhancements is a modular Minecraft Paper/Spigot plugin (Java 21, Paper API 1.21.11) for a private server. The plugin provides custom game mechanics and enhancements through a feature-based architecture.

## AI Assistance Guidelines

When working with this repository, adhere to these critical requirements:

- **Maintain code quality and style consistency** - Follow existing patterns, naming conventions, and formatting
- **Never generate unclear code** - If generated code is complex or unclear, refactor it for maintainability
- **Follow existing architectural patterns** - Use established design patterns (Template Method, Strategy, Registry) unless explicitly introducing improvements
- **Security is paramount** - Review all code for vulnerabilities, edge cases, and proper error handling
- **JavaDoc only** - Use clean, concise JavaDoc for all public/protected methods with @param/@return; use multi-line JavaDoc for complex logic to explain "why"; avoid inline comments except for explaining non-obvious decisions or workarounds
- **Validate assumptions** - When uncertain about implementation details, ask for clarification rather than guessing
- **Prefer early returns** - Use guard clauses and early returns to reduce nesting. Never use if-else when early returns are applicable
- **Use switch statements** - Prefer switch over multiple if-else chains when comparing constant values
- **Use constants judiciously** - Extract constants when they improve code clarity, NOT as a blanket rule:
  - **Always use constants for**: PDC keys, namespaced keys, and technical identifiers
  - **Use constants for**: Configuration values (timeouts, cooldowns, thresholds), values used multiple times in the same class, complex calculations (e.g., `1000L * 60L * 5L` for "5 minutes")
  - **Never use constants for**: User-facing text (display names, lore), one-off magic numbers (particle counts, delays used once), simple inline values (0.0, 1, 20 ticks when used once), colors (use `NamedTextColor.RED`, `Color.RED`), sound parameters (volume/pitch like 1.0f, 0.8f)
  - **Rule of thumb**: If it's used once and self-explanatory in context, keep it inline; if it's reused or a tuning value, extract it
- **Clean coding style** - Write clean, readable code following SOLID, DRY, KISS principles
- **No deprecated APIs** - Never use deprecated APIs or methods; always find and use the modern alternative
- **Method visibility** - Use private for internal helpers, public only when needed externally
- **Keep methods focused** - Methods should be short and do one thing; extract private helper methods when logic becomes complex
- **Proper exception handling** - Use try-catch for operations that can fail; log errors appropriately
- **Balance loops vs streams** - Use traditional for/while loops for simple iterations (more performant and readable); use streams for complex filtering/transforming chains where they improve readability
- **Use `this.` prefix** - Always use `this.` to access fields and call methods for consistency
- **Blank lines for clarity** - Use blank lines to separate logical sections within methods

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
- Paper API 1.21.11-R0.1-SNAPSHOT (provided scope)
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