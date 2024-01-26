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
        String[] available_models = new String[]{
                "gpt-3.5-turbo",
                "gpt-3.5-turbo-1106",
                "gpt-3.5-turbo-0613",
                "gpt-3.5-turbo-0301",
                "gpt-3.5-turbo-16k",
                "gpt-3.5-turbo-16k-0613",
                "gpt-4",
                "gpt-4-turbo-preview",
                "gpt-4-1106-preview",
                "gpt-4-0125-preview"};


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
        //enablevoicevox
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("enablevoicevox")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Boolean>argument("enablevoicevox", BoolArgumentType.bool())
                                                .executes(
                                                        x -> {
                                                            //set enablevoicevox
                                                            boolean enablevoicevox = x.getArgument("enablevoicevox", Boolean.class);
                                                            LMMChatConfig.setEnableVoicevox(enablevoicevox);
                                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("EnableVoiceVox set to " + enablevoicevox + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get enablevoicevox
                                                    boolean enablevoicevox = LMMChatConfig.isEnableVoicevox();
                                                    x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("EnableVoiceVox is " + enablevoicevox + ".")), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );
        //neutral speaker id
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("neutralspeakerid")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("neutralspeakerid", IntegerArgumentType.integer())
                                                .executes(
                                                        x -> {
                                                            //set neutralspeakerid
                                                            int neutralspeakerid = x.getArgument("neutralspeakerid", Integer.class);
                                                            LMMChatConfig.setVoiceVoxNeutralSpeakerId(neutralspeakerid);

                                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("NeutralSpeakerId set to " + neutralspeakerid + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get neutralspeakerid
                                                    int neutralspeakerid = LMMChatConfig.getVoiceVoxNeutralSpeakerId();
                                                    x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("NeutralSpeakerId is " + neutralspeakerid + ".")), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );


        //speaker id
        server.getCommands().getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lmmchat")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("speakerid")
                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("speakerid", IntegerArgumentType.integer())
                                                .executes(
                                                        x -> {
                                                            //set speakerid
                                                            int speakerid = x.getArgument("speakerid", Integer.class);
                                                            LMMChatConfig.setVoiceVoxSpeakerId(speakerid);

                                                            x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("SpeakerId set to " + speakerid + ".")), false);
                                                            return 0;
                                                        }
                                                )
                                        )).then(LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                        .executes(
                                                x -> {
                                                    //get speakerid
                                                    int speakerid = LMMChatConfig.getVoiceVoxSpeakerId();
                                                    x.getSource().sendSuccess(Helper.wrapSupplier(Component.nullToEmpty("SpeakerId is " + speakerid + ".")), false);
                                                    return 0;
                                                }
                                        )
                                ))
        );


    }
}
