package dev.sefiraat.netheopoiesis.listeners;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.sefiraat.netheopoiesis.Purification;
import dev.sefiraat.netheopoiesis.utils.TimePeriod;
import dev.sefiraat.netheopoiesis.utils.WorldUtils;

/**
 * The purpose of this listener is to allow players to sleep and set their respawn point whilst
 * in the Nether assuming the chunk is purified enough
 */
public class PlayerSleepListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSleep(@Nonnull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final Block block = event.getClickedBlock();
        if (block != null && validSleepEvent(event.getAction(), world, block)) {
            event.setCancelled(true);
            if (TimePeriod.isNight(world)) {
                player.sleep(block.getLocation(), true);
                player.setBedSpawnLocation(block.getLocation());
                player.sendMessage(ChatColor.WHITE + "已设置重生点");
            } else {
                player.sendMessage(ChatColor.WHITE + "你只能在夜间或者雷雨天睡觉");
            }

        }
    }

    @ParametersAreNonnullByDefault
    private boolean validSleepEvent(Action action, World world, Block block) {
        return action == Action.RIGHT_CLICK_BLOCK
            && WorldUtils.inNether(world)
            && Tag.BEDS.isTagged(block.getType())
            && Purification.getValue(block.getChunk()) >= 250;
    }
}
