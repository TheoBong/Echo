package ac.echo.commands.impl;

import ac.echo.Echo;
import ac.echo.classes.API;
import ac.echo.commands.BaseCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeyCommand extends BaseCommand {

    private Echo echo;

    public KeyCommand(Echo echo) {
        super("key");
        this.echo = echo;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!sender.hasPermission("echo.staff")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        echo.getConfig().getString("NO_PERMISSION")));
                return;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /key <api-key>");
                return;
            }

            updateKey(player, args[0]);
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }
    }

    private void updateKey(Player p, String key){
        new Thread(() -> {
            API api = new API();

            if (api.isValidKey(key)){
                echo.getStorage().addUser(p.getUniqueId().toString(), key);
                p.sendMessage(ChatColor.GREEN + "Success!");
            } else {
                p.sendMessage(ChatColor.RED + "Invalid Key!");
            }
        }).start();
    }
}
