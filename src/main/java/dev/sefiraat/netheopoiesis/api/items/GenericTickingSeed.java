package dev.sefiraat.netheopoiesis.api.items;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;

import com.google.common.base.Preconditions;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;

import dev.sefiraat.netheopoiesis.Netheopoiesis;
import dev.sefiraat.netheopoiesis.implementation.GenericTickingMethods;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

/**
 * This plant will accept the given consumer each tick when fully matured
 *
 * @see {@link GenericTickingMethods}
 */
public class GenericTickingSeed extends NetherSeed {

    @Nullable
    private Consumer<GenericTickingMethods.TickParameters> consumer;

    public GenericTickingSeed(@Nonnull SlimefunItemStack item) {
        super(item);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTickFullyGrown(Location location, NetherSeed seed, SlimefunBlockData data) {
        final GenericTickingMethods.TickParameters tickParameters = new GenericTickingMethods.TickParameters(
            location,
            seed,
            data
        );
        Preconditions.checkNotNull(this.consumer);
        this.consumer.accept(tickParameters);
    }

    @Nonnull
    public GenericTickingSeed setConsumer(@Nonnull Consumer<GenericTickingMethods.TickParameters> consumer) {
        this.consumer = consumer;
        return this;
    }

    @Nullable
    public Consumer<GenericTickingMethods.TickParameters> getConsumer() {
        return consumer;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected boolean validateSeed() {
        if (this.consumer == null) {
            Netheopoiesis.logWarning(this.getId() + " has no Consumer, it will not be registered.");
            return false;
        }
        return true;
    }
}
