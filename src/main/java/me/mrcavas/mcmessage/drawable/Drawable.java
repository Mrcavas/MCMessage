package me.mrcavas.mcmessage.drawable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Drawable {
    public abstract int getWidth();
    public abstract int getHeight();

    public abstract void render(MinecraftClient client, MatrixStack matrices, int x, int y);

    public abstract void activate(int ticks);

    public abstract boolean toDraw(int ticks);
}
