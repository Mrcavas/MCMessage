package me.mrcavas.mcmessage;

import me.mrcavas.mcmessage.db.Database;
import me.mrcavas.mcmessage.drawable.Notification;
import me.mrcavas.mcmessage.gui.MessagesScreen;
import org.json.JSONException;
import org.json.JSONObject;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
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

    private static final KeyBinding dbinit = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mcmessage.dbinit",
            GLFW.GLFW_KEY_I,
            "category.mcmessage.main"
        ));

    @Override
    public void onInitializeClient() {
        Database.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (open.wasPressed()) {
                MinecraftClient.getInstance().openScreen(new MessagesScreen());
            }
            while (dbinit.wasPressed()) {
                Database.init();
                MessagesScreen.gui.init();
            }
        });



        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientPlayNetworking.send(new Identifier("mcmessage", "ok"), new PacketByteBuf(Unpooled.copiedBuffer("ok".getBytes(StandardCharsets.UTF_8)))));

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "ok"), (client, handler, buf, responseSender) -> {
            String message = buf.toString(StandardCharsets.UTF_8);
            Database.setServerUUID(UUID.fromString(message));
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "incoming_msg"), (client, handler, buf, responseSender) -> {
            String message = buf.toString(StandardCharsets.UTF_8);

            try {
                JSONObject obj = new JSONObject(message);

                client.execute(() -> {
//                    new Renderer().drawNotification(obj.getString("message"), 200, obj.getString("from"));
                    Renderer.draw(new Notification(obj.getString("from"), 200, obj.getString("message")));
                    Database.addMessage(obj.getString("from"), obj.getString("message"), false);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "outgoing_msg"), (client, handler, buf, responseSender) -> {
            String message = buf.toString(StandardCharsets.UTF_8);

            try {
                JSONObject obj = new JSONObject(message);

                client.execute(() -> Database.addMessage(obj.getString("to"), obj.getString("message"), true));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
