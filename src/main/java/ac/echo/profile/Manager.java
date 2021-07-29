package ac.echo.profile;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class Manager {
    @Getter private final HashMap<UUID, Profile> uuidProfiles = new HashMap<>();

    public void createProfile(String name, UUID uuid) {
        Profile playerProfile = new Profile(name, uuid);
        this.uuidProfiles.put(uuid, playerProfile);
    }

    public Profile getProfile(UUID uuid) {
        return this.uuidProfiles.get(uuid);
    }
}
