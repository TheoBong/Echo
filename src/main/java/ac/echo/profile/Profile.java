package ac.echo.profile;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Profile {
    @Getter @Setter private boolean frozen = false;
    @Getter @Setter private boolean started = false;
    private final String name;
    private final UUID uuid;

    public Profile(final String name, final UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}
