# AGENTS.md

This file is the single source of the instructions for this repository. It is read by Codex, by Claude Code through the one-line `CLAUDE.md` adapter, and by human contributors. Change the rules here and nowhere else.

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

### Adding a Command

Command mechanics live in `lib/command/` and are shared by every feature. Never re-implement the
lifecycle wiring, the status codes or the executor check inside a feature package.
`feature/minecartportal/command/MinecartPortalCommand.java` is the reference implementation, so copy
its shape when adding a command.

A command class implements `PluginCommand` and returns the whole tree, root literal included, from
`createNode()`. There is no label accessor: the label lives in the returned builder and is read back
from it, so it cannot be declared twice and drift.

```java
public final class MyFeatureCommand implements PluginCommand {
    private static final String ADMIN_PERMISSION = "gamingbytez.myfeature.admin";
    private static final String LABEL = "myfeature";

    private final Messenger messenger;

    public MyFeatureCommand(Messenger messenger) {
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> createNode() {
        return Commands.literal(MyFeatureCommand.LABEL)
                .requires(MyFeatureCommand::canUse)
                .executes(this::sendHelp)
                .then(Commands.literal("here").executes(this::showPosition));
    }

    @Override
    public String description() {
        return "Manage My Feature of the GamingBytez Enhancements plugin";
    }

    @Override
    public List<String> aliases() {
        return List.of("gbmf");
    }

    private static boolean canUse(CommandSourceStack source) {
        return source.getSender().hasPermission(MyFeatureCommand.ADMIN_PERMISSION);
    }

    private int sendHelp(CommandContext<CommandSourceStack> context) {
        CommandHelp.send(context.getSource(), this.messenger, context.getNodes().getFirst().getNode());

        return CommandResults.SUCCESS;
    }

    private int showPosition(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (CommandSources.playerExecutor(source).isEmpty()) {
            this.messenger.error(source.getSender(), "This command can only be used by a player.");

            return CommandResults.FAILURE;
        }

        this.messenger.field(source.getSender(), "Position", LocationFormat.format(source.getLocation()));

        return CommandResults.SUCCESS;
    }
}
```

The feature registers the command through `CommandRegistrar`, which owns the
`LifecycleEvents.COMMANDS` wiring. `CommandRegistrar` is the only class in the plugin allowed to
reference that lifecycle event.

```java
private void registerCommands() {
    new CommandRegistrar(this.plugin).register(new MyFeatureCommand(this.messenger));
}
```

Rules that hold for every command:

- Return `CommandResults.SUCCESS` and `CommandResults.FAILURE`, never a bare `1`, `0` or
  `Command.SINGLE_SUCCESS`. A rejected or failed operation returns `FAILURE`.
- Get the acting player from `CommandSources.playerExecutor(source)`, which returns an `Optional`.
  It reads the executor rather than the sender, so `/execute as <player>` keeps working.
- Build live suggestions with `CommandSuggestions.fromSupplier(...)`. The supplier is asked again on
  every invocation and the candidates are filtered case-insensitively against what the sender typed,
  so a suggestion list never goes stale.
- Render help with `CommandHelp.send(...)` from the invoked node, never from a hardcoded string.
  Help read from the live tree cannot drift from the grammar, and it prints the label the operator
  actually typed, so an alias renders as `/gbmcp ...`.
- Declare the permission node in `src/main/resources/paper-plugin.yml` and check it in `requires(...)`
  on the root literal, so an unpermitted sender never sees the branch.
- Keep the wording out of the handlers. A feature holds it in a `messages` package beside the
  packages that speak it, as `feature/minecartportal/messages/MinecartPortalMessages.java` does
  with its `Component` factories. Messages word and never validate: a rule like a name pattern
  lives on the model, and the messages class at most reads a constant from it.

Where a command grows past a handful of subcommands, split it the way the Minecart Portal command
does: the root class owns the literal, the permission, the help executor and composition only, and
each collaborator returns a complete `LiteralArgumentBuilder<CommandSourceStack>` that the root
attaches with a plain `.then(...)`.

### Player-Facing Messages

Presentation mechanics live in `lib/message/` and contain no wording. Wording stays in the feature
that says it.

