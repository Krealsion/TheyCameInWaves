package wavesofterror;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MainMenuState extends GameState {

	int Opacity;
	public static final int MouseSize = 12;
	Vector2 MouseDraw;

	int PlayWidth = 500;
	int PlayHeight = 80;
	Button PlayButton;

	int ExitWidth = 400;
	int ExitHeight = 80;
	Button ExitButton;

	Button InfoButton;

	static Clip c;

	public MainMenuState(Renderer Render, StateManager Controller) {
		super(Render, Controller);
		MouseDraw = new Vector2();
		PlayButton = new Button(new Rectangle(Renderer.WindowWidth / 2 - PlayWidth / 2, 400, PlayWidth, PlayHeight), "Play Game", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 10, 30);
		InfoButton = new Button(new Rectangle(Renderer.WindowWidth / 2 - ExitWidth / 2, 550, ExitWidth, ExitHeight), "Info", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 10, 30);
		ExitButton = new Button(new Rectangle(Renderer.WindowWidth / 2 - ExitWidth / 2, 700, ExitWidth, ExitHeight), "Exit", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 10, 30);
	}

	@Override
	public void Pause() {

	}

	@Override
	public void Resume() {

	}

	@Override
	public void Draw(Graphics g) {
		Vector2 MousePos = new Vector2(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
		g.setColor(new Color(15, 60, 25));
		g.fillRect(0, 0, Renderer.WindowWidth, Renderer.WindowHeight);
		g.setColor(Color.DARK_GRAY);
		//Title
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.BOLD, 100));
		g.drawString("They Came In Waves", Renderer.WindowWidth / 2 - 500, 200);
		//Buttons
		PlayButton.Draw(g);
		ExitButton.Draw(g);
		InfoButton.Draw(g);
		//Mouse
		g.setColor(Color.BLACK);
		g.fillOval((int) MousePos.GetX() - MouseSize / 2, (int) MousePos.GetY() - MouseSize / 2, MouseSize, MouseSize);
		g.setColor(Color.RED);
		g.fillOval((int) MouseDraw.GetX() - MouseSize / 2 + 2, (int) MouseDraw.GetY() - MouseSize / 2 + 2, MouseSize - 4, MouseSize - 4);
	}

	private void HandleKeys() {
		if (Input.IsKeyPressed(KeyEvent.VK_ESCAPE)) {
			Controller.Exit();
		}
	}

	@Override
	public void Update() {
		Vector2 MousePos = new Vector2(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
		PlayButton.Update(MousePos);
		ExitButton.Update(MousePos);
		InfoButton.Update(MousePos);
		double ModX = (MousePos.GetX() - MouseDraw.GetX()) / 5d;
		double ModY = (MousePos.GetY() - MouseDraw.GetY()) / 5d;
		MouseDraw.SetX(MouseDraw.GetX() + ModX);
		MouseDraw.SetY(MouseDraw.GetY() + ModY);
		if (Math.abs(MouseDraw.GetX() - MousePos.GetX()) < 1) {
			MouseDraw.SetX(MousePos.GetX());
		}
		if (Math.abs(MouseDraw.GetY() - MousePos.GetY()) < 1) {
			MouseDraw.SetY(MousePos.GetY());
		}
		HandleKeys();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Vector2 MousePos = new Vector2(e.getX(), e.getY());
		if (PlayButton.ContainsPoint(MousePos)) {
			Controller.Push(new PlayState(Render, Controller));
			PlaySong();
		}
		if (ExitButton.ContainsPoint(MousePos)) {
			Controller.Exit();
		}
		if (InfoButton.ContainsPoint(MousePos)) {
			Controller.Push(new InfoState(Render, Controller));
		}
	}

	public static synchronized void PlaySong() {
		if (c != null) {
			c.stop();
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					c = AudioSystem.getClip();
					File f = new File("src/wavesofterror/Resources/Song" + (int) (Math.random() * 3) + ".wav");
					System.out.println(f.getAbsolutePath());
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
					c.open(inputStream);
					c.loop(Clip.LOOP_CONTINUOUSLY);
					c.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}
