package dev.sefiraat.netheopoiesis.api.items;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;

import dev.sefiraat.netheopoiesis.Netheopoiesis;
import dev.sefiraat.netheopoiesis.utils.WorldUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

/**
 * This plant will spawn the provided entity when fully matured
 */
public class EntitySpawningSeed extends NetherSeed {

    @Nullable
    private EntityType entityType;
    @Nullable
    private Consumer<LivingEntity> callback;

    public EntitySpawningSeed(@Nonnull SlimefunItemStack item) {
        super(item);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTickFullyGrown(Location location, NetherSeed seed, SlimefunBlockData data) {
        double randomChance = ThreadLocalRandom.current().nextDouble();
        if (randomChance <= 0.05) {
            final Block block = WorldUtils.randomLocation(location, 4).getBlock();

            // The first block we spawn on needs to be AIR
            if (block.getType() != Material.AIR) {
                return;
            }

            final Block blockBelow = block.getRelative(BlockFace.DOWN);

            // And we need a solid floor and finally not too many nearby mobs
            if (this.entityType == null
                || !blockBelow.getType().isSolid()
                || block.getWorld().getNearbyEntities(block.getLocation(), 10, 10, 10).size() > 6
            ) {
                return;
            }

            // Clear to spawn
            final Entity entity = blockBelow.getWorld().spawnEntity(block.getLocation(), this.entityType);

            // Accept call back if preset
            if (entity instanceof LivingEntity livingEntity && this.callback != null) {
                livingEntity.setRemoveWhenFarAway(true);
                livingEntity.setNoDamageTicks(20);
                this.callback.accept(livingEntity);
            }
        }
    }

    @Nonnull
    public EntitySpawningSeed setEntityType(@Nonnull EntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    @Nullable
    public EntityType getEntityType() {
        return entityType;
    }

    @Nonnull
    public EntitySpawningSeed setCallback(@Nonnull Consumer<LivingEntity> callback) {
        this.callback = callback;
        return this;
    }

    @Nullable
    public Consumer<LivingEntity> getCallback() {
        return callback;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected boolean validateSeed() {
        if (this.entityType == null) {
            Netheopoiesis.logWarning(this.getId() + " has no EntityType, it will not be registered.");
            return false;
        } else if (!this.entityType.isAlive() || !this.entityType.isSpawnable()) {
            Netheopoiesis.logWarning(this.getId() + " EntityType must be both Living and Spawnable.");
            return false;
        }
        return true;
    }
}
