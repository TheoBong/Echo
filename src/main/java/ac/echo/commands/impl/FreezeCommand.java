package ac.echo.commands.impl;

import ac.echo.Echo;
import ac.echo.classes.API;
import ac.echo.classes.EchoClient;
import ac.echo.commands.BaseCommand;
import ac.echo.profile.Profile;
import com.neovisionaries.ws.client.WebSocketException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class FreezeCommand extends BaseCommand {

    private Echo echo;

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

        boolean wasFlying = false;
        float walkSpeed = 0.2F;
        int foodLevel = 20;

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
            sender.sendMessage(ChatColor.GREEN + "Successfully unfroze " + target.getName());

            target.setFlying(wasFlying);
            target.setWalkSpeed(walkSpeed);
            target.setFoodLevel(foodLevel);
            target.setSprinting(true);
            target.removePotionEffect(PotionEffectType.JUMP);
        }

        return;
    }

    private void getPin(CommandSender sender, Player target){
        API api = new API();

        String pin = api.getPin(echo.getApikey(), sender);
        String link = api.getLink(echo.getApikey(), sender);

        target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                echo.getConfig().getString("FREEZE_COMMAND.START_MESSAGE")
                        .replace("{link}", link)));

        long expiry = System.currentTimeMillis() + echo.getConfig().getInt("FREEZE_COMMAND.AUTOBAN.TIME_BEFORE_AUTOBAN");

        new Thread(() -> {
            for (int i = 0; i < 60; i++) {
                if (echo.getConfig().getBoolean("FREEZE_COMMAND.AUTOBAN.ENABLED")) {
                    if (System.currentTimeMillis() > expiry) {
                        CommandSender sender1 = echo.getConfig().getBoolean("FREEZE_COMMAND.AUTOBAN.AUTOBAN_COMMAND_CONSOLE") ? Bukkit.getServer().getConsoleSender() : sender;
                        Bukkit.dispatchCommand(sender1, echo.getConfig().getString("FREEZE_COMMAND.AUTOBAN.AUTOBAN_COMMAND"));
                        break;
                    }
                }

                try {
                    Thread.sleep(echo.getConfig().getInt("FREEZE_COMMAND.SEND_MESSAGE_EVERY"));
                } catch (InterruptedException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }
                target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        echo.getConfig().getString("FREEZE_COMMAND.START_MESSAGE")
                                .replace("{link}", link)));
            }
        }).start();

        try {
            EchoClient.connect(pin, sender);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}