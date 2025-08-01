package dev.sefiraat.netheopoiesis.implementation.groups;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.sefiraat.netheopoiesis.Registry;
import dev.sefiraat.netheopoiesis.api.items.NetherSeed;
import dev.sefiraat.netheopoiesis.api.plant.breeding.BreedingPair;
import dev.sefiraat.netheopoiesis.implementation.Groups;
import dev.sefiraat.netheopoiesis.utils.StatisticUtils;
import dev.sefiraat.netheopoiesis.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * This flex group is used to display breeding information to the player.
 * Information is locked until the player has bred the appropriate plant at least once
 */
public class DiscoveriesFlexGroup extends FlexItemGroup {

    private static final int PAGE_SIZE = 36;

    private static final int GUIDE_BACK = 1;

    private static final int PAGE_PREVIOUS = 46;
    private static final int PAGE_NEXT = 52;

    private static final int[] HEADER = new int[]{
        0, 1, 2, 3, 4, 5, 6, 7, 8
    };
    private static final int[] FOOTER = new int[]{
        45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    private static final int CHILD_SLOT = 22;
    private static final int[] CHILD_INFO_SLOT = new int[]{13, 31};

    private static final int MOTHER_SLOT = 21;
    private static final int[] MOTHER_INFO_SLOT = new int[]{12, 30};

    private static final int FATHER_SLOT = 23;
    private static final int[] FATHER_INFO_SLOT = new int[]{14, 32};

    private static final int GROWTH_RATE_SLOT = 37;
    private static final int PURIFICATION_AMOUNT_SLOT = 38;
    private static final int[] HELD_SLOTS = new int[]{39, 40, 41, 42, 43};

    private static final ItemStack MOTHER_INFO = new CustomItemStack(
        Material.LIGHT_BLUE_STAINED_GLASS_PANE,
        Theme.PASSIVE + "母种子"
    );

    private static final ItemStack FATHER_INFO = new CustomItemStack(
        Material.LIGHT_BLUE_STAINED_GLASS_PANE,
        Theme.PASSIVE + "父种子"
    );

    private static final ItemStack CHILD_INFO = new CustomItemStack(
        Material.LIGHT_BLUE_STAINED_GLASS_PANE,
        Theme.PASSIVE + "子种子"
    );

    private static final ItemStack HELD_SLOT = new CustomItemStack(
        Material.BLACK_STAINED_GLASS_PANE,
        " "
    );

    private static final DecimalFormat FORMAT = new DecimalFormat("#,###.##");

    public DiscoveriesFlexGroup(NamespacedKey key, ItemStack item) {
        super(key, item);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player player, PlayerProfile playerProfile, SlimefunGuideMode guideMode) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode mode) {
        final ChestMenu chestMenu = new ChestMenu(Theme.MAIN.getColor() + "培育笔记");

        for (int slot : HEADER) {
            chestMenu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);
        }

