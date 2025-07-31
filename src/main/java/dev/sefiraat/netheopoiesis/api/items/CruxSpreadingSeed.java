package dev.sefiraat.netheopoiesis.api.items;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.google.common.base.Preconditions;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import dev.sefiraat.netheopoiesis.Netheopoiesis;
import dev.sefiraat.netheopoiesis.Purification;
import dev.sefiraat.netheopoiesis.api.events.CruxSpreadEvent;
import dev.sefiraat.netheopoiesis.api.interfaces.SpreadingPlant;
import dev.sefiraat.netheopoiesis.implementation.tasks.UpdateCruxTask;
import dev.sefiraat.netheopoiesis.utils.ProtectionUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

/**
 * This seed will spread crux to nearby blocks when placed and grown
 */
public class CruxSpreadingSeed extends NetherSeed implements SpreadingPlant {

    private SlimefunItemStack convertTo;
    private double spreadChance = 0.1;

    @ParametersAreNonnullByDefault
    public CruxSpreadingSeed(@Nonnull SlimefunItemStack item) {
        super(item);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTickFullyGrown(Location location, NetherSeed seed, SlimefunBlockData data) {
        spread(location, seed, data);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void spread(Location sourceLocation, NetherSeed seed, SlimefunBlockData data) {
        double randomChance = ThreadLocalRandom.current().nextDouble();

        if (randomChance <= (this.spreadChance * Netheopoiesis.CRUX_SPREAD_MULTIPLIER)) {
            // Fails chance roll
            return;
        }

        final double randomX = ThreadLocalRandom.current().nextInt(-3, 4);
        final double randomY = ThreadLocalRandom.current().nextInt(-2, 1);
        final double randomZ = ThreadLocalRandom.current().nextInt(-3, 4);
        // For loop to make sure the purification can creep up and down.

        final Block block = sourceLocation.clone().add(randomX, randomY, randomZ).getBlock();
        final SlimefunItem possibleCrux = StorageCacheUtils.getSfItem(block.getLocation());
        if (possibleCrux instanceof NetherCrux currentCrux
            && getPlacements().contains(currentCrux.getId())
            && ProtectionUtils.hasPermission(getOwner(sourceLocation), block, Interaction.BREAK_BLOCK)
        ) {
            final SlimefunItem slimefunItem = SlimefunItem.getByItem(convertTo);

            if (!(slimefunItem instanceof NetherCrux crux)) {
                // Not a crux, cannot spread - should not be reachable thanks to #setCrux()
                return;
            }

            final CruxSpreadEvent event = new CruxSpreadEvent(block, this, crux);

            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                // Event cancelled - lets not spread
                return;
            }
            Slimefun.getDatabaseManager().getBlockDataController().removeBlock(block.getLocation());
            Purification.removeValue(block);
            // Schedule a task to ensure the new block storage happens only AFTER deletion
            UpdateCruxTask task = new UpdateCruxTask(block, convertTo);
            task.runTaskTimer(Netheopoiesis.getInstance(), 1, 20);
            afterSpread(sourceLocation, seed, data, block);
        }
    }

    @Nonnull
    public CruxSpreadingSeed setSpreadChance(double spreadChance) {
        this.spreadChance = spreadChance;
        return this;
    }

    @Nonnull
    public CruxSpreadingSeed setCrux(@Nonnull SlimefunItemStack crux) {
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(crux);
        Preconditions.checkArgument(slimefunItem instanceof NetherCrux, "Set Crux MUST be a NetherCrux");
        this.convertTo = crux;
        return this;
    }

    @Nullable
    public SlimefunItemStack getCrux() {
        return this.convertTo;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected boolean validateSeed() {
        if (this.convertTo == null) {
            Netheopoiesis.logWarning(this.getId() + " has not has it's Crux set, will not be registered.");
            return false;
        }
        return true;
    }
}
