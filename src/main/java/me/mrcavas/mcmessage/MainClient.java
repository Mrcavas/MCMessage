package me.mrcavas.mcmessage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import me.mrcavas.mcmessage.gui.TestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainClient implements ClientModInitializer {
    private static final KeyBinding open = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mcmessage.open",
            GLFW.GLFW_KEY_N,
            "category.mcmessage.main"
        ));

    @Override
    public void onInitializeClient() {
        Database.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (open.wasPressed()) {
                MinecraftClient.getInstance().openScreen(new TestScreen());
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientPlayNetworking.send(new Identifier("mcmessage", "ok"), new PacketByteBuf(Unpooled.copiedBuffer("ok".getBytes(StandardCharsets.UTF_8))));
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "ok_answ"), (client, handler, buf, responseSender) -> {
            String message = buf.toString(StandardCharsets.UTF_8);
            Database.setServerUUID(UUID.fromString(message));
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "msg"), (client, handler, buf, responseSender) -> {
            String message = buf.toString(StandardCharsets.UTF_8);

            try {
                JsonObject obj = new JsonParser().parse(message).getAsJsonObject();

                client.execute(() -> {
                    new Renderer().drawNotification(obj.get("message").getAsString(), 200, obj.get("from").getAsString());
                    Database.addMessage(obj.get("from").getAsString(), obj.get("message").getAsString(), false);
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "my_msg"), (client, handler, buf, responseSender) -> {
            String message = buf.toString(StandardCharsets.UTF_8);

            try {
                JsonObject obj = new JsonParser().parse(message).getAsJsonObject();

                client.execute(() -> {
                    Database.addMessage(obj.get("to").getAsString(), obj.get("message").getAsString(), true);
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        });
    }
}
