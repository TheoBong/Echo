package ac.echo;

import ac.echo.classes.API;
import ac.echo.commands.AltsCommand;
import ac.echo.commands.FreezeCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ac.echo.commands.EchoCommand;

import java.io.File;

public class Echo extends JavaPlugin {
    public static Echo INSTANCE;
    @Getter @Setter Boolean enterprise;
    @Getter @Setter String username;
    @Getter String apikey;

    private File file;
    @Getter private YamlConfiguration config;

    @Override
    public void onEnable() {
        INSTANCE = this;

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

        if (getConfig().getBoolean("ALTS_COMMAND.ENABLED")) {
            getCommand("alts").setExecutor(new AltsCommand(this));
        }

        if (getConfig().getBoolean("FREEZE_COMMAND.ENABLED") && enterprise) {
            getCommand("freeze").setExecutor(new FreezeCommand(this));
        }

        if (getConfig().getBoolean("ECHO_COMMAND.ENABLED")) {
            getCommand("echo").setExecutor(new EchoCommand(this));
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
    }

}