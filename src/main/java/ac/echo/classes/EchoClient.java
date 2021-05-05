package ac.echo.classes;

import ac.echo.Echo;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class EchoClient {
    private static final String SERVER = "wss://scanner.echo.ac";

    public static WebSocket connect(String pin, CommandSender staff) throws IOException, WebSocketException {
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
                            String progress = message.split("\\$")[1];

                            if (progress.equals("FINISHED")) {
                                API api = new API();

                                staff.sendMessage("Scan complete! Here is link:");
                                staff.sendMessage("https://scan.echo.ac/" + api.getLastScan(Echo.INSTANCE.getApikey(), staff));
                                websocket.sendClose();
                            } else if (progress.equals("STARTED")) {
                                staff.sendMessage("Player started scanning!");
                            } else {
                                staff.sendMessage("Progress: " + progress + "%");
                            }
                        }
                    }
                })
                .connect();
    }
}
