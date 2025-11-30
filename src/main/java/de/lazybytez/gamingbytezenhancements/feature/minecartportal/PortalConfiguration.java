package de.lazybytez.gamingbytezenhancements.feature.minecartportal;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Service that handles portal configuration.
 * <p>
 * This service deals with the configuration of portals.
 * It is expected that the number of portals per server will be limited.
 * Therefore, portals are stored in a dedicated configuration file.
 * <p>
 * For improved performance, all portals are loaded during startup
 * and cached throughout the server's lifetime.
 */
public class PortalConfiguration {
    /**
     * Name of the configuration file that will hold all Minecart Portals.
     */
    protected static final String PORTAL_CONFIG_FILE = "minecart_portals.yaml";

    /**
     * Key in the YAML configuration that stores the portals.
     */
    protected static final String PORTAL_CONFIG_KEY = "portals";

    /**
     * Plugin instance used to locate the portal configuration file.
     */
    private final Plugin plugin;

    /**
     * The config instance that was last loaded.
     */
    private final YamlConfiguration config;

    /**
     * The list of available portals.
     */
    private volatile CopyOnWriteArrayList<MinecartPortal> portals;

    public PortalConfiguration(Plugin plugin) {
        this.plugin = plugin;
        this.config = new YamlConfiguration();
        this.portals = new CopyOnWriteArrayList<>();
    }

    /**
     * Add a new portal to the available Minecart portals.
     *
     * @param portal is the portal to add
     * @return whether the portal has been added or not
     */
    public synchronized boolean addPortal(MinecartPortal portal) {
        if (!this.hasPortal(portal.getName())) {
            this.portals.add(portal);

            return true;
        }

        return false;
    }

    /**
     * Update a portal with a new version.
     * <p>
     * This method updates a specific portal with a new version.
     * The method will replace the original portal instance with the supplied one.
     * Matching happens by name.
     *
     * @param portal is the portal to update
     * @return whether the portal has been updated or not
     */
    public synchronized boolean updatePortal(MinecartPortal portal) {
        boolean updated = false;
        for (int i = 0; i < this.portals.size(); i++) {
            if (this.portals.get(i).getName().equals(portal.getName())) {
                this.portals.set(i, portal);
                updated = true;
                break;
            }
        }

        return updated;
    }

    /**
     * Delete a portal from the known portals.
     * <p>
     * This method deletes the provided portal from the portal list.
     * Matching happens by name.
     *
     * @param portal the portal to delete
     * @return whether the portal could be deleted or not
     */
    public synchronized boolean deletePortal(MinecartPortal portal) {
        boolean deleted = false;
        int portalIndex = 0;

        for (int i = 0; i < this.portals.size(); i++) {
            if (this.portals.get(i).getName().equals(portal.getName())) {
                portalIndex = i;
                deleted = true;
                break;
            }
        }

        if (deleted) {
            this.portals.remove(portalIndex);
        }

        return deleted;
    }

    /**
     * Check if a portal with the given name exists
     *
     * @param name is the name of the portal to search for
     * @return whether a portal with the given name exists or not.
     */
    public boolean hasPortal(String name) {
        boolean portalExists = false;

        for (MinecartPortal portal : this.portals) {
            if (portal.getName().equals(name)) {
                portalExists = true;
                break;
            }
        }

        return portalExists;
    }

    /**
     * Get a portal at a specific location
     *
     * @param location the location of the portal to get
     * @return the found portal or null
     */
    public MinecartPortal getPortalAtLocation(Location location) {
        MinecartPortal portal = null;

        for (MinecartPortal currentPortal : this.portals) {
            if (currentPortal.getPortal() == null) {
                continue;
            }

            if (location.getWorld() != currentPortal.getPortal().getWorld()) {
                continue;
            }

            if (currentPortal.getPortal().distance(location) < 1.0) {
                portal = currentPortal;
                break;
            }
        }

        return portal;
    }

    /**
     * Get a portal by its name
     *
     * @param name is the name of the portal to get
     * @return the found portal or null
     */
    public MinecartPortal getPortalByName(String name) {
        MinecartPortal portal = null;

        for (MinecartPortal currentPortal : this.portals) {
            if (currentPortal.getName().equals(name)) {
                portal = currentPortal;
                break;
            }
        }

        return portal;
    }

    /**
     * Get all currently registered (not only saved!) portals.
     *
     * @return all currently registered portals
     */
    public List<MinecartPortal> getPortals() {
        return List.copyOf(this.portals);
    }

    /**
     * Load the configuration from disk synchronously.
     * <p>
     * This function loads the configuration synchronously from disk.
     * If the configuration file does not exist, it is created and prepared to store
     * portals.
     * <p>
     * This function may not be used during server operation.
     * Use the asynchronous load function instead to ensure smooth operation.
     *
     * @throws IOException                   if any file operation fails
     * @throws InvalidConfigurationException when the configuration format is invalid
     */
    public synchronized void loadSync() throws IOException, InvalidConfigurationException {
        // Load config ion a thread safe manner
        try {
            this.plugin.getLogger().info("Loading minecart portals file...");

            File file = this.getConfigurationFile();
            this.ensureFileExists(file);

            // We do not use YamlConfiguration.loadConfiguration as we want to handle
            // exceptions our own. this.config is initialized in constructor
            this.config.load(file);

            this.plugin.getLogger().info("Loaded minecart portals file!");
        } catch (IOException | InvalidConfigurationException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to load minecart portals file!",
                    e
            );

            throw e;
        }


