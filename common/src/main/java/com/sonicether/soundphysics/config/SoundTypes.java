package com.sonicether.soundphysics.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.SoundType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SoundTypes {

    private static final Map<SoundType, String> TRANSLATION_MAP;

    static {
        Map<SoundType, String> names = new HashMap<>();

        // We need to put it in manually, because of remapping
        names.put(SoundType.WOOD, "WOOD");
        names.put(SoundType.GRAVEL, "GRAVEL");
        names.put(SoundType.GRASS, "GRASS");
        names.put(SoundType.LILY_PAD, "LILY_PAD");
        names.put(SoundType.STONE, "STONE");
        names.put(SoundType.METAL, "METAL");
        names.put(SoundType.GLASS, "GLASS");
        names.put(SoundType.WOOL, "WOOL");
        names.put(SoundType.SAND, "SAND");
        names.put(SoundType.SNOW, "SNOW");
        names.put(SoundType.POWDER_SNOW, "POWDER_SNOW");
        names.put(SoundType.LADDER, "LADDER");
        names.put(SoundType.ANVIL, "ANVIL");
        names.put(SoundType.SLIME_BLOCK, "SLIME_BLOCK");
        names.put(SoundType.HONEY_BLOCK, "HONEY_BLOCK");
        names.put(SoundType.WET_GRASS, "WET_GRASS");
        names.put(SoundType.CORAL_BLOCK, "CORAL_BLOCK");
        names.put(SoundType.BAMBOO, "BAMBOO");
        names.put(SoundType.BAMBOO_SAPLING, "BAMBOO_SAPLING");
        names.put(SoundType.SCAFFOLDING, "SCAFFOLDING");
        names.put(SoundType.SWEET_BERRY_BUSH, "SWEET_BERRY_BUSH");
        names.put(SoundType.CROP, "CROP");
        names.put(SoundType.HARD_CROP, "HARD_CROP");
        names.put(SoundType.VINE, "VINE");
        names.put(SoundType.NETHER_WART, "NETHER_WART");
        names.put(SoundType.LANTERN, "LANTERN");
        names.put(SoundType.STEM, "STEM");
        names.put(SoundType.NYLIUM, "NYLIUM");
        names.put(SoundType.FUNGUS, "FUNGUS");
        names.put(SoundType.ROOTS, "ROOTS");
        names.put(SoundType.SHROOMLIGHT, "SHROOMLIGHT");
        names.put(SoundType.WEEPING_VINES, "WEEPING_VINES");
        names.put(SoundType.TWISTING_VINES, "TWISTING_VINES");
        names.put(SoundType.SOUL_SAND, "SOUL_SAND");
        names.put(SoundType.SOUL_SOIL, "SOUL_SOIL");
        names.put(SoundType.BASALT, "BASALT");
        names.put(SoundType.WART_BLOCK, "WART_BLOCK");
        names.put(SoundType.NETHERRACK, "NETHERRACK");
        names.put(SoundType.NETHER_BRICKS, "NETHER_BRICKS");
        names.put(SoundType.NETHER_SPROUTS, "NETHER_SPROUTS");
        names.put(SoundType.NETHER_ORE, "NETHER_ORE");
        names.put(SoundType.BONE_BLOCK, "BONE_BLOCK");
        names.put(SoundType.NETHERITE_BLOCK, "NETHERITE_BLOCK");
        names.put(SoundType.ANCIENT_DEBRIS, "ANCIENT_DEBRIS");
        names.put(SoundType.LODESTONE, "LODESTONE");
        names.put(SoundType.CHAIN, "CHAIN");
        names.put(SoundType.NETHER_GOLD_ORE, "NETHER_GOLD_ORE");
        names.put(SoundType.GILDED_BLACKSTONE, "GILDED_BLACKSTONE");
        names.put(SoundType.CANDLE, "CANDLE");
        names.put(SoundType.AMETHYST, "AMETHYST");
        names.put(SoundType.AMETHYST_CLUSTER, "AMETHYST_CLUSTER");
        names.put(SoundType.SMALL_AMETHYST_BUD, "SMALL_AMETHYST_BUD");
        names.put(SoundType.MEDIUM_AMETHYST_BUD, "MEDIUM_AMETHYST_BUD");
        names.put(SoundType.LARGE_AMETHYST_BUD, "LARGE_AMETHYST_BUD");
        names.put(SoundType.TUFF, "TUFF");
        names.put(SoundType.CALCITE, "CALCITE");
        names.put(SoundType.DRIPSTONE_BLOCK, "DRIPSTONE_BLOCK");
        names.put(SoundType.POINTED_DRIPSTONE, "POINTED_DRIPSTONE");
        names.put(SoundType.COPPER, "COPPER");
        names.put(SoundType.CAVE_VINES, "CAVE_VINES");
        names.put(SoundType.SPORE_BLOSSOM, "SPORE_BLOSSOM");
        names.put(SoundType.AZALEA, "AZALEA");
        names.put(SoundType.FLOWERING_AZALEA, "FLOWERING_AZALEA");
        names.put(SoundType.MOSS_CARPET, "MOSS_CARPET");
        names.put(SoundType.MOSS, "MOSS");
        names.put(SoundType.BIG_DRIPLEAF, "BIG_DRIPLEAF");
        names.put(SoundType.SMALL_DRIPLEAF, "SMALL_DRIPLEAF");
        names.put(SoundType.ROOTED_DIRT, "ROOTED_DIRT");
        names.put(SoundType.HANGING_ROOTS, "HANGING_ROOTS");
        names.put(SoundType.AZALEA_LEAVES, "AZALEA_LEAVES");
        names.put(SoundType.SCULK_SENSOR, "SCULK_SENSOR");
        names.put(SoundType.SCULK_CATALYST, "SCULK_CATALYST");
        names.put(SoundType.SCULK, "SCULK");
        names.put(SoundType.SCULK_VEIN, "SCULK_VEIN");
        names.put(SoundType.SCULK_SHRIEKER, "SCULK_SHRIEKER");
        names.put(SoundType.GLOW_LICHEN, "GLOW_LICHEN");
        names.put(SoundType.DEEPSLATE, "DEEPSLATE");
        names.put(SoundType.DEEPSLATE_BRICKS, "DEEPSLATE_BRICKS");
        names.put(SoundType.DEEPSLATE_TILES, "DEEPSLATE_TILES");
        names.put(SoundType.POLISHED_DEEPSLATE, "POLISHED_DEEPSLATE");
        names.put(SoundType.FROGLIGHT, "FROGLIGHT");
        names.put(SoundType.FROGSPAWN, "FROGSPAWN");
        names.put(SoundType.MANGROVE_ROOTS, "MANGROVE_ROOTS");
        names.put(SoundType.MUDDY_MANGROVE_ROOTS, "MUDDY_MANGROVE_ROOTS");
        names.put(SoundType.MUD, "MUD");
        names.put(SoundType.MUD_BRICKS, "MUD_BRICKS");
        names.put(SoundType.PACKED_MUD, "PACKED_MUD");
        names.put(SoundType.HANGING_SIGN, "HANGING_SIGN");
        names.put(SoundType.NETHER_WOOD_HANGING_SIGN, "NETHER_WOOD_HANGING_SIGN");
        names.put(SoundType.BAMBOO_WOOD_HANGING_SIGN, "BAMBOO_WOOD_HANGING_SIGN");
        names.put(SoundType.BAMBOO_WOOD, "BAMBOO_WOOD");
        names.put(SoundType.NETHER_WOOD, "NETHER_WOOD");
        names.put(SoundType.CHERRY_WOOD, "CHERRY_WOOD");
        names.put(SoundType.CHERRY_SAPLING, "CHERRY_SAPLING");
        names.put(SoundType.CHERRY_LEAVES, "CHERRY_LEAVES");
        names.put(SoundType.CHERRY_WOOD_HANGING_SIGN, "CHERRY_WOOD_HANGING_SIGN");
        names.put(SoundType.CHISELED_BOOKSHELF, "CHISELED_BOOKSHELF");
        names.put(SoundType.SUSPICIOUS_SAND, "SUSPICIOUS_SAND");
        names.put(SoundType.SUSPICIOUS_GRAVEL, "SUSPICIOUS_GRAVEL");
        names.put(SoundType.DECORATED_POT, "DECORATED_POT");
        names.put(SoundType.DECORATED_POT_CRACKED, "DECORATED_POT_CRACKED");

        TRANSLATION_MAP = Collections.unmodifiableMap(names);
    }

    public static Map<SoundType, String> getTranslationMap() {
        return TRANSLATION_MAP;
    }

    @Nullable
    public static String getName(SoundType soundType) {
        return TRANSLATION_MAP.get(soundType);
    }

    @Nullable
    public static SoundType getSoundType(String name) {
        return TRANSLATION_MAP.entrySet().stream().filter(e -> e.getValue().equals(name)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public static MutableComponent getNameComponent(SoundType soundType) {
        String name = getName(soundType);
        if (name == null) {
            return Component.literal("N/A");
        }
        String[] split = name.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(s.charAt(0));
            builder.append(s.substring(1).toLowerCase(Locale.ROOT));
            builder.append(" ");
        }

        return Component.literal(builder.toString().trim());
    }

}
