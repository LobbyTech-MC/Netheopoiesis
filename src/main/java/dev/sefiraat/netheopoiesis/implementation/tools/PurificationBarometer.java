package dev.sefiraat.netheopoiesis.implementation.tools;

import javax.annotation.Nonnull;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.sefiraat.netheopoiesis.Purification;
import dev.sefiraat.netheopoiesis.api.interfaces.PurifyingObject;
import dev.sefiraat.netheopoiesis.utils.ItemStackUtils;
import dev.sefiraat.netheopoiesis.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The PurificationBarometer will display the player's currently located chunks purification level
 *
 * @see PurifyingObject
 * Coolsdown after use.
 */
public class PurificationBarometer extends SimpleSlimefunItem<ItemUseHandler> {

    public PurificationBarometer(ItemGroup itemGroup,
                                 SlimefunItemStack item,
                                 RecipeType recipeType,
                                 ItemStack[] recipe
    ) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return event -> {
            final Player player = event.getPlayer();
            final Chunk chunk = player.getLocation().getChunk();
            final ItemStack barometer = event.getItem();

            if (ItemStackUtils.isOnCooldown(barometer)) {
                player.sendMessage(Theme.WARNING + "该物品仍在冷却中.");
                return;
            }

            final String message = Theme.CLICK_INFO.asTitle(
                "当前区块净化值",
                Purification.getValue(chunk)
            );
            player.sendMessage(message);
            ItemStackUtils.addCooldown(barometer, 5);
        };
    }
}
