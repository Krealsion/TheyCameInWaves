package wavesofterror;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class InfoState extends GameState {

	public InfoState(Renderer Render, StateManager Controller) {
		super(Render, Controller);
	}

	@Override
	public void Pause() {

	}

	@Override
	public void Resume() {

	}

	@Override
	public void Draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Renderer.WindowWidth, Renderer.WindowHeight);
		//Title
		g.setColor(Color.RED);
		g.setFont(new Font("TimesRoman", Font.BOLD, 30));
		g.drawString("They Came In Waves is a top down 2D shooter with elements of tower defense.", 20, 40);
		g.drawString("Why? Because I thought it would be a good idea and thats enough of that.", 20, 80);
		g.drawString("You are trying to protect your base, your nexus.", 20, 120);
		g.drawString("Why? Because I thought it would be a good idea and thats enough of that.", 20, 160);
		g.drawString("You are a glowy ball of gitch and color that flies around able to shoot math.", 20, 200);
		g.drawString("Why? Because I thought it would be a good idea and thats enough of that.", 20, 240);
		g.drawString("Build some walls. Build some towers. Play with trig. Don't die.", 20, 280);
		g.drawString("Why? Because I thought it would be a good idea and thats enough of that.", 20, 320);
		g.drawString("Good luck, and have fun.", 20, 360);
		g.drawString("Why? Because I thought it would be a good idea and thats enough of that.", 20, 400);
		g.drawString("WSAD to move around.", 20, 500);
		g.drawString("Space to Pause. Can only do some things while paused.", 20, 540);
		g.drawString("Q to send waves early for extra gold", 20, 580);
		g.drawString("Escape to go back to menu", 20, 620);
	}

	private void HandleKeys() {
		if (Input.IsKeyPressed(KeyEvent.VK_ESCAPE)) {
			Controller.Pop();
		}
	}

	@Override
	public void Update() {
		HandleKeys();
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

}
