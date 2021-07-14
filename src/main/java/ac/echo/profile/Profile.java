package ac.echo.profile;

import lombok.Data;

import java.util.UUID;

public @Data class Profile {
    private boolean frozen = false;
    private boolean started = false;
    private boolean scanning = false;
    private String name;
    private UUID uuid;

    public Profile(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}
