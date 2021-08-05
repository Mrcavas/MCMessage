package me.mrcavas.mcmessage.gui;

import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.control.*;
import io.github.redstoneparadox.oaktree.client.math.Direction2D;
import io.github.redstoneparadox.oaktree.client.gui.style.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class TestScreen extends Screen {
	private final ControlGui gui;

	public TestScreen() {
		super(new LiteralText("TestScreen"));
		this.gui = new ControlGui(this, testOne());
		this.gui.applyTheme(Theme.vanilla());
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

	private PanelControl testOne() {

		DropdownControl leftDropdown = new DropdownControl()
				.dropdown(
						new ListPanelControl()
								.id("base")
								.size(80, 80)
								.children(4, this::itemLabel)
								.displayCount(4)
				)
				.size(40, 20)
				.id("button")
				.dropdownDirection(Direction2D.LEFT)
				.anchor(Anchor.CENTER);

		leftDropdown.onTick((controlGui, leftDropdownControl) -> {
			leftDropdownControl.dropdownDirection(Direction2D.RIGHT);
		});

		DropdownControl rightDropdown = new DropdownControl()
				.dropdown(
						new ListPanelControl()
								.id("base")
								.size(80, 80)
								.children(4, this::itemLabel)
								.displayCount(4)
				)
				.size(40, 20)
				.id("button")
				.dropdownDirection(Direction2D.RIGHT)
				.anchor(Anchor.CENTER);


		return new PanelControl<>()
				.child(new DropdownControl()
						.dropdown(
								new ListPanelControl()
										.id("base")
										.size(60, 60)
										.child(leftDropdown)
										.child(rightDropdown)
										.displayCount(2)
						)
						.size(60, 20)
						.id("button")
						.anchor(Anchor.CENTER)
				)
				.size(90, 50)
				.anchor(Anchor.CENTER)
				.id("base");


	}

	private LabelControl itemLabel(int number) {

		return new LabelControl()
				.size(60, 20)
				.text("Item No. " + (number + 1))
				.anchor(Anchor.CENTER)
				.shadow(true);


	}
}