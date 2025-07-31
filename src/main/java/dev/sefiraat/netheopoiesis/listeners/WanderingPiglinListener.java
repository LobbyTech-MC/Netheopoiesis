package dev.sefiraat.netheopoiesis.listeners;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import dev.sefiraat.netheopoiesis.api.mobs.MobCapType;
import dev.sefiraat.netheopoiesis.managers.MobManager;
import dev.sefiraat.netheopoiesis.utils.Keys;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;

public class WanderingPiglinListener implements Listener {


    @EventHandler
    public void onPiglinPicksUpItem(@Nonnull EntityPickupItemEvent event) {
        final UUID piglinUuid = event.getEntity().getUniqueId();
        if (MobManager.getInstance().getMobCap(MobCapType.PIGLIN_TRADER).contains(piglinUuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(@Nonnull PlayerDropItemEvent event) {
        PersistentDataAPI.setString(
            event.getItemDrop(),
            Keys.DROPPED_PLAYER,
            event.getPlayer().getUniqueId().toString()
        );
    }
}
