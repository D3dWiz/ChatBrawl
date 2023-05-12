package be.woutzah.chatbrawl.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {

    public static boolean isRunning(BukkitTask task){
        int taskId = task.getTaskId();
        return Bukkit.getScheduler().isCurrentlyRunning(taskId) ||
                Bukkit.getScheduler().isQueued(taskId);
    }

    public static void cancel(BukkitTask task){
        if (isRunning(task)) task.cancel();
    }

}
