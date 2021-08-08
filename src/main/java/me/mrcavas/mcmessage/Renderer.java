package me.mrcavas.mcmessage;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.List;

public class Renderer {

    private static int ticks;
    private static int notificationOnTicks = -1;
    private static String notificationText;
    private static String plName;

    public void render(MinecraftClient client, MatrixStack matrices, int delta) {
        ticks = delta;

        if (notificationOnTicks != -1) {
            if (delta <= notificationOnTicks) {
                TextureManager textureManager = client.getTextureManager();
                int scaledWidth = client.getWindow().getScaledWidth();
                int scaledHeight = client.getWindow().getScaledHeight();

                textureManager.bindTexture(new Identifier("mcmessage", "textures/gui/notification.png"));
                DrawableHelper.drawTexture(matrices, scaledWidth - 192 - 8, 8, 0, 0, 192, 96, 192, 96);
                client.textRenderer.draw(matrices, new TranslatableText("text.mcmessage.says_you", plName), scaledWidth - 192 - 8 + 19, 8 + 8, 0);
                client.textRenderer.draw(matrices, new TranslatableText("text.mcmessage.open", new KeybindText("key.mcmessage.open")).getString(), scaledWidth - 192 - 8 + 86, 8 + 80, 0);
                try {
                    textureManager.bindTexture(client.getNetworkHandler().getPlayerListEntry(plName).getSkinTexture());
                } catch (NullPointerException ignored) {}

                List<OrderedText> lines = client.textRenderer.wrapLines(StringVisitable.plain(notificationText), 168);

                for (int line = 0; line < lines.size(); line++) {
                    int y = 8 + 26 + (9 * line);

                    if (line == 4 && lines.size() > 5) {
                        OrderedText last = OrderedText.concat(lines.get(line), Language.getInstance().reorder(StringVisitable.plain("...")));
                        client.textRenderer.draw(matrices, last, scaledWidth - 192 - 8 + 12, y, 13027014);
                        break;
                    }

                    client.textRenderer.draw(matrices, lines.get(line), scaledWidth - 192 - 8 + 12, y, 13027014);
                }

            } else notificationOnTicks = -1;
        }
    }

    public void drawNotification(String message, int duration, String plName) {
        notificationText = message;
        notificationOnTicks = ticks + duration;
        Renderer.plName = plName;
    }
}
