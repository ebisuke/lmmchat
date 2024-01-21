package jp.mochisuke.lmmchat.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.function.Supplier;

public class CommandDispatcher {

    public static void registerCommands(MinecraftServer server) {
        String[] available_models = new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-1106","gpt-3.5-turbo-16k","gpt-4","gpt-4-1106-preview"};
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("model")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("modelname", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    return net.minecraft.commands.SharedSuggestionProvider.suggest(available_models, builder);
                                                })

                                                .executes(
                                                        x -> {
                                                            //set model
                                                            String modelname = x.getArgument("modelname", String.class);

                                                            //available model?
                                                            if (java.util.Arrays.asList(available_models).contains(modelname)) {
                                                                LMMChatConfig.setModel(modelname);
                                                                var c=Component.nullToEmpty("Model set to " + modelname + ".");
                                                                // to supplier
                                                                Supplier<Component> supplier = () -> c;
                                                                x.getSource().sendSuccess(supplier, false);
                                                            } else {
                                                                var c=Component.nullToEmpty("Model " + modelname + " is not available.");
                                                                // to supplier
                                                                Supplier<Component> supplier = () -> c;
                                                                x.getSource().sendFailure(c);
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                                .executes(
                                                        x -> {
                                                            //get model
                                                            String modelname = LMMChatConfig.getModelName();

                                                            x.getSource().sendSuccess(
                                                                    Helper.wrapSupplier(Component.nullToEmpty("Model is " + modelname + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )
                                )

        );
        //set maxtokens
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("maxtokens")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("maxtokens", IntegerArgumentType.integer())
                                                .executes(
                                                        x -> {
                                                            //set maxtokens
                                                            int maxtokens = x.getArgument("maxtokens", Integer.class);
                                                            LMMChatConfig.setMaxTokens(maxtokens);
                                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("MaxTokens set to " + maxtokens + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get maxtokens
                                                    int maxtokens = LMMChatConfig.getMaxTokens();
                                                    x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("MaxTokens is " + maxtokens + ".")), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );
        //embedding
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("embedding")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Boolean>argument("embedding", BoolArgumentType.bool())
                                                .executes(
                                                        x -> {
                                                            //set embedding
                                                            boolean embedding = x.getArgument("embedding", Boolean.class);
                                                            LMMChatConfig.setEnableEmbedding(embedding);
                                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("Embedding set to " + embedding + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get embedding
                                                    boolean embedding = LMMChatConfig.isEnableEmbeddeding();
                                                    x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("Embedding is " + (embedding?"enabled":"disabled") + ".")), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );
        //embeddingthreshold
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("embeddingthreshold")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Double>argument("thesholdsimilarity", DoubleArgumentType.doubleArg(0,1))
                                                .executes(
                                                        x -> {
                                                            //set embeddingthreshold
                                                            double embeddingthreshold = x.getArgument("thesholdsimilarity", Double.class);
                                                            LMMChatConfig.setThresholdSimilarity(embeddingthreshold);
                                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("EmbeddingThreshold set to " + embeddingthreshold + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get embeddingthreshold
                                                    double embeddingthreshold = LMMChatConfig.getThresholdSimilarity();
                                                    x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("EmbeddingThreshold is " + embeddingthreshold + ".")), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );
        // reload
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload")
                                .executes(
                                        x -> {
                                            //reload
                                            LMMChatConfig.reload();
                                            LMMChat.chatManager.clearAll();
                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("Config reloaded.")), false);
                                            return 0;
                                        }
                                )
                        )
        );
        // print preface
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("preface")
                                .executes(
                                        x -> {
                                            //print preface
                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty(LMMChatConfig.getPreface())), false);
                                            return 0;
                                        }
                                )
                        )
        );
        // remove history
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("removehistory")
                                .executes(
                                        x -> {
                                            //remove history
                                            LMMChat.chatManager.clearAll();
                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("History removed.")), false);
                                            return 0;
                                        }
                                )
                        )
        );

        //engine
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("engine")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("engine", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    return net.minecraft.commands.SharedSuggestionProvider.suggest(new String[]{"openai", "gemini"}, builder);
                                                })

                                                .executes(
                                                        x -> {
                                                            //set engine
                                                            String engine = x.getArgument("engine", String.class);

                                                            //available engine?
                                                            if (java.util.Arrays.asList(new String[]{"openai", "gemini"}).contains(engine)) {
                                                                LMMChatConfig.setEngine(LMMChatConfig.Engine.valueOf(engine.toUpperCase()));
                                                                var c=Component.nullToEmpty("Engine set to " + engine + ".");
                                                                // to supplier
                                                                Supplier<Component> supplier = () -> c;
                                                                x.getSource().sendSuccess(supplier, false);
                                                            } else {
                                                                var c=Component.nullToEmpty("Engine " + engine + " is not available.");
                                                                // to supplier
                                                                Supplier<Component> supplier = () -> c;
                                                                x.getSource().sendFailure(c);
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get engine
                                                    String engine = LMMChatConfig.getEngine().toString();

                                                    x.getSource().sendSuccess(
                                                            Helper.wrapSupplier(Component.nullToEmpty("Engine is " + engine + ".")), false);
                                                    return 0;
                                                }
                                        )
                                )

                        )
        );
    }
}
