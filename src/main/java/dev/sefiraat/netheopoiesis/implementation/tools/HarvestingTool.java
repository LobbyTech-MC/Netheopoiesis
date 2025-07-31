package dev.sefiraat.netheopoiesis.implementation.tools;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import dev.sefiraat.netheopoiesis.api.items.HarvestableSeed;
import dev.sefiraat.netheopoiesis.api.items.NetherSeed;
import dev.sefiraat.netheopoiesis.utils.Keys;
import dev.sefiraat.netheopoiesis.utils.ProtectionUtils;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.LimitedUseItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

/**
 * The HarvestingTool is used on {@link HarvestableSeed} to
 * generate materials and revert the plant's growth to seed.
 */
public class HarvestingTool extends LimitedUseItem {

    private static final NamespacedKey key = Keys.newKey("uses");

    public HarvestingTool(ItemGroup group,
                          SlimefunItemStack item,
                          RecipeType recipeType,
                          ItemStack[] recipe,
                          int amount
    ) {
        super(group, item, recipeType, recipe);
        setMaxUseCount(amount);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return this::onUse;
    }

    private void onUse(@Nonnull PlayerRightClickEvent e) {
        e.cancel();
        final Optional<Block> optional = e.getClickedBlock();
        if (optional.isPresent()) {
            final Block block = optional.get();
            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());
            if (slimefunItem instanceof HarvestableSeed seed && canHarvest(seed, block, e.getPlayer())) {
                final ItemStack stackToDrop = seed.getHarvestingResult();
                seed.updateGrowthStage(block, 1);
                block.getWorld().dropItem(block.getLocation(), stackToDrop);
                damageItem(e.getPlayer(), e.getItem());
            }
        }
    }

    private boolean canHarvest(@Nonnull NetherSeed seed, @Nonnull Block block, @Nonnull Player player) {
        return seed.isHarvestable()
            && seed.isMature(block)
            && ProtectionUtils.hasPermission(player, block, Interaction.INTERACT_BLOCK);
    }

    @Override
    protected @Nonnull
    NamespacedKey getStorageKey() {
        return key;
    }
}