        for (int slot : FOOTER) {
            chestMenu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);
        }

        chestMenu.setEmptySlotsClickable(false);
        setupPage(p, profile, mode, chestMenu, 1);
        chestMenu.open(p);
    }

    @ParametersAreNonnullByDefault
    private void setupPage(Player player, PlayerProfile profile, SlimefunGuideMode mode, ChestMenu menu, int page) {
        final List<BreedingPair> breedingPairs = new ArrayList<>(Registry.getInstance().getBreedingPairs());
        final int amount = breedingPairs.size();
        final int totalPages = (int) Math.ceil(amount / (double) PAGE_SIZE);
        final int start = (page - 1) * PAGE_SIZE;
        final int end = Math.min(start + PAGE_SIZE, breedingPairs.size());

        breedingPairs.sort(Comparator.comparing(pair -> pair.getChild().getId()));

        final List<BreedingPair> pairSubList = breedingPairs.subList(start, end);

        reapplyFooter(player, profile, mode, menu, page, totalPages);

        // Sound
        menu.addMenuOpeningHandler((p) -> p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F));

        // Back
        menu.replaceExistingItem(
            GUIDE_BACK,
            ChestMenuUtils.getBackButton(
                player,
                "",
                ChatColor.GRAY + Slimefun.getLocalization().getMessage(player, "guide.back.guide")
            )
        );
        menu.addMenuClickHandler(GUIDE_BACK, (player1, slot, itemStack, clickAction) -> {
            SlimefunGuide.openItemGroup(profile, Groups.MAIN, mode, 1);
            return false;
        });

        for (int i = 0; i < 36; i++) {
            final int slot = i + 9;

            if (i + 1 <= pairSubList.size()) {
                final BreedingPair pair = pairSubList.get(i);
                final NetherSeed child = pair.getChild();
                final boolean researched = StatisticUtils.isDiscovered(player, child.getId());

                if (mode == SlimefunGuideMode.CHEAT_MODE || researched) {
                    menu.replaceExistingItem(slot, new ItemStack(pair.getChild().getDisplayPlant()));
                    menu.addMenuClickHandler(slot, (player1, i1, itemStack1, clickAction) -> {
                        displayDetail(player1, profile, mode, menu, page, pair);
                        return false;
                    });
                } else {
                    menu.replaceExistingItem(slot, getUndiscovered(child));
                    menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
                }
            } else {
                menu.replaceExistingItem(slot, null);
                menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void displayDetail(Player p,
                               PlayerProfile profile,
                               SlimefunGuideMode mode,
                               ChestMenu menu,
                               int returnPage,
                               BreedingPair pair
    ) {
        // Sound
        p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);

        // Back
        menu.replaceExistingItem(
            GUIDE_BACK,
            ChestMenuUtils.getBackButton(
                p,
                "",
                ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide")
            )
        );
        menu.addMenuClickHandler(GUIDE_BACK, (player1, slot, itemStack, clickAction) -> {
            setupPage(player1, profile, mode, menu, returnPage);
            p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
            return false;
        });

        clearDisplay(menu);

        final NetherSeed child = pair.getChild();
        final NetherSeed mother = (NetherSeed) SlimefunItem.getById(pair.getMotherId());
        final NetherSeed father = (NetherSeed) SlimefunItem.getById(pair.getFatherId());

        // Child
        menu.replaceExistingItem(CHILD_SLOT, child.getDisplayPlant());
        menu.addMenuClickHandler(CHILD_SLOT, ChestMenuUtils.getEmptyClickHandler());
        for (int i : CHILD_INFO_SLOT) {
            menu.replaceExistingItem(i, CHILD_INFO);
            menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }

        // Mother
        menu.replaceExistingItem(MOTHER_SLOT, mother.getDisplayPlant());
        menu.addMenuClickHandler(MOTHER_SLOT, ChestMenuUtils.getEmptyClickHandler());
        for (int i : MOTHER_INFO_SLOT) {
            menu.replaceExistingItem(i, MOTHER_INFO);
            menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }

        // Father
        menu.replaceExistingItem(FATHER_SLOT, father.getDisplayPlant());
        menu.addMenuClickHandler(FATHER_SLOT, ChestMenuUtils.getEmptyClickHandler());
        for (int i : FATHER_INFO_SLOT) {
            menu.replaceExistingItem(i, FATHER_INFO);
            menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }

        // Growth Rate
        menu.replaceExistingItem(GROWTH_RATE_SLOT, getGrowthRate(child));
        menu.addMenuClickHandler(GROWTH_RATE_SLOT, ChestMenuUtils.getEmptyClickHandler());

        // Purification
        menu.replaceExistingItem(PURIFICATION_AMOUNT_SLOT, getPurificationValue(child));
        menu.addMenuClickHandler(PURIFICATION_AMOUNT_SLOT, ChestMenuUtils.getEmptyClickHandler());

        // Held slots (for adding possible future plant information)
        for (int i : HELD_SLOTS) {
            menu.replaceExistingItem(i, HELD_SLOT);
            menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @ParametersAreNonnullByDefault
    private void clearDisplay(ChestMenu menu) {
        for (int i = 0; i < 45; i++) {
            final int slot = i + 9;
            menu.replaceExistingItem(slot, null);
            menu.addMenuClickHandler(slot, (player1, i1, itemStack1, clickAction) -> false);
        }
    }

    @ParametersAreNonnullByDefault
    private void reapplyFooter(Player p,
                               PlayerProfile profile,
                               SlimefunGuideMode mode,
                               ChestMenu menu,
                               int page,
                               int totalPages
    ) {
        for (int slot : FOOTER) {
            menu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }

        menu.replaceExistingItem(PAGE_PREVIOUS, ChestMenuUtils.getPreviousButton(p, page, totalPages));
        menu.addMenuClickHandler(PAGE_PREVIOUS, (player1, slot, itemStack, clickAction) -> {
            final int previousPage = page - 1;
            if (previousPage >= 1) {
                setupPage(player1, profile, mode, menu, previousPage);
            }
            return false;
        });

        menu.replaceExistingItem(PAGE_NEXT, ChestMenuUtils.getNextButton(p, page, totalPages));
        menu.addMenuClickHandler(PAGE_NEXT, (player1, slot, itemStack, clickAction) -> {
            final int nextPage = page + 1;
            if (nextPage <= totalPages) {
                setupPage(player1, profile, mode, menu, nextPage);
            }
            return false;
        });
    }

    @Nonnull
    public static ItemStack getUndiscovered(@Nonnull NetherSeed seed) {
        return Theme.themedItemStack(
            Material.BARRIER,
            Theme.DISCOVEREY,
            seed.getItemName(),
            Theme.ERROR + "未发现",
            "你还没有实验出",
            "如何培育这种植物!"
        );
    }

    @Nonnull
    public static ItemStack getGrowthRate(@Nonnull NetherSeed seed) {
        return new CustomItemStack(
            Material.WHEAT_SEEDS,
            Theme.CLICK_INFO.asTitle("生长速率", FORMAT.format(seed.getGrowthRate()))
        );
    }

    @Nonnull
    public static ItemStack getPurificationValue(@Nonnull NetherSeed seed) {
        return new CustomItemStack(
            Material.NETHERRACK,
            Theme.CLICK_INFO.asTitle("净化值", seed.getPurificationValue())
        );
    }
}
