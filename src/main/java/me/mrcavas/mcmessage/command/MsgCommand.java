package me.mrcavas.mcmessage.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.*;

public class MsgCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("msg")
                .then(argument("targets", EntityArgumentType.players())
                        .then(argument("message", MessageArgumentType.message())
                                .executes(ctx ->
                                        execute(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), MessageArgumentType.getMessage(ctx, "message"))))));
    }

    public int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) throws CommandSyntaxException {
//        if ()

        return 1;
    }
}
