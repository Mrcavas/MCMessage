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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
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

    public int execute(ServerPlayerEntity player, Collection<ServerPlayerEntity> targets, Text message) {
        targets.forEach(target -> {
            if (Main.withMod.contains(target.getUuid())) {
                JSONObject from = new JSONObject();

                from.put("from", player.getName().getString());
                from.put("message", message.getString());

                if (Main.withMod.contains(player.getUuid())) {
                    JSONObject to = new JSONObject();

                    to.put("to", target.getName().getString());
                    to.put("message", message.getString());

                    ServerPlayNetworking.send(player, new Identifier("mcmessage", "outgoing_msg"), new PacketByteBuf(Unpooled.copiedBuffer(to.toString().getBytes(StandardCharsets.UTF_8))));
                    player.sendSystemMessage((new TranslatableText("text.mcmessage.outgoing_msg", target.getDisplayName().getString(), message)).formatted(Formatting.GRAY, Formatting.ITALIC), player.getUuid());
                } else {
                    player.sendSystemMessage((new TranslatableText("commands.message.display.outgoing", target.getDisplayName().getString(), message)).formatted(Formatting.GRAY, Formatting.ITALIC), player.getUuid());
                }
                ServerPlayNetworking.send(target, new Identifier("mcmessage", "incoming_msg"), new PacketByteBuf(Unpooled.copiedBuffer(from.toString().getBytes(StandardCharsets.UTF_8))));
            } else {
                player.server.getCommandManager().execute(player.getCommandSource(), "minecraft:msg " + target.getName().getString() + " " + message.getString());
            }
        });

        return 1;
    }
}
