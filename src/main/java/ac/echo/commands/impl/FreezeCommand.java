package ac.echo.commands.impl;

import ac.echo.Echo;
import ac.echo.classes.API;
import ac.echo.classes.EchoClient;
import ac.echo.commands.BaseCommand;
import ac.echo.profile.Profile;
import com.neovisionaries.ws.client.WebSocketException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class FreezeCommand extends BaseCommand {

    private Echo echo;

    private boolean wasFlying = false;
    private float walkSpeed = 0.2F;
    private int foodLevel = 20;

    public FreezeCommand(Echo echo) {
        super("freeze");
        this.echo = echo;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("echo.pin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("NO_PERMISSION")));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /freeze <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Can't find player!");
            return;
        }

        Profile profile = echo.getProfileManager().getProfile(target.getUniqueId());

        if (!profile.isFrozen()) {
            profile.setFrozen(true);
            sender.sendMessage(ChatColor.GREEN + "Successfully froze " + target.getName());

            wasFlying = target.isFlying();
            walkSpeed = target.getWalkSpeed();
            foodLevel = target.getFoodLevel();

            target.setFlying(false);
            target.setWalkSpeed(0.0F);
            target.setFoodLevel(0);
            target.setSprinting(false);
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));

            getPin(sender, target);
        } else {
            profile.setFrozen(false);
            profile.setStarted(false);
            sender.sendMessage(ChatColor.GREEN + "Successfully unfroze " + target.getName());

            target.sendMessage(ChatColor.GREEN + "You have been unfrozen.");
            target.setFlying(wasFlying);
            target.setWalkSpeed(walkSpeed);
            target.setFoodLevel(foodLevel);
            target.setSprinting(true);
            target.removePotionEffect(PotionEffectType.JUMP);
        }
    }

    private void getPin(CommandSender sender, Player target){
        API api = new API();

        String pin = api.getPin(echo.getApikey(), sender);
        String link = api.getLink(echo.getApikey(), sender);

        long expiry = System.currentTimeMillis() + echo.getConfig().getInt("FREEZE_COMMAND.AUTOBAN.TIME_BEFORE_AUTOBAN");

        if (echo.getConfig().getBoolean("FREEZE_COMMAND.AUTOBAN.ENABLED")) {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("FREEZE_COMMAND.MESSAGE")
                            .replace("{link}", link).replace("{countdown}", "" + (expiry - System.currentTimeMillis())/1000)));
        } else {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("FREEZE_COMMAND.MESSAGE")
                            .replace("{link}", link)));
        }

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(echo.getConfig().getInt("FREEZE_COMMAND.SEND_MESSAGE_EVERY"));
                } catch (InterruptedException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }

                Profile profile = echo.getProfileManager().getProfile(target.getUniqueId());
                if (!profile.isFrozen() || profile.isStarted()) {
                    break;
                }

                if (echo.getConfig().getBoolean("FREEZE_COMMAND.AUTOBAN.ENABLED")) {
                    if (System.currentTimeMillis() > expiry) {
                        CommandSender sender1 = echo.getConfig().getBoolean("FREEZE_COMMAND.AUTOBAN.AUTOBAN_COMMAND_CONSOLE") ? Bukkit.getServer().getConsoleSender() : sender;
                        Bukkit.dispatchCommand(sender1, echo.getConfig().getString("FREEZE_COMMAND.AUTOBAN.AUTOBAN_COMMAND"));
                        break;
                    }
                }

                if (echo.getConfig().getBoolean("FREEZE_COMMAND.AUTOBAN.ENABLED")) {
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            echo.getConfig().getString("FREEZE_COMMAND.MESSAGE")
                                    .replace("{link}", link).replace("{countdown}", "" + (expiry - System.currentTimeMillis())/1000)));
                } else {
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            echo.getConfig().getString("FREEZE_COMMAND.MESSAGE")
                                    .replace("{link}", link)));
                }
            }
        }).start();

        try {
            EchoClient.connect(pin, sender, target);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}