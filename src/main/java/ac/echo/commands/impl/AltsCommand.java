package ac.echo.commands.impl;

import ac.echo.Echo;
import ac.echo.classes.API;
import ac.echo.commands.BaseCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AltsCommand extends BaseCommand {

    private Echo echo;

    public AltsCommand(Echo echo) {
        super("alts");
        this.echo = echo;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("echo.alts")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("NO_PERMISSION")));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /alts <player>");
            return;
        }

        getAlts(args[0], sender);
    }

    private void getAlts(String p, CommandSender sender) {
        new Thread(() -> {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("ALTS_COMMAND.MESSAGE")
                    .replace("{player}", p)));

            API api = new API();

            String final_alt_list = String.join(", ", api.getAlts(echo.getApikey(), p, sender));

            if (!final_alt_list.contains(", ")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        echo.getConfig().getString("ALTS_COMMAND.NO_ALTS_MESSAGE")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        echo.getConfig().getString("ALTS_COMMAND.FINISH_MESSAGE")
                        .replace("{alts}", final_alt_list)));
            }
        }).start();
    }
}