package ac.echo;

import ac.echo.classes.API;
import ac.echo.commands.BaseCommand;
import ac.echo.commands.impl.AltsCommand;
import ac.echo.commands.impl.FreezeCommand;
import ac.echo.listeners.DisconnectEvent;
import ac.echo.listeners.FreezeListener;
import ac.echo.listeners.LoginEvent;
import ac.echo.listeners.MoveEvent;
import ac.echo.profile.Manager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ac.echo.commands.impl.EchoCommand;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
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

    private File file;
    @Getter private YamlConfiguration config;

    @Override
    public void onEnable() {
        INSTANCE = this;

        profileManager = new Manager(this);

        commands = new HashSet<>();

        file = new File(getDataFolder(), "config.yml");
        saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(file);

        if (getConfig().getString("API_KEY") == null) {
            getLogger().severe("API Key not found! Please enter an API key in the config.");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            API api = new API();
            String key = getConfig().getString("API_KEY");

            if (!api.isValidKey(key)) {
                getLogger().severe("Disabling Echo Plugin due to invalid API key.");
                getServer().getPluginManager().disablePlugin(this);
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
        this.getServer().getScheduler().cancelTasks(this);
    }


    public Echo registerCommand(BaseCommand command) {
        commands.add(command);
        commandMap.register(command.getName(), command);
        return this;
    }
}