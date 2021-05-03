package ac.echo.commands;


import ac.echo.Echo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FreezeCommand implements CommandExecutor {

    private Echo echo;

    public FreezeCommand(Echo echo) {
        this.echo = echo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        return true;
    }
}
