package de.lazybytez.gamingbytezenhancements.feature.minecartportal.command;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to manage Minecart Portals
 */
public class MinecartPortalCommand implements BasicCommand {
    private final PortalConfiguration config;

    public MinecartPortalCommand(PortalConfiguration config) {
        this.config = config;
    }

    @Override
    public void execute(CommandSourceStack css, String[] args) {
        CommandSender sender = css.getSender();

        if (args.length < 1) {
            sender.sendMessage("Inavlid number of arguments.");

            return;
        }

        String subCommand = args[0];

        switch (subCommand) {
            case "add":
                this.handleAddSubCommand(css, args);
                break;
            case "set":
                this.handleSetSubCommand(css, args);
                break;
            case "delete":
                this.handleDeleteSubCommand(css, args);
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

    }

    /**
     * Handle the "set" subcommand that allows to set locations of a portal.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleSetSubCommand(CommandSourceStack css, String[] args) {
    }

    /**
     * Handle the "delete" subcommand that allows to delete a portal.
     *
     * @param css the command source stack that provides context information
     * @param args the arguments passed to the command
     */
    private void handleDeleteSubCommand(CommandSourceStack css, String[] args) {
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
