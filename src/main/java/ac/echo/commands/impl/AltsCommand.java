package ac.echo.commands.impl;

import ac.echo.Echo;
import ac.echo.classes.API;
import ac.echo.commands.BaseCommand;
import ac.echo.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AltsCommand extends BaseCommand {

    private Echo echo;

    public AltsCommand(Echo echo) {
        super(echo.getConfig().getString("ALTS_COMMAND.MAIN_COMMAND"));
        this.echo = echo;
        setAliases(echo.getConfig().getStringList("ALTS_COMMAND.ALIASES"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        doEverything(sender, args);
    }

    public void doEverything(CommandSender sender, String[] args) {
        ThreadUtil.runTask(true, echo, () -> {
            if (!sender.hasPermission("echo.alts")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        echo.getConfig().getString("NO_PERMISSION")));
                return;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /alts <player>");
                return;
            }

            if (sender instanceof Player) {
                Player staff = (Player) sender;
                if (echo.getStorage().getKey(staff.getUniqueId().toString()) == null) {
                    staff.sendMessage(ChatColor.RED + "Please specify your API key using /key <api-key>");
                    return;
                }

                API api = new API();
                if (!api.isValidKey(echo.getStorage().getKey(staff.getUniqueId().toString()))) {
                    staff.sendMessage(ChatColor.RED + "Your API Key is no longer valid. Please set it again using /key <api-key>");
                    return;
                }

            } else {
                if (echo.getStorage().getConsole() == null) {
                    sender.sendMessage(ChatColor.RED + "Please specify console API key using /key <api-key>");
                    return;
                }

                API api = new API();
                if (!api.isValidKey(echo.getStorage().getConsole())) {
                    sender.sendMessage(ChatColor.RED + "Your API Key is no longer valid. Please set it again using /key <api-key>");
                    return;
                }
            }

            getAlts(args[0], sender);
        });
    }

    private void getAlts(String p, CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                echo.getConfig().getString("ALTS_COMMAND.START_MESSAGE")
                        .replace("{player}", p)));

        API api = new API();

        String final_alt_list;

        if (sender instanceof Player) {
            Player staffPlayer = (Player) sender;
            final_alt_list = String.join(", ", api.getAlts(echo.getStorage().getKey(staffPlayer.getUniqueId().toString()), p, sender));
        } else {
            final_alt_list = String.join(", ", api.getAlts(echo.getStorage().getConsole(), p, sender));
        }

        if (!final_alt_list.contains(", ")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("ALTS_COMMAND.NO_ALTS_MESSAGE")));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("ALTS_COMMAND.FINISH_MESSAGE")
                            .replace("{alts}", final_alt_list)));
        }
    }
}