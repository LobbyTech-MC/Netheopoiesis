package dev.sefiraat.netheopoiesis.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import dev.sefiraat.netheopoiesis.api.items.NetherCrux;
import dev.sefiraat.netheopoiesis.api.items.NetherSeed;
import dev.sefiraat.netheopoiesis.utils.WorldUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * The purpose of this listener is to stop abnormal removal of our Crux blocks.
 * Currently, Endermen can pick them up and place them (as Vanilla blocks) elsewhere.
 * This is confusing to players who may not know that the block is NOT a crux.
 * This listener isn't limited to just Endermen, we don't want ANY entities to
 * effect our seeds/crux'
 */
public class BlockProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockChange(@Nonnull EntityChangeBlockEvent event) {
        if (WorldUtils.inNether(event.getEntity())) {
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(event.getBlock().getLocation());
            if (slimefunItem instanceof NetherSeed || slimefunItem instanceof NetherCrux) {
                event.setCancelled(true);
            }
        }
    }
}