        // Handle fresh configuration that is still empty
        boolean portalsConfigured = config.isSet(PortalConfiguration.PORTAL_CONFIG_KEY);

        if (!portalsConfigured) {
            this.plugin.getLogger().info("Minecart Portal storage is missing, adding it...");
            // In this case, we want to save the configuration
            // Constructor of class ensures we have an empty list just in case
            this.saveSync();
            this.plugin.getLogger().info("Added portal storage to Minecart Portal storage file!");
        }

        this.plugin.getLogger().info("Parsing portal instances from portal storage...");
        this.portals = this.getPortalsFromConfig(config);
        this.plugin.getLogger().info("Finished parsing portal instances from portal storage!");
    }

    /**
     * Try to load the configuration asynchronously.
     * <p>
     * This method tries to load the configuration asynchronously using the
     * Bukkit scheduler. If loading the configuration fails, the issue will
     * be logged to the console.
     * <p>
     * As long as the initial load of the configuration was successful, a failed
     * reload of the configuration should not cause any issues.
     *
     * @param callback A consumer used as callback called after load.
     *                 The boolean parameter indicates success of the save operation.
     */
    public void loadAsync(Consumer<Boolean> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                this.loadSync();
                callback.accept(true);
            } catch (IOException | InvalidConfigurationException e) {
                this.plugin.getLogger().log(
                        Level.SEVERE,
                        "Failed to load minecart portal configuration asynchronously!",
                        e
                );
                callback.accept(false);
            }
        });
    }

    /**
     * Get portals from the configuration in a type safe manner.
     *
     * @param config the config to load the portals from
     * @return a list of minecart portal instances
     */
    private CopyOnWriteArrayList<MinecartPortal> getPortalsFromConfig(YamlConfiguration config) {
        List<?> rawPortals = config.getList(PortalConfiguration.PORTAL_CONFIG_KEY, new ArrayList<>());

        CopyOnWriteArrayList<MinecartPortal> loadedPortals = new CopyOnWriteArrayList<>();
        for (Object o : rawPortals) {
            if (o instanceof MinecartPortal) {
                loadedPortals.add((MinecartPortal) o);
            }
        }

        return loadedPortals;
    }

    /**
     * Saves the configuration file synchronously.
     * <p>
     * This method saves the current configuration synchronously.
     * This includes updating the portals value in the configuration
     * with the current portal list of this configuration.
     * <p>
     * As this function is synchronous, it should only be used during
     * plugin initialization. Please use asynchronous functions
     * during normal operation.
     *
     * @throws IOException when the configuration could not be saved
     */
    public synchronized void saveSync() throws IOException {
        File file = this.getConfigurationFile();

        // Update config value with cache
        try {
            this.plugin.getLogger().info("Saving Minecart Portals to file...");

            this.config.set(PortalConfiguration.PORTAL_CONFIG_KEY, this.portals);
            int portalCount = this.portals.size();

            // Save
            this.config.save(file);

            this.plugin.getLogger().info("Saved " + portalCount + " Minecart Portals in the configuration file!");
        } catch (IOException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to save minecart portals file!",
                    e
            );

            throw e;
        }
    }

    /**
     * Try to save the configuration asynchronously.
     * <p>
     * This method tries to sae the configuration asynchronously using the
     * Bukkit scheduler. If saving the configuration fails, the issue will
     * be logged to the console.
     *
     * @param callback A consumer used as callback called after save.
     *                 The boolean parameter indicates success of the save operation.
     */
    public void saveAsync(Consumer<Boolean> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                this.saveSync();
                callback.accept(true);
            } catch (IOException e) {
                this.plugin.getLogger().log(
                        Level.SEVERE,
                        "Failed to save minecart portal configuration asynchronously!",
                        e
                );
                callback.accept(false);
            }
        });
    }

    /**
     * Ensure that the portal configuration file exists.
     *
     * @param configFile is the file that should be created (if it does not exist)
     * @throws IOException thrown when the directory path or file could not be created
     */
    private void ensureFileExists(File configFile) throws IOException {
        // Ignore result of mkdirs, as in this case it does not matter if
        // directories where actually created or not.
        //noinspection ResultOfMethodCallIgnored
        configFile.getParentFile().mkdirs();

        if (!configFile.exists()) {
            // Here the same is true as for mkdirs, the result does not matter.
            // Only exceptions matter.
            //noinspection ResultOfMethodCallIgnored
            configFile.createNewFile();
        }
    }

    /**
     * Get a file instance pointing to the portal configuration files location.
     *
     * @return the file instance pointing to the configuration file.
     */
    private File getConfigurationFile() {
        return new File(this.plugin.getDataFolder(), PortalConfiguration.PORTAL_CONFIG_FILE);
    }
}
