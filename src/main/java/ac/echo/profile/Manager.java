package ac.echo.profile;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Manager {
    @Getter private HashMap<UUID, Profile> uuidProfiles = new HashMap<>();

    public Profile createProfile(String name, UUID uuid) {
        Profile playerProfile = new Profile(name, uuid);
        this.uuidProfiles.put(uuid, playerProfile);
        return playerProfile;
    }

    public Profile getProfile(Player player) {
        return this.getProfile(player.getUniqueId());
    }

    public Profile getProfile(UUID uuid) {
        return this.uuidProfiles.get(uuid);
    }
}
