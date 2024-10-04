package de.lazybytez.gamingbytezenhancements.feature.minecartportal.command;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.MinecartPortalFeature;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Command to manage Minecart Portals.
 * <p>
 * This command allows to create and delete Minecart Portals.
 * It allows to set and move the locations of a portal.
 * <p>
 * The command is kept pretty simple as it is an administrative command anyway.
 */
public class MinecartPortalCommand implements BasicCommand {
    private final MinecartPortalFeature feature;

    public MinecartPortalCommand(MinecartPortalFeature feature) {
        this.feature = feature;
    }

    @Override
    public void execute(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();

        if (args.length < 1) {
            sender.sendMessage("Invalid number of arguments.");
            sender.sendMessage("You must at least provide a subcommand.");
            sender.sendMessage("Valid subcommands: add, entry, exit, delete, list, inspect, save, reload");

            return;
        }

        String subCommand = args[0];

        switch (subCommand) {
            case "add":
                this.handleAddSubCommand(css, args);
                break;
            case "entry":
                this.handleEntrySubCommand(css, args);
                break;
            case "exit":
                this.handleExitSubCommand(css, args);
                break;
            case "delete":
                this.handleDeleteSubCommand(css, args);
                break;
            case "list":
                this.handleListSubCommand(css, args);
                break;
            case "inspect":
                this.handleInspectSubCommand(css, args);
                break;
            case "save":
                this.handleSaveSubCommand(css, args);
                break;
            case "reload":
                this.handleReloadSubCommand(css, args);
                break;
            default:
                sender.sendMessage("The provided subcommand does not exist. Valid are: add, set and delete");
                break;
        }
    }