`MessagePalette` is the semantic colour vocabulary. Pick a token by the role the text plays, never by
the colour you want, so a role can be recoloured plugin-wide in one place.

| Token | Colour | Role |
| --- | --- | --- |
| `DECORATION` | `DARK_GRAY` | brackets, bullets, separators |
| `BODY` | `GRAY` | neutral copy and item lore body |
| `VALUE` | `WHITE` | data values inside a line |
| `SUBJECT` | `YELLOW` | the named thing an operation acts on |
| `HEADING` | `AQUA` | section headings |
| `EMPHASIS` | `GOLD` | calls to action, broadcasts, recoverable problems |
| `SUCCESS` | `GREEN` | a completed mutation |
| `ERROR` | `RED` | a rejected or failed operation |

A feature brand colour is deliberately not a palette token. Each feature picks its own colour and
hands it to `MessagePrefix.of(featureName, brandColor)`, which is what lets a player tell two
features apart at a glance.

A feature constructs exactly one `Messenger` in its constructor and injects it into every command,
listener and recipe that talks to a player. Do not build a second one, and do not call
`Audience#sendMessage` with a hand-assembled line.

```java
private static final NamedTextColor BRAND_COLOR = NamedTextColor.LIGHT_PURPLE;

this.messenger = new Messenger(MessagePrefix.of("MyFeature", MyFeature.BRAND_COLOR));
```

`Messenger` sends to an Adventure `Audience`, so command senders, players and broadcasts all use one
API. Its line vocabulary is semantic, and the palette decides how each line looks:

- Prefixed lines opening a message: `heading`, `info`, `success`, `warning`, `error`.
- Unprefixed indented continuation lines: `detail`, `bullet`, `field`.
- `prefixed(Component)` returns the prefixed line for a caller that delivers it itself.

**Every player-facing line carries its feature prefix.** A line without a prefix cannot be traced
back to the plugin that sent it.

**The chat bot is the one deliberate exemption to that rule.** `feature/chatbot` sends no prefix and
uses no palette colour, because its output is meant to be indistinguishable from a message a player
typed. A prefix would announce it as plugin output and a palette colour would tint a line that
vanilla renders white, so either change would defeat the feature. This is intentional and must not be
"fixed".

Two further rules:

- Item display names keep their own identity colours, because the name colour signals what the item
  is, which is not a message role. Only item lore uses palette tokens.
- Render a location with `LocationFormat.format(location)`, which takes the world from
  `World#getKey()` and renders a null location as an italic placeholder.

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
- Brigadier-based commands: `/minecartportals` (alias `/gbmcp`), the reference implementation of `lib/command`
- Portals are written to disk after every change, and a failed write warns the operator
- Thread-safe portal management with `CopyOnWriteArrayList`
- Async configuration I/O using Bukkit scheduler

**ChatBotFeature** (`feature/chatbot/`):
- Multiple weighted actions with chance-based triggering
- ExecutorService for async message processing
- OpenAI integration for AI-powered responses
- Rate limiting (60s between AI requests)
- Exempt from the message foundation, see the chat bot exemption under Player-Facing Messages

## Configuration

### Main Config (`config.yml`)

Contains chatbot settings and OpenAI credentials:
```yaml
chatbot:
  enable_ai_answers: false
  system_prompt: ""       # Optional; when non-empty, sent as system-role message in API requests
  disable_thinking: false  # When true, sends chat_template_kwargs to disable model thinking
openai:
  apiUrl: "https://api.openai.com/v1/chat/completions"
  apiKey: ""
  organizationId: ""
  model: "gpt-3.5-turbo"
  temperature: 1.0
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
version: '${project.version}'
main: de.lazybytez.gamingbytezenhancements.EnhancementsPlugin
api-version: '1.21'
```

The same file declares the permission nodes. A command that checks a permission in `requires(...)`
must have that node declared here with its default.

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

## Documentation

- `docs/` holds the feature and configuration documentation aimed at server operators.

## Branch Naming

- `main` - Default branch (use for PRs)
- `feature/*` - New features
- `fix/*` - Bug fixes