package ac.echo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ac.echo.Echo;

public class EchoCommand implements CommandExecutor {

    private Echo echo;

    public EchoCommand(Echo echo) {
        this.echo = echo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String enterprise = echo.getEnterprise() ? "Enterprise" : "Personal";

        sender.sendMessage("This version of Echo is licensed to " + echo.getUsername());
        sender.sendMessage("Current plan: " + enterprise);

        return true;
    }
}
