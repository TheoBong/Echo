package ac.echo.utils;

import ac.echo.Echo;

public class ThreadUtil {
    public static void runTask(boolean async, Echo echo, Runnable runnable) {
        if(async) {
            echo.getServer().getScheduler().runTaskAsynchronously(echo, runnable);
        } else {
            runnable.run();
        }
    }
}
