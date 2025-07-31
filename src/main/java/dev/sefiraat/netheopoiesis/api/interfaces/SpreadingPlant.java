package dev.sefiraat.netheopoiesis.api.interfaces;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;

import dev.sefiraat.netheopoiesis.api.items.CruxSpreadingSeed;
import dev.sefiraat.netheopoiesis.api.items.NetherSeed;

/**
 * This interface represents a plant that spreads out into BlockStorage
 *
 * @see CruxSpreadingSeed
 */
public interface SpreadingPlant {

    @ParametersAreNonnullByDefault
    void spread(Location sourceLocation, NetherSeed seed, SlimefunBlockData data);

    @ParametersAreNonnullByDefault
    default void afterSpread(Location sourceLocation, NetherSeed seed, SlimefunBlockData data, Block spreadTo) {

    }
}
