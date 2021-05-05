package ac.echo.listeners;

import ac.echo.Echo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class LoginEvent implements Listener {
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        Echo.INSTANCE.getProfileManager().createProfile(event.getName(), event.getUniqueId());
    }
}
