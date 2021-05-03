package ac.echo.commands;

import ac.echo.Echo;
import ac.echo.Locale;
import ac.echo.classes.API;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AltsCommand implements CommandExecutor {

    private Echo echo;

    public AltsCommand(Echo echo) {
        this.echo = echo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("echo.alts")) {
            sender.sendMessage(Locale.NO_PERMISSION.format());
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /alts <player>");
            return true;
        }

        getAlts(args[0], sender);
        return true;
    }

    private void getAlts(String p, CommandSender sender) {
        new Thread(() -> {
            sender.sendMessage(Locale.ALTS_START_MESSAGE.format().replace("{player}", p));
            API api = new API();

            String final_alt_list = String.join(", ", api.getAlts(echo.getApikey(), p, sender));;

            if (!final_alt_list.contains(", ")) {
                sender.sendMessage(Locale.NO_ALTS_MESSAGE.format());
            } else {
                sender.sendMessage(Locale.ALTS_FINISH_MESSAGE.format().replace("{alts}", final_alt_list));
            }
        }).start();
    }
}