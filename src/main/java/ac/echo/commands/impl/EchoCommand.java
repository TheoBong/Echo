package ac.echo.commands.impl;

import ac.echo.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ac.echo.Echo;

public class EchoCommand extends BaseCommand {

    private Echo echo;

    public EchoCommand(Echo echo) {
        super("echo");
        this.echo = echo;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String enterprise = echo.getEnterprise() ? "Enterprise" : "Personal";

        sender.sendMessage("This version of Echo is licensed to " + ChatColor.AQUA + echo.getUsername());
        sender.sendMessage(ChatColor.GRAY + "Current plan: " + enterprise);

        return;
    }
}
