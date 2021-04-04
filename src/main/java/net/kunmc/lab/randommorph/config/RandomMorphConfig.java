package net.kunmc.lab.randommorph.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;

import java.util.HashMap;
import java.util.Map;

public class RandomMorphConfig implements Config {

    @Comment(value = "エンドラのブロック破壊")
    public boolean enderDragonBlockBreak = true;

    @Comment(value = "ラヴェジャーのブロック破壊")
    public boolean ravagerBlockBreak = true;

    @Comment(value = "エンドラの体当たりダメージ")
    public double enderDragonAttackDamage = 10;

    @Comment(value = "ラヴェジャーの体当たりダメージ")
    public double ravagerAttackDamage = 5;

    @Comment(value = "エンドラの体当たりノックバック倍率")
    public double enderDragonAttackKnockBack = 1.0;

    @Comment(value = "ラヴェジャーの体当たりノックバック倍率")
    public double ravagerAttackKnockBack = 0.3;

    @Comment(value = "Tier Map")
    public Map<String, Integer> tierMap = new HashMap<String, Integer>() {
        {
            put("bat", 1);
            put("armor_stand", 1);
            put("bee", 1);
            put("chiken", 1);
            put("slime", 1);
            put("cod", 1);
            put("cow", 1);
            put("dolphin", 1);
            put("donkey", 1);
            put("parrot", 1);
            put("salmon", 1);
            put("pig", 1);
            put("sheep", 1);
            put("pufferfish", 1);
            put("silverfish", 1);
            put("tropical_fish", 1);

            put("squid", 2);
            put("fox", 2);
            put("rabbit", 2);
            put("creeper", 2);
            put("zombie", 2);
            put("drowned", 2);
            put("endermite", 2);
            put("magma_cube", 2);
            put("mooshroom", 2);
            put("snow_golem", 2);
            put("turtle", 2);
            put("spider", 2);
            put("husk", 2);
            put("llama", 2);
            put("villager", 2);
            put("trader_llama", 2);
            put("ocelot", 2);
            put("mule", 2);
            put("panda", 2);
            put("skeleton", 2);
            put("zombie_villager", 2);
            put("wolf", 2);

            put("zoglin", 3);
            put("skeleton_horse", 3);
            put("zombie_horse", 3);
            put("strider", 3);
            put("zombified_piglin", 3);
            put("wandering_trader", 3);
            put("witch", 3);
            put("vex", 3);
            put("stray", 3);
            put("phantom", 3);
            put("vindicator", 3);
            put("horse", 3);
            put("piglin", 3);
            put("shulker", 3);
            put("piglin_brute", 3);
            put("cave_spider", 3);
            put("illusioner", 3);
            put("pillager", 3);
            put("ghast", 3);
            put("blaze", 3);
            put("enderman", 3);
            put("evoker", 3);
            put("guardian", 3);

            put("wither_skeleton", 4);
            put("elder_guardian", 4);
            put("ravager", 4);
            put("hoglin", 4);
            put("giant", 4);
            put("iron_golem", 4);
            put("polar_bear", 4);

            put("ender_dragon", 5);
            put("wither", 5);
        }
    };

    @Comment(value = "Interval (tick)")
    public int interval = 100;

    @Override
    public String getName() {
        return "randommorph";
    }

    @Override
    public String getExtension() {
        return "json5";
    }
}
