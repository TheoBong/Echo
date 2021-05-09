package ac.echo.classes;

import ac.echo.Echo;
import ac.echo.profile.Profile;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;

public class EchoClient {
    private static final String SERVER = "wss://scanner.echo.ac";

    public static WebSocket connect(String pin, CommandSender staff, Player target)
            throws IOException, WebSocketException {
        String text = pin + "|no|progress";

        return new WebSocketFactory().createSocket(SERVER).addProtocol("progress").addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                if (message.equals("hello")) {
                    websocket.sendText(text);
                }

                if (message.startsWith("NEW_PROGRESS$")) {
                    Profile profile = Echo.INSTANCE.getProfileManager().getProfile(target.getUniqueId());
                    if (!profile.isFrozen()) {
                        websocket.sendClose();
                    }

                    String progress = message.split("\\$")[1];

                    if (progress.equals("STARTED")) {
                        profile.setStarted(true);

                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                Echo.INSTANCE.getConfig().getString("FREEZE_COMMAND.STAFF_STARTED_MESSAGE")
                                        .replace("{player}", target.getName())));

                    } else if (progress.equals("FINISHED")) {
                        API api = new API();

                        String link;

                        if (staff instanceof Player) {
                            Player staffPlayer = (Player) staff;
                            link = "https://scan.echo.ac/" + api.getLastScan(
                                    Echo.INSTANCE.getStorage().getKey(staffPlayer.getUniqueId().toString()), staff);
                        } else {
                            link = "https://scan.echo.ac/"
                                    + api.getLastScan(Echo.INSTANCE.getStorage().getConsole(), staff);
                        }

                        staff.sendMessage(ChatColor.translateAlternateColorCodes('&', Echo.INSTANCE.getConfig()
                                .getString("FREEZE_COMMAND.STAFF_RESULT_MESSAGE").replace("{link}", link)));
                    } else {
                        try {
                            int progress_value = Integer.parseInt(progress) / 5;
                            int progress_value_remaining = 20 - progress_value;

                            String progress_bar = ChatColor.translateAlternateColorCodes('&',
                                    Echo.INSTANCE.getConfig()
                                            .getString("FREEZE_COMMAND.STAFF_ACTION_BAR_PROGRESS_BAR_COLOR_1"))
                                    + "["
                                    + ChatColor.translateAlternateColorCodes('&',
                                            Echo.INSTANCE.getConfig()
                                                    .getString("FREEZE_COMMAND.STAFF_ACTION_BAR_PROGRESS_BAR_COLOR_2"))
                                    + new String(new char[progress_value]).replace("\0", "|")
                                    + ChatColor.translateAlternateColorCodes('&',
                                            Echo.INSTANCE.getConfig()
                                                    .getString("FREEZE_COMMAND.STAFF_ACTION_BAR_PROGRESS_BAR_COLOR_3"))
                                    + new String(new char[progress_value_remaining]).replace("\0", "|")
                                    + ChatColor.translateAlternateColorCodes('&',
                                            Echo.INSTANCE.getConfig()
                                                    .getString("FREEZE_COMMAND.STAFF_ACTION_BAR_PROGRESS_BAR_COLOR_1"))
                                    + "]";

                            PacketPlayOutChat packet = new PacketPlayOutChat(
                                    new ChatComponentText((ChatColor.translateAlternateColorCodes('&',
                                            Echo.INSTANCE.getConfig().getString("FREEZE_COMMAND.STAFF_ACTION_BAR")
                                                    .replace("{progress}",
                                                            (Echo.INSTANCE.getConfig().getBoolean(
                                                                    "FREEZE_COMMAND.STAFF_ACTION_BAR_PROGRESS_BAR")
                                                                            ? progress_bar
                                                                            : progress))))),
                                    (byte) 2);
                            ((CraftPlayer) staff).getHandle().playerConnection.sendPacket(packet);
                        } catch (NumberFormatException e) {

                        }
                    }
                }
            }
        }).connect();
    }
}
