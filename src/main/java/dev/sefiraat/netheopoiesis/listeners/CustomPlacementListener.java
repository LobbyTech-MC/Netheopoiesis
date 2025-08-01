package dev.sefiraat.netheopoiesis.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import dev.sefiraat.netheopoiesis.api.interfaces.CustomPlacementBlock;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * The purpose of this listener is to allow us to cancel the block placement if not on the
 * correct crux/material. If done within the onBlockPlace handler, the BlockStorage is retained
 * leading to dupes.
 * Also allows other objects implementing {@link dev.sefiraat.netheopoiesis.api.interfaces.CustomPlacementBlock}
 * to fire their own methods
 * TODO PR to slimefun to either do blockstorage after checking the event is cancelled or to remove
 */
public class CustomPlacementListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlaced(@Nonnull BlockPlaceEvent event) {
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(event.getItemInHand());
        if (slimefunItem instanceof CustomPlacementBlock block) {
            block.whenPlaced(event);
        }
    }
}
