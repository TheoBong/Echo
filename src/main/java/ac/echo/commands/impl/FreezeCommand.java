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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.Base64;

public class FreezeCommand extends BaseCommand {

    private Echo echo;

    private boolean wasFlying = false;
    private float walkSpeed = 0.2F;
    private int foodLevel = 20;

    public FreezeCommand(Echo echo) {
        super("freeze");
        this.echo = echo;
        setAliases(echo.getConfig().getStringList("FREEZE_COMMAND.ALIASES"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("echo.pin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    echo.getConfig().getString("NO_PERMISSION")));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /freeze <player> [pin]");
            return;
        }

        if (args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /freeze <player> [pin]");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        String pin = null;

        if (args.length == 2) {
            pin = args[1];
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Can't find player!");
            return;
        }

        Profile profile = echo.getProfileManager().getProfile(target.getUniqueId());

        if (!profile.isFrozen()) {

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

                Profile staffProfile = echo.getProfileManager().getProfile(staff.getUniqueId());
                if (staffProfile.isScanning()) {
                    staff.sendMessage(ChatColor.RED + "You may only freeze 1 person at a time.");
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

                if (echo.getServerScanning()) {
                    sender.sendMessage(ChatColor.RED + "Console is already scanning someone!");
                    return;
                }
            }

            profile.setFrozen(true);
            sender.sendMessage(ChatColor.GREEN + "Successfully froze " + target.getName());

            if (sender instanceof Player) {
                Player senderPlayer = (Player) sender;
                Profile senderProfile = echo.getProfileManager().getProfile(senderPlayer.getUniqueId());

                senderProfile.setScanning(true);
            } else {
                echo.setServerScanning(true);
            }

            wasFlying = target.isFlying();
            walkSpeed = target.getWalkSpeed();
            foodLevel = target.getFoodLevel();

            target.setFlying(false);
            target.setWalkSpeed(0.0F);
            target.setFoodLevel(0);
            target.setSprinting(false);
            target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));

            if (pin != null) {
                API api = new API();
                if (sender instanceof Player) {
                    Player staff = (Player) sender;
                    String style = api.styleCode(echo.getStorage().getKey(staff.getUniqueId().toString()));

                    String encodedString = Base64.getEncoder().encodeToString(pin.getBytes());

                    String link = "https://dl.echo.ac/" + style + "-" + encodedString;
                    getPin(link, pin, sender, target);
                } else {
                    String style = api.styleCode(echo.getStorage().getConsole());
                    String encodedString = Base64.getEncoder().encodeToString(pin.getBytes());

                    String link = "https://dl.echo.ac/" + style + "-" + encodedString;
                    getPin(link, pin, sender, target);
                }
            } else {
                getPin(null, null, sender, target);
            }
        } else {
            if (sender instanceof Player) {
                Player staffPlayer = (Player) sender;
                Profile staffProfile = echo.getProfileManager().getProfile(staffPlayer.getUniqueId());

                staffProfile.setScanning(false);
            } else {
                echo.setServerScanning(false);
            }

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

    private void getPin(String link, String pin, CommandSender sender, Player target){
        API api = new API();

        if (link == null) {
            if (sender instanceof ConsoleCommandSender) {
                pin = api.getPin(echo.getStorage().getConsole(), sender);
                link = api.getLink(echo.getStorage().getConsole(), sender);
            } else {
                Player staff = (Player) sender;

                pin = api.getPin(echo.getStorage().getKey(staff.getUniqueId().toString()), sender);
                link = api.getLink(echo.getStorage().getKey(staff.getUniqueId().toString()), sender);
            }
        }

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

        String finalLink = link;
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
                                    .replace("{link}", finalLink).replace("{countdown}", "" + (expiry - System.currentTimeMillis())/1000)));
                } else {
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            echo.getConfig().getString("FREEZE_COMMAND.MESSAGE")
                                    .replace("{link}", finalLink)));
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