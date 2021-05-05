package ac.echo.profile;

import ac.echo.Echo;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Manager {
    private final Echo instance;

    public Manager(final Echo instance) {
        this.instance = instance;
    }

    public Profile createProfile(final String name, final UUID uuid) {
        final Profile playerProfile = new Profile(name, uuid);
        this.uuidProfiles.put(uuid, playerProfile);
        return playerProfile;
    }

    private final HashMap<UUID, Profile> uuidProfiles = new HashMap<>();

    public Profile getProfile(final Player player) {
        return this.getProfile(player.getUniqueId());
    }

    public Profile getProfile(final UUID uuid) {
        return this.uuidProfiles.get(uuid);
    }
}