    /**
     * Handle the "add" subcommand that allows to initialize a new portal.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleAddSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();
        if (args.length != 2) {
            sender.sendMessage("Invalid number of arguments, format is: /minecartportal add <name>");
            return;
        }

        String name = args[1];
        if (name.length() > 16) {
            sender.sendMessage("Please choose a name with <= 16 characters!");
            return;
        }

        if (!name.matches("[A-Za-z0-9]+")) {
            sender.sendMessage("The name of a portal must be alphanumeric!");
            return;
        }

        sender.sendMessage("Adding portal with name \"" + name + "\"...");
        MinecartPortal portal = new MinecartPortal(name, null, null);

        // Portal adding is synchronized. Run it async to prevent any performance impact
        this.feature.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.feature.getPlugin(), () -> {
            boolean added = this.feature.getPortalConfig().addPortal(portal);
            this.feature.getPlugin().getServer().getScheduler().runTask(this.feature.getPlugin(), () -> {
                if (!added) {
                    sender.sendMessage("A portal with name \"" + name + "\" already exists!");
                    return;
                }
                sender.sendMessage("Added portal with name \"" + name + "\"!");
                this.feature.getPlugin().getLogger().info("The player "
                                + sender.getName()
                                + " added a Minecart Portal with name \"" + name + "\""
                );
            });
        });
    }

    /**
     * Handle the "entry" subcommand that allows to set the entrypoint of a portal.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleEntrySubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();

        if (args.length != 2) {
            sender.sendMessage("Invalid number of arguments, format is: /minecartportal entry <name>");
            return;
        }

        String name = args[1];
        MinecartPortal portal = this.feature.getPortalConfig().getPortalByName(name);
        if (portal == null) {
            sender.sendMessage("Could not find a Minecart Portal with name \"" + name + "\"!");
            return;
        }

        Location location = css.getLocation();
        if (!location.getBlock().getType().equals(Material.DETECTOR_RAIL)) {
            sender.sendMessage("The entrypoint of a Minecart Portal can only be placed on a detector rail!");
            return;
        }

        MinecartPortal updatedPortal = new MinecartPortal(
                portal.getName(),
                location,
                portal.getDestination()
        );

        sender.sendMessage("Updating entrypoint of the Minecart Portal \"" + name + "\"...");
        this.feature.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.feature.getPlugin(), () -> {
            boolean updated = this.feature.getPortalConfig().updatePortal(updatedPortal);
            this.feature.getPlugin().getServer().getScheduler().runTask(this.feature.getPlugin(), () -> {
                if (!updated) {
                    sender.sendMessage("Failed to place entrypoint of the Minecart Portal \"" + name + "\"!");
                    return;
                }

                sender.sendMessage("Successfully placed entrypoint of the Minecart Portal \"" + name + "\"!");
                this.feature.getPlugin().getLogger().info("The player "
                        + sender.getName()
                        + " set the entrypoint of the Minecart Portal with name \"" + name + "\" to location: "
                        + location
                );
            });
        });
    }

    /**
     * Handle the "exit" subcommand that allows to set the exit point of a portal.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleExitSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();

        if (args.length != 2) {
            sender.sendMessage("Invalid number of arguments, format is: /minecartportal exit <name>");
            return;
        }

        String name = args[1];
        MinecartPortal portal = this.feature.getPortalConfig().getPortalByName(name);
        if (portal == null) {
            sender.sendMessage("Could not find a Minecart Portal with name \"" + name + "\"!");
            return;
        }

        Location location = css.getLocation();
        MinecartPortal updatedPortal = new MinecartPortal(
                portal.getName(),
                portal.getPortal(),
                location
        );

        sender.sendMessage("Updating exit point of the Minecart Portal \"" + name + "\"...");
        this.feature.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.feature.getPlugin(), () -> {
            boolean updated = this.feature.getPortalConfig().updatePortal(updatedPortal);
            this.feature.getPlugin().getServer().getScheduler().runTask(this.feature.getPlugin(), () -> {
                if (!updated) {
                    sender.sendMessage("Failed to place exit point of the Minecart Portal \"" + name + "\"!");
                    return;
                }

                sender.sendMessage("Successfully placed exit point of the Minecart Portal \"" + name + "\"!");
                this.feature.getPlugin().getLogger().info("The player "
                        + sender.getName()
                        + " set the exit point of the Minecart Portal with name \"" + name + "\" to location: "
                        + location
                );
            });
        });
    }

    /**
     * Handle the "delete" subcommand that allows to delete a portal.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleDeleteSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();

        if (args.length != 2) {
            sender.sendMessage("Invalid number of arguments, format is: /minecartportal exit <name>");
            return;
        }

        String name = args[1];
        MinecartPortal portal = this.feature.getPortalConfig().getPortalByName(name);
        if (portal == null) {
            sender.sendMessage("Could not find a Minecart Portal with name \"" + name + "\"!");
            return;
        }

        sender.sendMessage("Deleting the Minecart Portal \"" + name + "\"...");
        this.feature.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.feature.getPlugin(), () -> {
            boolean updated = this.feature.getPortalConfig().deletePortal(portal);
            this.feature.getPlugin().getServer().getScheduler().runTask(this.feature.getPlugin(), () -> {
                if (!updated) {
                    sender.sendMessage("Failed to delete the Minecart Portal \"" + name + "\"!");
                    return;
                }

                sender.sendMessage("Successfully deleted the Minecart Portal \"" + name + "\"!");
                this.feature.getPlugin().getLogger().info("The player "
                        + sender.getName()
                        + " deleted theMinecart Portal with name \"" + name + "\""
                );
            });
        });
    }

    /**
     * Handle the "list" subcommand that allows to list registered portals.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleListSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();

        List<MinecartPortal> portals = this.feature.getPortalConfig().getPortals();
        if (portals.isEmpty()) {
            sender.sendMessage("There are currently no Minecart Portals registered.");
            return;
        }

        sender.sendMessage("The following Minecart Portals are currently registered:");
        sender.sendMessage(String.join(", ", portals.stream().map(MinecartPortal::getName).toList()));
    }

    /**
     * Handle the "inspect" subcommand that allows to inspect registered portals.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleInspectSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();
        if (args.length != 2) {
            sender.sendMessage("Invalid number of arguments, format is: /minecartportal inspect <name>");
            return;
        }

        String name = args[1];
        MinecartPortal portal = this.feature.getPortalConfig().getPortalByName(name);
        if (portal == null) {
            sender.sendMessage("Could not find a Minecart Portal with name \"" + name + "\"!");
            return;
        }

        sender.sendMessage(" === Minecart Portal Inspection ===");
        sender.sendMessage("Name: " + portal.getName());

        String portalLocation = portal.getPortal() == null ? "Not Set" : portal.getPortal().toString();
        String destinationLocation = portal.getDestination() == null ? "Not Set" : portal.getDestination().toString();

        sender.sendMessage("Portal: " + portalLocation);
        sender.sendMessage("Destination: " + destinationLocation);
    }

    /**
     * Handle the "save" subcommand that allows to save portals to the portal storage.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleSaveSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();
        sender.sendMessage("Saving portals to portal storage...");

        if (args.length != 1) {
            sender.sendMessage("The save subcommand does not expect any additional arguments!");
            return;
        }

        this.feature.getPlugin().getLogger().info("The player "
                + sender.getName()
                + " triggered a save of all Minecart Portals to the storage file"
        );

        this.feature.getPortalConfig().saveAsync(success -> {
            // Resync message send to main thread
            this.feature.getPlugin().getServer().getScheduler().runTask(this.feature.getPlugin(), () -> {
                if (success) {
                    sender.sendMessage("Successfully saved portals to portal storage!");
                    return;
                }

                sender.sendMessage("Failed to save portals to portal storage!");
            });
        });
    }

    /**
     * Handle the "reload" subcommand that allows to reload portals from portal storage.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleReloadSubCommand(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();
        sender.sendMessage("Reloading portals from portal storage...");

        if (args.length != 1) {
            sender.sendMessage("The reload subcommand does not expect any additional arguments!");
            return;
        }

        this.feature.getPortalConfig().loadAsync(success -> {
            // Resync message send to main thread
            this.feature.getPlugin().getServer().getScheduler().runTask(this.feature.getPlugin(), () -> {
                if (success) {
                    sender.sendMessage("Successfully reloaded portals from portal storage!");
                    return;
                }

                sender.sendMessage("Failed to reload portals from portal storage!");
            });
        });
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player && sender.isOp();
    }

//    protected MinecartPortalCommand() {
//        super(
//                "minecartportals",
//                "Manage the Minecart Portals of the GamingBytez Enhancements plugin",
//                "/minecart portals <command> <portal-name>",
//                List.of("gbmcp")
//        );
//    }

}
