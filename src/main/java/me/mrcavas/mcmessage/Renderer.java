package me.mrcavas.mcmessage;

import me.mrcavas.mcmessage.drawable.Drawable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Renderer {

    private static int ticks;
    private static final ArrayList<Drawable> drawables = new ArrayList<>();

    public static void render(MinecraftClient client, MatrixStack matrices, int ticks) {
        Renderer.ticks = ticks;
        System.out.println("tick " + ticks);

        int scaledWidth = client.getWindow().getScaledWidth();

        int currentHeight = 8;

        for (Iterator<Drawable> iter = drawables.iterator(); iter.hasNext();) {
            Drawable drawable = iter.next();
            if (drawable.toDraw(ticks)) {
                drawable.render(client, matrices, scaledWidth - drawable.getWidth() - 8, currentHeight);
                currentHeight += drawable.getHeight() + 8;
            } else {
                iter.remove();
                System.out.println("stop - " + new Date().getTime());
            }
        }
    }

    public static void draw(Drawable drawable) {
        drawable.activate(ticks);
        drawables.add(drawable);
        System.out.println("start- " + new Date().getTime());
    }
}
