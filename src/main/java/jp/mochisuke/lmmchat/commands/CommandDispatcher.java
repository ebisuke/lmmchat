package jp.mochisuke.lmmchat.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class CommandDispatcher {

    public static void registerCommands(MinecraftServer server) {
        String[] available_models = new String[]{"gpt-3.5-turbo", "gpt-4"};
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
                                                                x.getSource().sendSuccess(Component.nullToEmpty("Model set to " + modelname + "."), false);
                                                            } else {
                                                                x.getSource().sendFailure(Component.nullToEmpty("Model " + modelname + " is not available."));
                                                            }
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                                .executes(
                                                        x -> {
                                                            //get model
                                                            String modelname = LMMChatConfig.getModelName();
                                                            x.getSource().sendSuccess(Component.nullToEmpty("Model is " + modelname + "."), false);
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
                                                            x.getSource().sendSuccess(Component.nullToEmpty("MaxTokens set to " + maxtokens + "."), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get maxtokens
                                                    int maxtokens = LMMChatConfig.getMaxTokens();
                                                    x.getSource().sendSuccess(Component.nullToEmpty("MaxTokens is " + maxtokens + "."), false);
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
                                                            x.getSource().sendSuccess(Component.nullToEmpty("Embedding set to " + embedding + "."), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get embedding
                                                    boolean embedding = LMMChatConfig.isEnableEmbeddeding();
                                                    x.getSource().sendSuccess(Component.nullToEmpty("Embedding is " + (embedding?"enabled":"disabled") + "."), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );
        //embeddingthreshold
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("embedthreshold")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Double>argument("thesholdsimilarity", DoubleArgumentType.doubleArg(0,1))
                                                .executes(
                                                        x -> {
                                                            //set embeddingthreshold
                                                            double embeddingthreshold = x.getArgument("thesholdsimilarity", Double.class);
                                                            LMMChatConfig.setThresholdSimilarity(embeddingthreshold);
                                                            x.getSource().sendSuccess(Component.nullToEmpty("EmbeddingThreshold set to " + embeddingthreshold + "."), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get embeddingthreshold
                                                    double embeddingthreshold = LMMChatConfig.getThresholdSimilarity();
                                                    x.getSource().sendSuccess(Component.nullToEmpty("EmbeddingThreshold is " + embeddingthreshold + "."), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );
    }
}
