package dev.alangomes.springspigot.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Date;
import java.util.concurrent.*;

public class SpigotScheduler extends ThreadPoolTaskScheduler {

    private final Plugin plugin;

    private final BukkitScheduler scheduler;

    public SpigotScheduler(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    private Runnable wrapSync(Runnable task) {
        return () -> scheduler.scheduleSyncDelayedTask(plugin, task);
    }

    private <T> Callable<T> wrapSync(Callable<T> task) {
        return () -> {
            CompletableFuture<T> future = new CompletableFuture<>();
            scheduler.scheduleSyncDelayedTask(plugin, () -> {
                try {
                    future.complete(task.call());
                } catch (Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            });
            return future.get(1, TimeUnit.MINUTES);
        };
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return super.schedule(wrapSync(task), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return super.schedule(wrapSync(task), startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return super.scheduleAtFixedRate(wrapSync(task), startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return super.scheduleAtFixedRate(wrapSync(task), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return super.scheduleWithFixedDelay(wrapSync(task), startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return super.scheduleWithFixedDelay(wrapSync(task), delay);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(wrapSync(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrapSync(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(wrapSync(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(wrapSync(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(wrapSync(task));
    }

    @Override
    protected void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(task);
    }

}
