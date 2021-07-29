package ac.echo.commands.impl;

import ac.echo.commands.BaseCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class EchoCommand extends BaseCommand {
    public EchoCommand() {
        super("echo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Echo API Implementation made with love by Cowings");
        sender.sendMessage(ChatColor.GRAY + "Repository: github.com/Cowings/Echo");
    }
}