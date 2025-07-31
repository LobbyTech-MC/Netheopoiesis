package dev.sefiraat.netheopoiesis.api.interfaces;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;

import dev.sefiraat.netheopoiesis.Purification;

/**
 * This interface is used to describe an object that adds a purification value to the Nether
 */
public interface PurifyingObject {
    /**
     * The amount that this object in the world will
     *
     * @return The value that this item will increment the purification amount
     */
    int getPurificationValue();

    /**
     * Adds the purification value to the {@link Purification} instance.
     */
    default void registerPurificationValue(@Nonnull Block block) {
        Purification.addValue(block, getPurificationValue());
    }

    /**
     * Removes the purification value to the {@link Purification} instance.
     */
    default void removePurificationRegistry(@Nonnull Block block) {
        Purification.removeValue(block);
    }
}
