package dev.sefiraat.netheopoiesis.api.interfaces;

import javax.annotation.Nonnull;

import org.bukkit.event.block.BlockPlaceEvent;

public interface CustomPlacementBlock {

    void whenPlaced(@Nonnull BlockPlaceEvent event);

}
