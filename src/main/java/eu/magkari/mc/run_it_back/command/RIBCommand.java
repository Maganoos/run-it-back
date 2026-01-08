package eu.magkari.mc.run_it_back.command;

import com.mojang.brigadier.CommandDispatcher;
import eu.magkari.mc.run_it_back.RunItBack;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionLevel;

public class RIBCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        var base = Commands.literal(RunItBack.MOD_ID)
                .requires(Permissions.require(
                        RunItBack.CONFIG.permissions.permission.value(),
                        PermissionLevel.GAMEMASTERS
                ));

        dispatcher.register(base
                .then(
                        Commands.literal("reload")
                                .executes(context -> {
                                    if (RunItBack.fillLists(true)) {
                                        context.getSource().sendSuccess(
                                                () -> Component.translatable("commands.run-it-back.reload.success"),
                                                true
                                        );
                                        return 1;
                                    } else {
                                        context.getSource().sendFailure(
                                                Component.translatable("commands.run-it-back.reload.failure")
                                        );
                                        return 0;
                                    }
                                })
                )
        );

        dispatcher.register(base
                .then(
                        Commands.literal("toggle")
                                .executes(context -> {
                                    RunItBack.CONFIG.enabled.setValue(!RunItBack.CONFIG.enabled.value());
                                    RunItBack.CONFIG.save();
                                    context.getSource().sendSuccess(() -> Component.translatable(RunItBack.CONFIG.enabled.value() ? "options.on.composed" : "options.off.composed", "RunItBack"), true);
                                    return 1;
                                })
                )

        );
    }

}
