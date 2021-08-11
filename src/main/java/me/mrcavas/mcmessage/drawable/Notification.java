package me.mrcavas.mcmessage.drawable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.KeybindText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.List;

public class Notification extends Drawable {
    public static final int width = 192;
    public static final int height = 96;
    public final String plName;
    public final int duration;
    public final String text;
    public int expiresAt;

    public Notification(String plName, int duration, String text) {
        this.plName = plName;
        this.duration = duration;
        this.text = text;
    }

    @Override
    public void activate(int ticks) {
        expiresAt = ticks + duration;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int x, int y) {
        TextureManager textureManager = client.getTextureManager();

        textureManager.bindTexture(new Identifier("mcmessage", "textures/gui/notification.png"));
        DrawableHelper.drawTexture(matrices, x - 8, y, 0, 0, 192, 96, width, height);
        client.textRenderer.draw(matrices, new TranslatableText("text.mcmessage.says_you", plName), x - 8 + 21, y + 8, 0);
        client.textRenderer.draw(matrices, new TranslatableText("text.mcmessage.open", new KeybindText("key.mcmessage.open")).getString(), x - 8 + 86, y + 80, 0);
        try {
            textureManager.bindTexture(client.getNetworkHandler().getPlayerListEntry(plName).getSkinTexture());
            DrawableHelper.drawTexture(matrices, x, y + 8, 8, 8, 8, 8, 64, 64);
        } catch (NullPointerException e) {e.printStackTrace();}

        List<OrderedText> lines = client.textRenderer.wrapLines(StringVisitable.plain(text), 168);

        for (int line = 0; line < lines.size(); line++) {
            int lineY = y + 26 + (9 * line);

            if (line == 4 && lines.size() > 5) {
                OrderedText last = OrderedText.concat(lines.get(line), Language.getInstance().reorder(StringVisitable.plain("...")));
                client.textRenderer.draw(matrices, last, x - 8 + 12, lineY, 13027014);
                break;
            }

            client.textRenderer.draw(matrices, lines.get(line), x - 8 + 12, lineY, 13027014);
        }
    }

    @Override
    public boolean toDraw(int ticks) {
        return ticks <= expiresAt;
    }
}
