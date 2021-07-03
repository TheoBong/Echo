package ac.echo.listeners;

import ac.echo.Echo;
import ac.echo.profile.Profile;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent implements Listener {
    private Echo echo;

    public MoveEvent(Echo echo) {
        this.echo = echo;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Profile profile = echo.getProfileManager().getProfile(event.getPlayer().getUniqueId());

        Location to = event.getTo();
        Location from = event.getFrom();

        if (profile.isFrozen()
                && (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ())) {
            from.setX(from.getBlockX() + 0.5);
            from.setZ(from.getBlockZ() + 0.5);
            event.setTo(from);
        }
    }
}
