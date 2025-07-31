package dev.sefiraat.netheopoiesis.api.interfaces;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

/**
 * A WorldCrushable item is one that will be destroyed when crushed by a falling block
 * but drop a 'crushed' version of itself.
 */
public interface WorldCrushable {

    /**
     * This is the {@link ItemStack} that should drop when this item is crushed
     *
     * @return The {@link ItemStack} to drop.
     */
    @Nullable
    ItemStack crushingDrop();
}
