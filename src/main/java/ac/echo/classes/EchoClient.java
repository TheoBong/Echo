package ac.echo.classes;

import ac.echo.Echo;
import ac.echo.profile.Profile;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class EchoClient {
    private static final String SERVER = "wss://scanner.echo.ac";

    public static WebSocket connect(String pin, CommandSender staff, Player target) throws IOException, WebSocketException {
        String text = pin + "|no|progress";

        return new WebSocketFactory()
                .createSocket(SERVER)
                .addProtocol("progress")
                .addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) {
                        if (message.equals("hello")) {
                            websocket.sendText(text);
                        }

                        if (message.startsWith("NEW_PROGRESS$")) {
                            System.out.println(message);

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



                            } else if (progress.contains("FINISHED")) {
                                API api = new API();

                                String link = "https://scan.echo.ac/" + api.getLastScan(Echo.INSTANCE.getApikey(), staff);

                                staff.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        Echo.INSTANCE.getConfig().getString("FREEZE_COMMAND.STAFF_RESULT_MESSAGE")
                                                .replace("{link}", link)));
                            } else {
                                staff.sendMessage("Progress: " + progress + "%");
                            }
                        }
                    }
                })
                .connect();
    }
}
