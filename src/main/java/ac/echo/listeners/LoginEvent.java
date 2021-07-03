package ac.echo.listeners;

import ac.echo.Echo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class LoginEvent implements Listener {
    private Echo echo;

    public LoginEvent(Echo echo) {
        this.echo = echo;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        echo.getProfileManager().createProfile(event.getName(), event.getUniqueId());
    }
}
