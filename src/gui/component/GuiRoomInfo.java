package gui.component;

import java.awt.Color;
import java.awt.Graphics;

import pseudomain.Game;

public class GuiRoomInfo extends GuiComponent {

	private final static int INFO_HEIGHT = 30;
	
	private GuiLabel roomOwner, playerName;
	
	public GuiRoomInfo() {
		this.x = 0;
		this.y = 0;
		roomOwner = new GuiLabel("�ХD�ʺ١G", x+30, y+20, Color.WHITE);
		playerName = new GuiLabel("���a�ʺ١G", x+400, y+20, Color.WHITE);
	}
	
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(x, y, Game.WIDTH * Game.scale, INFO_HEIGHT);
		roomOwner.render(g);
		playerName.render(g);
	}
}
