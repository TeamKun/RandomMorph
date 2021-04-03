package net.kunmc.lab.randommorph;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import draylar.identity.Identity;
import draylar.identity.api.event.PlayerJoinCallback;
import draylar.identity.registry.Components;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.kunmc.lab.randommorph.config.RandomMorphConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMorph implements ModInitializer {

    public MinecraftServer minecraftServerInstance;

    public static RandomMorphConfig CONFIG = OmegaConfig.register(RandomMorphConfig.class);

    private boolean active = false;
    private int count = 0;
    private int tier = 1;

    private ServerBossBar bossBar = new ServerBossBar(new LiteralText("変身するまで"), BossBar.Color.WHITE, BossBar.Style.PROGRESS);

    @Override
    public void onInitialize() {

        Identity.CONFIG.enableClientSwapMenu = false;

        registerCommand();

        PlayerJoinCallback.EVENT.register(player -> {
            if (!active) {
                return;
            }
            bossBar.addPlayer(player);
            randomMorph(player);
        });

        ServerStartCallback.EVENT.register(server -> minecraftServerInstance = server);

        ServerTickCallback.EVENT.register(server -> {

            if (!active) {
                return;
            }

            count ++;

            bossBar.setName(new LiteralText(String.format("変身するまで %s秒", (CONFIG.interval - count) / 20)));
            bossBar.setPercent((float) count / (float) CONFIG.interval);

            if (count < CONFIG.interval) {
                return;
            }

            server.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                randomMorph(serverPlayerEntity);
            });
            count = 0;

        });
    }

    public void randomMorph(ServerPlayerEntity player) {
        List<EntityType> candidacies = new ArrayList<>();
        CONFIG.tierMap.entrySet().forEach(stringIntegerEntry -> {
            if (tier < stringIntegerEntry.getValue()) {
                return;
            }
            String id = stringIntegerEntry.getKey();
            if (!EntityType.get(id).isPresent()) {
                return;
            }
            EntityType type = EntityType.get(id).get();
            candidacies.add(type);
        });
        int size = candidacies.size();
        EntityType entityType = candidacies.get(new Random().nextInt(size));
        Components.CURRENT_IDENTITY.get(player).setIdentity((LivingEntity) entityType.create(player.world));
        player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new TranslatableText(entityType.getTranslationKey()).append(new LiteralText(" に変身した"))));

    }

    public void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, b) -> {
            LiteralCommandNode<ServerCommandSource> rootNode = CommandManager
                    .literal("randomMorph")
                    .requires(source -> source.hasPermissionLevel(2))

                    .then(CommandManager.literal("reloadConfig")
                            .executes(context -> {
                                CONFIG = OmegaConfig.register(RandomMorphConfig.class);
                                context.getSource().sendFeedback(new LiteralText("設定ファイルを再読み込みしました"), true);
                                return 1;
                            }))

                    .then(CommandManager.literal("start")
                            .executes(context -> {
                                active = true;
                                minecraftServerInstance.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> bossBar.addPlayer(serverPlayerEntity));
                                context.getSource().sendFeedback(new LiteralText("ランダム変身を開始しました"), true);
                                return 1;
                            }))

                    .then(CommandManager.literal("stop")
                            .executes(context -> {
                                active = false;
                                count = 0;
                                bossBar.clearPlayers();
                                context.getSource().sendFeedback(new LiteralText("ランダム変身を停止しました"), true);
                                return 1;
                            }))

                    .then(CommandManager.literal("setTier")
                            .then(CommandManager.argument("tier", IntegerArgumentType.integer(1))
                                    .executes(context -> {
                                        int tier = IntegerArgumentType.getInteger(context, "tier");
                                        this.tier = tier;
                                        context.getSource().sendFeedback(new LiteralText("Tier を " + tier + " に設定しました"), true);
                                        return 1;
                                    })))

                    .build();

            dispatcher.getRoot().addChild(rootNode);
        });
    }
}
