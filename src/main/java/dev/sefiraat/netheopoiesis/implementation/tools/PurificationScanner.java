package dev.sefiraat.netheopoiesis.implementation.tools;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import dev.sefiraat.netheopoiesis.api.interfaces.PurifyingObject;
import dev.sefiraat.netheopoiesis.utils.ItemStackUtils;
import dev.sefiraat.netheopoiesis.utils.ProtectionUtils;
import dev.sefiraat.netheopoiesis.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

/**
 * The Scanner is used on {@link PurifyingObject}s to display their purifying effect to players.
 */
public class PurificationScanner extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public PurificationScanner(ItemGroup group, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(group, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler((ItemUseHandler) this::onUse);
    }

    private void onUse(@Nonnull PlayerRightClickEvent event) {
        final Optional<Block> optional = event.getClickedBlock();
        if (optional.isPresent()) {
            final Block block = optional.get();
            final Player player = event.getPlayer();

            final ItemStack analyser = event.getItem();

            if (ItemStackUtils.isOnCooldown(analyser)) {
                player.sendMessage(Theme.WARNING + "该物品仍在冷却中.");
                return;
            }

            final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());

            if (slimefunItem instanceof PurifyingObject object
                && ProtectionUtils.hasPermission(player, block, Interaction.INTERACT_BLOCK)
            ) {
                final String messageValue = Theme.CLICK_INFO.asTitle(
                    "净化值",
                    object.getPurificationValue()
                );
                player.sendMessage(messageValue);
            }
            // Put item on cooldown to minimise potential BlockStorage spamming
            ItemStackUtils.addCooldown(analyser, 5);
        }
    }
}
