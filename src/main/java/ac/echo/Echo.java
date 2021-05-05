package ac.echo;

import ac.echo.classes.API;
import ac.echo.classes.Storage;
import ac.echo.commands.BaseCommand;
import ac.echo.commands.impl.AltsCommand;
import ac.echo.commands.impl.FreezeCommand;
import ac.echo.commands.impl.KeyCommand;
import ac.echo.listeners.DisconnectEvent;
import ac.echo.listeners.FreezeListener;
import ac.echo.listeners.LoginEvent;
import ac.echo.listeners.MoveEvent;
import ac.echo.profile.Manager;
import ac.echo.profile.Profile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ac.echo.commands.impl.EchoCommand;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class Echo extends JavaPlugin {
    public static Echo INSTANCE;
    private Set<BaseCommand> commands;
    private CommandMap commandMap;
    @Getter private Manager profileManager;
    @Getter @Setter Boolean enterprise;
    @Getter @Setter String username;
    @Getter String apikey;

    @Getter Storage storage;

    private File configFile;
    @Getter private YamlConfiguration config;

    @Override
    public void onEnable() {
        INSTANCE = this;

        profileManager = new Manager(this);

        commands = new HashSet<>();

        storage = new Storage();
        storage.importConfig();

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (getConfig().getString("SERVER_API_KEY") == null) {
            getLogger().severe("Server API Key not found! Please enter a server API key in the config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            API api = new API();
            String key = getConfig().getString("SERVER_API_KEY");

            if (!api.isValidKey(key)) {
                getLogger().severe("Disabling Echo Plugin due to invalid server API key.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            } else {
                apikey = key;
            }
        }

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new DisconnectEvent(), this);
        getServer().getPluginManager().registerEvents(new LoginEvent(), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(), this);

        if (getConfig().getBoolean("FREEZE_COMMAND.PLAYER_MOVE_EVENT")) {
            getServer().getPluginManager().registerEvents(new MoveEvent(), this);
        }

        registerCommand(new KeyCommand(this));

        if (getConfig().getBoolean("ALTS_COMMAND.ENABLED")) {
            registerCommand(new AltsCommand(this));
        }

        if (getConfig().getBoolean("FREEZE_COMMAND.ENABLED") && enterprise) {
            registerCommand(new FreezeCommand(this));
        }

        if (getConfig().getBoolean("ECHO_COMMAND.ENABLED")) {
            registerCommand(new EchoCommand(this));
        }
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> {
            final Profile profile = getProfileManager().getProfile(player.getUniqueId());
            if (profile.isFrozen()) {
                player.setFlying(false);
                player.setWalkSpeed(0.2F);
                player.setFoodLevel(20);
                player.setSprinting(true);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        });

        storage.exportConfig();
        this.getServer().getScheduler().cancelTasks(this);
    }


    public Echo registerCommand(BaseCommand command) {
        commands.add(command);
        commandMap.register(command.getName(), command);
        return this;
    }
}