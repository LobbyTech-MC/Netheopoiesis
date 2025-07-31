package dev.sefiraat.netheopoiesis.managers;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;

import co.aikar.commands.PaperCommandManager;
import dev.sefiraat.netheopoiesis.api.mobs.MobCapType;
import dev.sefiraat.netheopoiesis.implementation.commands.NetheoCommands;

public final class DispatchManager extends PaperCommandManager {

    private static DispatchManager instance;

    public DispatchManager(Plugin plugin) {
        super(plugin);

        Preconditions.checkArgument(instance == null, "Cannot create a new instance of the DispatchManager");
        instance = this;

        registerCommand(new NetheoCommands());

        getCommandCompletions().registerCompletion(
            "MOB_CAPS",
            context -> Arrays.stream(MobCapType.values())
                             .map(Enum::name)
                             .collect(Collectors.toSet())
        );
    }
}
