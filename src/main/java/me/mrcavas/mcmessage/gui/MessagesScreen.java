package me.mrcavas.mcmessage.gui;

import io.github.redstoneparadox.oaktree.client.gui.Color;
import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.control.*;
import io.github.redstoneparadox.oaktree.client.gui.style.ColorControlStyle;
import io.github.redstoneparadox.oaktree.client.gui.style.ControlStyle;
import io.github.redstoneparadox.oaktree.client.gui.style.TextureControlStyle;
import io.github.redstoneparadox.oaktree.client.math.Direction2D;
import io.github.redstoneparadox.oaktree.client.gui.style.Theme;
import io.netty.buffer.Unpooled;
import me.mrcavas.mcmessage.db.DBMessage;
import me.mrcavas.mcmessage.db.Database;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MessagesScreen extends Screen {
	public static ControlGui gui;

	public MessagesScreen() {
		super(new LiteralText("MessagesScreen"));
		gui = new ControlGui(this, testOne());

		gui.applyTheme(Theme.vanilla());
	}

	@Override
	public void init(MinecraftClient minecraftClient, int i, int j) {
		super.init(minecraftClient, i, j);
		gui.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		gui.draw(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private Control<? extends Control<?>> testOne() {

//		DropdownControl leftDropdown = new DropdownControl()
//				.dropdown(
//						new ListPanelControl()
//								.id("base")
//								.size(80, 80)
//								.children(4, this::itemLabel)
//								.displayCount(4)
//				)
//				.size(120, 20)
//				.id("button")
//				.dropdownDirection(Direction2D.LEFT)
//				.anchor(Anchor.CENTER);
//
////		leftDropdown.onTick((controlGui, leftDropdownControl) -> leftDropdownControl.dropdownDirection(Direction2D.LEFT));
//
//		DropdownControl rightDropdown = new DropdownControl()
//				.dropdown(
//						new ListPanelControl()
//								.id("base")
//								.size(80, 80)
//								.children(4, this::itemLabel)
//								.displayCount(4)
//				)
//				.size(40, 20)
//				.id("button")
//				.dropdownDirection(Direction2D.RIGHT)
//				.anchor(Anchor.CENTER);


//		return new PanelControl<>()
//				.child(new DropdownControl()
//						.dropdown(
//								new ListPanelControl()
//										.id("base")
//										.size(60, 60)
//										.child(leftDropdown)
//										.child(rightDropdown)
//										.displayCount(2)
//						)
//						.size(60, 20)
//						.id("button")
//						.anchor(Anchor.CENTER)
//				)
//				.size(90, 50)
//				.anchor(Anchor.CENTER)
//				.id("base");
		try {
			HashMap<Long, DBMessage> messages = Database.getMessages(MinecraftClient.getInstance().player.getName().getString());

			ListPanelControl lpc = new ListPanelControl()
					.id("base")
					.size(200, 200)
					.anchor(Anchor.CENTER_RIGHT);

			if (messages != null) {

				for (DBMessage msg : messages.values()) {
					if (msg.isClient) {
						lpc.child(new LabelControl()
								.fitText(true)
								.text(msg.text)
								.size(80, MinecraftClient.getInstance().textRenderer.getWrappedLinesHeight(msg.text, 80))
								.anchor(Anchor.CENTER_RIGHT)
								.fontColor(Color.BLACK));
					} else {
						lpc.child(new LabelControl()
								.fitText(true)
								.text(msg.text)
								.size(80, MinecraftClient.getInstance().textRenderer.getWrappedLinesHeight(msg.text, 80))
								.anchor(Anchor.CENTER_LEFT)
								.fontColor(Color.BLACK));
					}
				}
//
			} else {
				lpc.child(new LabelControl()
						.fitText(true)
						.text("no messages")
						.size(100, MinecraftClient.getInstance().textRenderer.fontHeight)
						.anchor(Anchor.CENTER_LEFT)
						.fontColor(Color.BLACK));
			}

			return new ListPanelControl()
					.id("base")
					.size(100, 100)
					.anchor(Anchor.CENTER)
					.child(new ButtonControl()
							.id("button")
							.size(20, 20)
							.anchor(Anchor.CENTER_RIGHT)
							.onClick((controlGui, buttonControl) -> gui.init()))
					.child(new DropdownControl()
							.dropdown(lpc)
							.id("button")
							.dropdownDirection(Direction2D.RIGHT)
							.size(50, 50)
							.anchor(Anchor.CENTER_LEFT));
		} catch (Exception e) {
			e.printStackTrace();
			return new LabelControl()
					.text("FUUUUUUUUUUUCK")
					.fontColor(Color.BLUE)
					.size(100, 100)
					.anchor(Anchor.CENTER);
		}
	}

	private LabelControl itemLabel(int number) {

		return new LabelControl()
				.size(20, 20)
				.text(String.valueOf(number + 1))
				.anchor(Anchor.CENTER)
				.shadow(true);

	}
}