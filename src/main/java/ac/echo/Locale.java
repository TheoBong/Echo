package ac.echo;

import java.text.MessageFormat;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum Locale {

    NO_PERMISSION("NO_PERMISSION"),
    NO_ALTS_MESSAGE("ALTS_COMMAND.NO_ALTS_MESSAGE"),
    ALTS_START_MESSAGE("ALTS_COMMAND.START_MESSAGE"),
    ALTS_FINISH_MESSAGE("ALTS_COMMAND.FINISH_MESSAGE"),
    AUTOMATIC_BAN_COMMAND("FREEZE_COMMAND.AUTOBAN_COMMAND"),
    FREEZE_MESSAGE("FREEZE_COMMAND.MESSAGE"),
    FREEZE_STARTED_MESSAGE("FREEZE_COMMAND.STAFF_STARTED_MESSAGE"),
    STAFF_RESULT_MESSAGE("FREEZE_COMMAND.STAFF_RESULT_MESSAGE");

    private String path;

    public String format(Object... objects) {
        return new MessageFormat(ChatColor.translateAlternateColorCodes('&',
                Echo.INSTANCE.getConfig().getString(path))).format(objects);
    }
}

