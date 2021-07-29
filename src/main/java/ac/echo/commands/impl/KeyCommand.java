package ac.echo.commands.impl;

import ac.echo.Echo;
import ac.echo.classes.API;
import ac.echo.commands.BaseCommand;
import ac.echo.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeyCommand extends BaseCommand {

    private final Echo echo;

    public KeyCommand(Echo echo) {
        super(echo.getConfig().getString("KEY_COMMAND.MAIN_COMMAND"));
        this.echo = echo;
        setAliases(echo.getConfig().getStringList("KEY_COMMAND.ALIASES"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(echo.getConfig().getString("KEY_COMMAND.PERMISSION"))) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("NO_PERMISSION")));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /key <api-key>");
            return;
        }

        updateKey(sender, args[0]);

    }

    private void updateKey(CommandSender sender, String key){
        ThreadUtil.runTask(true, echo, () -> {
            API api = new API();

            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (api.isValidKey(key)){
                    if (!echo.getConfig().getBoolean("FREEZE_COMMAND.ALLOW_MULTIPLE_STAFF_PER_KEY") && echo.getStorage().keyUsed(key)) {
                        sender.sendMessage(ChatColor.RED + "This API key is already in use and cannot be used twice!");
                        return;
                    }

                    echo.getStorage().addUser(p.getUniqueId().toString(), key);
                    p.sendMessage(ChatColor.GREEN + "Success!");
                } else {
                    p.sendMessage(ChatColor.RED + "Invalid Key!");
                }
            } else {

                if (!echo.getConfig().getBoolean("FREEZE_COMMAND.ALLOW_MULTIPLE_STAFF_PER_KEY") && echo.getStorage().keyUsed(key)) {
                    sender.sendMessage(ChatColor.RED + "This API key is already in use and cannot be used twice!");
                    return;
                }

                if (api.isValidKey(key)){
                    echo.getStorage().addConsole(key);
                    sender.sendMessage(ChatColor.GREEN + "Success!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid Key!");
                }
            }
        });
    }
}
