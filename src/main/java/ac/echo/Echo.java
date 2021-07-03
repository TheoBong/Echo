package ac.echo;

import ac.echo.classes.Storage;
import ac.echo.commands.BaseCommand;
import ac.echo.commands.impl.AltsCommand;
import ac.echo.commands.impl.EchoCommand;
import ac.echo.commands.impl.FreezeCommand;
import ac.echo.commands.impl.KeyCommand;
import ac.echo.listeners.DisconnectEvent;
import ac.echo.listeners.FreezeListener;
import ac.echo.listeners.LoginEvent;
import ac.echo.listeners.MoveEvent;
import ac.echo.profile.Manager;
import ac.echo.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Echo extends JavaPlugin {
    public static Echo INSTANCE;
    private Set<BaseCommand> commands;
    private CommandMap commandMap;
    @Getter private Manager profileManager;
    @Getter @Setter Boolean serverScanning = false;

    @Getter Storage storage;

    File configFile;
    @Getter private YamlConfiguration config;

    @Override
    public void onEnable() {
        INSTANCE = this;

        profileManager = new Manager();

        commands = new HashSet<>();

        storage = new Storage();
        storage.importConfig();

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new DisconnectEvent(this), this);
        getServer().getPluginManager().registerEvents(new LoginEvent(this), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);

        registerCommand(new EchoCommand(this));

        if (getConfig().getBoolean("FREEZE_COMMAND.PLAYER_MOVE_EVENT")) {
            getServer().getPluginManager().registerEvents(new MoveEvent(this), this);
        }

        if (getConfig().getBoolean("ALTS_COMMAND.ENABLED")) {
            registerCommand(new AltsCommand(this));
        }

        if (getConfig().getBoolean("FREEZE_COMMAND.ENABLED")) {
            registerCommand(new FreezeCommand(this));
        }

        if (getConfig().getBoolean("KEY_COMMAND.ENABLED")) {
            registerCommand(new KeyCommand(this));
        }
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> {
            Profile profile = getProfileManager().getProfile(player.getUniqueId());
            if (profile.isFrozen()) {
                player.setFlying(false);
                player.setWalkSpeed(0.2F);
                player.setFoodLevel(20);
                player.setSprinting(true);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        });

        storage.exportConfig();
        getServer().getScheduler().cancelTasks(this);
    }


    public Echo registerCommand(BaseCommand command) {
        commands.add(command);
        commandMap.register(command.getName(), command);
        return this;
    }
}