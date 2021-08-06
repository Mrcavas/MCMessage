package me.mrcavas.mcmessage.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.Unpooled;
import me.mrcavas.mcmessage.Main;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static net.minecraft.server.command.CommandManager.*;

public class MsgCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("msg")
                .then(argument("targets", EntityArgumentType.players())
                        .then(argument("message", MessageArgumentType.message())
                                .executes(ctx ->
                                        execute(ctx.getSource().getPlayer(), EntityArgumentType.getPlayers(ctx, "targets"), MessageArgumentType.getMessage(ctx, "message"))))));
    }

    public int execute(ServerPlayerEntity player, Collection<ServerPlayerEntity> targets, Text message){
        targets.forEach(target -> {
            if (Main.withMod.contains(target.getUuid())) {
                JSONObject from = new JSONObject();

                from.put("from", player.getName());
                from.put("message", message.getString());

                if (Main.withMod.contains(player.getUuid())) {
                    JSONObject to = new JSONObject();

                    to.put("to", target.getName());
                    to.put("message", message.getString());

                    ServerPlayNetworking.send(player, new Identifier("mcmessage", "my_msg"), new PacketByteBuf(Unpooled.copiedBuffer(to.toString().getBytes(StandardCharsets.UTF_8))));
                }

                ServerPlayNetworking.send(target, new Identifier("mcmessage", "msg"), new PacketByteBuf(Unpooled.copiedBuffer(from.toString().getBytes(StandardCharsets.UTF_8))));
            } else {
                player.server.getCommandManager().execute(player.getCommandSource(), "w " + target + " " + message.getString());
            }
        });

        return 1;
    }
}
