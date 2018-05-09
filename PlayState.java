package wavesofterror;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.SwingUtilities;
import wavesofterror.Enemies.*;
import wavesofterror.Towers.*;

public class PlayState extends GameState {

	public static final int MapSize = 4200;

	public boolean[][] Walls;
	public boolean PlacedWall;
	public Tower[][] Towers;
	public boolean PlacedTower;
	public boolean CreateWalls = true;

	public static final int WallSeperation = 35;

	int Score;

	int UpgradeCostWeapon = 400;
	int UpgradeCostTowers = 300;

	int Level = 1;

	Vector2 CameraShake;
	public int Wave = 1;
	public static ArrayList<Enemy> Enemies;
	public static ArrayList<ProjectileCluster> PJCs;

	public static Vector2 PlayerPosition;
	public static Vector2 PlayerDelta;
	public int PlayerSize = 30;

	public static int BaseDamage = 12;
	public double Gold = 100;
	public double GoldPerSecond = 3;
	public final int BaseBaseHealth = 30;
	public int BaseHealth = BaseBaseHealth;

	boolean Paused;
	public int NumSlots = 3;
	public Weapon[] Slots;
	public int[] Cooldown;
	public int BaseCoolDown = 250;
	public int ActiveSlot = 0;

	public static String AlertText = "";
	public static int AlertOpacity = 0;

	int NextWaveTimer = 60000;

	//UI
	int UIWidth = 400;
	int ActivePanel = -1;						//Towers, Walls, Weapons, Upgrades
	public ArrayList<Button> BasicButtons;	//Towers, Walls, Weapons, Upgrades
	int ActiveFormula;
	String ActivesFormula;
	public ArrayList<Button> FormulaPanel;
	public Button CreateWall;
	public Button SwapBuild;
	public ArrayList<Button> TowerPanel;
	public ArrayList<Button> UpgradePanel;

	//Action
	boolean TakingAction;
	int State = -1;			// PlaceWall, PlaceTower
	Vector2 FirstWall;		// Empty Until the first point is selected
	int TowerSelected;		// Arrow, Cold, Fire, Stun, Return
	int SelectedLevel;

	public PlayState(Renderer Render, StateManager Controller) {
		super(Render, Controller);
		Enemies = new ArrayList<>();
		PJCs = new ArrayList<>();
		PlayerPosition = new Vector2(MapSize / 2, MapSize / 2);
		PlayerDelta = new Vector2();
		Walls = new boolean[MapSize / WallSeperation][MapSize / WallSeperation];
		Towers = new Tower[MapSize / WallSeperation][MapSize / WallSeperation];
		Slots = new Weapon[NumSlots];
		Cooldown = new int[NumSlots];
		Slots[0] = new Weapon("2sx)", 4, 1.5, 3, false, 15, 30, 3, 5, true, false);
		Slots[1] = new Weapon("3sx)", 8, 2, 3, false, 15, 30, 3, 5, true, false);
		Slots[2] = new Weapon("4s3x)", 3, 2, 3, true, 15, 30, 3, 5, false, false);
		InitializeUI();
	}

	private void InitializeUI() {
		BasicButtons = new ArrayList<>();
		int WW = Renderer.WindowWidth;
		BasicButtons.add(new Button(new Rectangle(WW - UIWidth, 0, UIWidth / 2, 50), "Towers", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
		BasicButtons.add(new Button(new Rectangle(WW - UIWidth / 2, 0, UIWidth / 2, 50), "Walls", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
		BasicButtons.add(new Button(new Rectangle(WW - UIWidth, 50, UIWidth / 2, 50), "Weapons", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
		BasicButtons.add(new Button(new Rectangle(WW - UIWidth / 2, 50, UIWidth / 2, 50), "Upgrades", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
		CreateFormulaPanel();
		CreateWallPanel();
		CreateTowerPanel();
		CreateUpgradePanel();
	}

	private void CreateWallPanel() {
		CreateWall = new Button(new Rectangle(Renderer.WindowWidth - UIWidth, 100, UIWidth, Renderer.WindowHeight - 300), "Create Wall", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 50);
		SwapBuild = new Button(new Rectangle(Renderer.WindowWidth - UIWidth, Renderer.WindowHeight - 200, UIWidth, 200), "ToggleBuilding (Building)", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 30);
	}

	private void CreateFormulaPanel() {
		FormulaPanel = new ArrayList<>();
		int WW = Renderer.WindowWidth;
		ActivesFormula = Slots[ActiveFormula].Formula;
		for (int i = 0; i < NumSlots; i++) {
			FormulaPanel.add(new Button(new Rectangle(WW - UIWidth + (int) (UIWidth * (double) i / (double) NumSlots), 100, UIWidth / NumSlots, 40), "Slot " + (i + 1), Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 3, 18));
		}
		FormulaPanel.add(new Button(new Rectangle(WW - UIWidth, 140, 400, 60), "Formula " + (ActiveFormula + 1), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, 0, 18));
		FormulaPanel.add(new Button(new Rectangle(WW - UIWidth, 200, 400, 60), Projectile.ToEnglish(ActivesFormula), Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, 0, 18));
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 5; i++) {
				FormulaPanel.add(new Button(new Rectangle(WW - UIWidth + (80 * i), 260 + (j * 80), 80, 80), "", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.PINK, 4, 18));
			}
		}
		for (int i = 0; i < 4; i++) {
			FormulaPanel.add(new Button(new Rectangle(WW - UIWidth + (400 * i / 4), 900, 100, 100), "", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 4, 18));
		}
		for (int i = 5; i < FormulaPanel.size(); i++) {
			if (i == 5) {
				FormulaPanel.get(i).Text = "Clear";
			} else if (i == 6) {
				FormulaPanel.get(i).Text = "Back";
			} else if (i == 7) {
				FormulaPanel.get(i).Text = "Apply";
			} else if (i == 8) {
				FormulaPanel.get(i).Text = ")";
			} else if (i == 9) {
				FormulaPanel.get(i).Text = "-";
			} else if (i == 10) {
				FormulaPanel.get(i).Text = ".";
			} else if (i == 11) {
				FormulaPanel.get(i).Text = "Sin(";
			} else if (i == 12) {
				FormulaPanel.get(i).Text = "Cos(";
			} else if (i == 13) {
				FormulaPanel.get(i).Text = "Tan(";
			} else if (i == 14) {
				FormulaPanel.get(i).Text = "x";
			} else if (i <= 23) {
				FormulaPanel.get(i).Text = (i - 14) + "";
			} else if (i == 24) {
				FormulaPanel.get(i).Text = "0";
			} else if (i == 25) {
				FormulaPanel.get(i).Text = "Speed";
			} else if (i == 26) {
				FormulaPanel.get(i).Text = "Duration";
			} else if (i == 27) {
				FormulaPanel.get(i).Text = "Count";
			} else if (i == 28) {
				FormulaPanel.get(i).Text = "Spread";
			} else if (i == 29) {
				FormulaPanel.get(i).Text = "Period";
			} else if (i == 30) {
				FormulaPanel.get(i).Text = Slots[ActiveFormula].Speed + "";
			} else if (i == 31) {
				FormulaPanel.get(i).Text = Slots[ActiveFormula].Duration + "";
			} else if (i == 32) {
				FormulaPanel.get(i).Text = Slots[ActiveFormula].Count + "";
			} else if (i == 33) {
				FormulaPanel.get(i).Text = Slots[ActiveFormula].Spread + "";
			} else if (i == 34) {
				FormulaPanel.get(i).Text = Slots[ActiveFormula].Period + "";
			} else if (i <= 39) {
				FormulaPanel.get(i).Text = "+";
			} else if (i <= 44) {
				FormulaPanel.get(i).Text = "-";
			} else if (i == 45) {
				FormulaPanel.get(i).Text = "Damage";
			} else if (i == 46) {
				FormulaPanel.get(i).Text = CalculateDamage() + "";
			} else if (i == 47) {
				if (Slots[ActiveFormula].NegateX) {
					FormulaPanel.get(i).Text = "NO-X";
				} else if (Slots[ActiveFormula].NegateY) {
					FormulaPanel.get(i).Text = "NO-Y";
				} else {
					FormulaPanel.get(i).Text = "NORM";
				}
			} else if (i == 48) {
				if (Slots[ActiveSlot].Stationary) {
					FormulaPanel.get(i).Text = "Stationary";
				} else {
					FormulaPanel.get(i).Text = "Moving";
				}
			}
		}
	}

	private void CreateTowerPanel() {
		TowerPanel = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			TowerPanel.add(new Button(new Rectangle(Renderer.WindowWidth - UIWidth, 100 + (i * 100), UIWidth, 100), "", Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
		}
		for (int i = 0; i < TowerPanel.size(); i++) {
			if (i == 0) {
				TowerPanel.get(i).Text = "Arrow Tower | 50g";
			} else if (i == 1) {
				TowerPanel.get(i).Text = "Cold Tower | 150g";
			} else if (i == 2) {
				TowerPanel.get(i).Text = "Fire Tower | 200g";
			} else if (i == 3) {
				TowerPanel.get(i).Text = "Stun Tower | 500g";
			} else if (i == 4) {
				TowerPanel.get(i).Text = "Return Tower | 1000g";
			}
		}
	}

	private void CreateUpgradePanel() {
		UpgradePanel = new ArrayList<>();
		UpgradePanel.add(new Button(new Rectangle(Renderer.WindowWidth - UIWidth, 100, UIWidth, (Renderer.WindowHeight - 100) / 2), "Upgrade Weapons | " + UpgradeCostWeapon, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
		UpgradePanel.add(new Button(new Rectangle(Renderer.WindowWidth - UIWidth, 100 + (Renderer.WindowHeight - 100) / 2, UIWidth, (Renderer.WindowHeight - 100) / 2), "Upgrade Towers | " + UpgradeCostTowers, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.RED, 5, 20));
	}

	@Override
	public void Pause() {
		Paused = true;
	}

	@Override
	public void Resume() {
		Paused = false;
	}

	@Override
	public void Draw(Graphics g) {
		Vector2 Offset = jMath.GetOffset();
		//Draw Projectiles
		for (int i = 0; i < PJCs.size(); i++) {
			PJCs.get(i).Draw(g);
		}
		//Draw Buttons
		for (int i = 0; i < BasicButtons.size(); i++) {
			BasicButtons.get(i).Draw(g);
		}
		//Draw Towers
		for (int i = 0; i < Towers.length; i++) {
			for (int j = 0; j < Towers.length; j++) {
				if (Towers[i][j] != null) {
					Towers[i][j].Draw(g);
				}
			}
		}
		//Draw Border
		g.setColor(Color.BLACK);
		g.drawRect((int) -Offset.GetX(), (int) -Offset.GetY(), MapSize, MapSize);
		//Draw Walls
		g.setColor(Color.BLACK);
		for (int i = 0; i < Walls.length; i++) {
			for (int j = 0; j < Walls[0].length; j++) {
				if (Walls[i][j]) {
					if (Walls[i + 1][j]) {
						DrawThickLine(g, new Vector2(j * WallSeperation - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY()), new Vector2(j * WallSeperation - (int) Offset.GetX(), (i + 1) * WallSeperation - (int) Offset.GetY()));
					}
					if (Walls[i - 1][j]) {
						DrawThickLine(g, new Vector2(j * WallSeperation - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY()), new Vector2(j * WallSeperation - (int) Offset.GetX(), (i - 1) * WallSeperation - (int) Offset.GetY()));
					}
					if (Walls[i][j + 1]) {
						DrawThickLine(g, new Vector2(j * WallSeperation - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY()), new Vector2((j + 1) * WallSeperation - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY()));
					}
					if (Walls[i][j - 1]) {
						DrawThickLine(g, new Vector2(j * WallSeperation - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY()), new Vector2((j - 1) * WallSeperation - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY()));
					}
				}
			}
		}
		//Draw Enemies
		for (int i = 0; i < Enemies.size(); i++) {
			Enemies.get(i).Draw(g);
		}
		//DrawNexus
		g.setColor(new Color(80, 10, 30));
		g.fillRect(MapSize / 2 - (int) Offset.GetX() - 20, MapSize / 2 - (int) Offset.GetY() - 20, 40, 40);
		int[] xPoint = new int[]{MapSize / 2 - (int) Offset.GetX(), MapSize / 2 + 20 - (int) Offset.GetX(), MapSize / 2 - (int) Offset.GetX(), MapSize / 2 - 20 - (int) Offset.GetX()};
		int[] yPoint = new int[]{MapSize / 2 - (int) Offset.GetY() - 20, MapSize / 2 - (int) Offset.GetY(), MapSize / 2 - (int) Offset.GetY() + 20, MapSize / 2 - (int) Offset.GetY()};
		g.setColor(new Color(212, 175, 55));
		g.fillPolygon(xPoint, yPoint, 4);
		g.setColor(Color.PINK);
		g.fillOval(MapSize / 2 - (int) Offset.GetX() - 14, MapSize / 2 - (int) Offset.GetY() - 14, 28, 28);
		//Draw Player
		for (int i = 0; i < PlayerSize; i += 2) {
			Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
			g.setColor(c);
			g.fillOval((int) (PlayerPosition.GetX() - Offset.GetX() - (PlayerSize - i) / 2), (int) (PlayerPosition.GetY() - Offset.GetY() - (PlayerSize - i) / 2), PlayerSize - i, PlayerSize - i);
		}
		//Draw Alert
		g.setFont(new Font("TimesRoman", Font.BOLD, 40));
		g.setColor(new Color(255, 0, 0, AlertOpacity));
		int Width = g.getFontMetrics().stringWidth(AlertText);
		g.drawString(AlertText, Renderer.WindowWidth / 2 - (int) Width / 2, 250);
		//Handle Drawing in Tower Mode
		if (ActivePanel == 0) {
			if (State == 1) {
				g.setColor(Color.BLACK);
				for (int i = 0; i < Walls.length; i++) {
					g.drawLine(-(int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY(), MapSize - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY());
				}
				for (int i = 0; i < Walls.length; i++) {
					g.drawLine(i * WallSeperation - (int) Offset.GetX(), -(int) Offset.GetY(), i * WallSeperation - (int) Offset.GetX(), MapSize - (int) Offset.GetY());
				}
				Vector2 MousePos = new Vector2(MouseInfo.getPointerInfo().getLocation().getX() + Offset.GetX(), MouseInfo.getPointerInfo().getLocation().getY() + Offset.GetY());
				int x = (int) (MousePos.GetX() / WallSeperation);
				int y = (int) (MousePos.GetY() / WallSeperation);
				if (MousePos.GetX() % WallSeperation > WallSeperation / 2) {
					x++;
				}
				if (MousePos.GetY() % WallSeperation > WallSeperation / 2) {
					y++;
				}
				g.setColor(Color.RED);
				g.fillOval(x * WallSeperation - (int) Offset.GetX() - 4, y * WallSeperation - (int) Offset.GetY() - 4, 8, 8);
			}
			for (int i = 0; i < TowerPanel.size(); i++) {
				TowerPanel.get(i).Draw(g);
			}
		} else if (ActivePanel == 1) {
			//Handle Drawing in Wall Mode
			if (State == 0) {
				g.setColor(Color.BLACK);
				for (int i = 0; i < Walls.length; i++) {
					g.drawLine(-(int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY(), MapSize - (int) Offset.GetX(), i * WallSeperation - (int) Offset.GetY());
				}
				for (int i = 0; i < Walls.length; i++) {
					g.drawLine(i * WallSeperation - (int) Offset.GetX(), -(int) Offset.GetY(), i * WallSeperation - (int) Offset.GetX(), MapSize - (int) Offset.GetY());
				}
				if (!PlacedWall) {
					g.setColor(Color.MAGENTA);
					g.setFont(new Font("TimesRoman", Font.BOLD, 35));
					Width = g.getFontMetrics().stringWidth("Hold Shift to ChainBuild Walls");
					g.drawString("Hold Shift to ChainBuild Walls", Renderer.WindowWidth / 2 - (int) Width / 2, 400);
					Width = g.getFontMetrics().stringWidth("Right Click to Cancel");
					g.drawString("Right Click to Cancle", Renderer.WindowWidth / 2 - (int) Width / 2, 460);
				}
				Vector2 MousePos = new Vector2(MouseInfo.getPointerInfo().getLocation().getX() + Offset.GetX(), MouseInfo.getPointerInfo().getLocation().getY() + Offset.GetY());
				int x = (int) (MousePos.GetX() / WallSeperation);
				int y = (int) (MousePos.GetY() / WallSeperation);
				if (MousePos.GetX() % WallSeperation > WallSeperation / 2) {
					x++;
				}
				if (MousePos.GetY() % WallSeperation > WallSeperation / 2) {
					y++;
				}
				Vector2 ShadowWall = new Vector2(x, y);
				if (FirstWall != null) {
					if (x == FirstWall.GetX() || y == FirstWall.GetY()) {
						g.setColor(Color.GREEN);
					} else {
						g.setColor(Color.RED);
					}
					DrawThickLine(g, new Vector2((int) FirstWall.GetX() * WallSeperation - (int) Offset.GetX(), (int) FirstWall.GetY() * WallSeperation - (int) Offset.GetY()),
							new Vector2((int) ShadowWall.GetX() * WallSeperation - (int) Offset.GetX(), (int) ShadowWall.GetY() * WallSeperation - (int) Offset.GetY()));
				}
				g.setColor(Color.RED);
				g.fillOval(x * WallSeperation - (int) Offset.GetX() - 4, y * WallSeperation - (int) Offset.GetY() - 4, 8, 8);

			}
			CreateWall.Draw(g);
			SwapBuild.Draw(g);
		} else if (ActivePanel == 2) {
			//Handle Drawing in WeaponMode
			for (int i = 0; i < FormulaPanel.size(); i++) {
				FormulaPanel.get(i).Draw(g);
			}
		} else if (ActivePanel == 3) {
			for (int i = 0; i < UpgradePanel.size(); i++) {
				UpgradePanel.get(i).Draw(g);
			}
		}

		//Draw Important Info
		g.setFont(new Font("TimesRoman", Font.BOLD, 40));
		g.setColor(new Color(212, 175, 55));
		g.drawString("GOLD", Renderer.WindowWidth - 550, 60);
		g.drawString("" + (int) Gold, Renderer.WindowWidth - 550, 110);
		g.setColor(Color.RED);
		Width = g.getFontMetrics().stringWidth((int) (NextWaveTimer / 1000) + " seconds till next wave!");
		g.drawString((int) (NextWaveTimer / 1000) + " seconds till wave " + Wave + "!", Renderer.WindowWidth / 2 - Width / 2, 60);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		Width = g.getFontMetrics().stringWidth(Enemies.size() + " enemies remaining!");
		g.drawString(Enemies.size() + " enemies remaining!", Renderer.WindowWidth / 2 - Width / 2, 120);
		g.setFont(new Font("TimesRoman", Font.BOLD, 50));
		g.setColor(new Color(255 * BaseHealth / BaseBaseHealth, 0, 0));
		g.drawString("Base Health", 60, 70);
		g.drawString("" + BaseHealth, 60, 130);
	}

	private void DrawThickLine(Graphics g, Vector2 P1, Vector2 P2) {
		g.drawLine((int) P1.GetX(), (int) P1.GetY(), (int) P2.GetX(), (int) P2.GetY());
		g.drawLine((int) P1.GetX() + 1, (int) P1.GetY(), (int) P2.GetX() + 1, (int) P2.GetY());
		g.drawLine((int) P1.GetX() - 1, (int) P1.GetY(), (int) P2.GetX() - 1, (int) P2.GetY());
		g.drawLine((int) P1.GetX(), (int) P1.GetY() + 1, (int) P2.GetX(), (int) P2.GetY() + 1);
		g.drawLine((int) P1.GetX(), (int) P1.GetY() - 1, (int) P2.GetX(), (int) P2.GetY() - 1);
	}

	public static void Alert(String s) {
		AlertText = s;
		AlertOpacity = 255;
	}

	private void HandleKeys() {
		if (Input.IsKeyPressed(KeyEvent.VK_ESCAPE)) {
			Controller.Pop();
		}
		if (!Paused) {
			if (Input.IsKeyDown(KeyEvent.VK_W)) {
				PlayerDelta.AddY(-.5);
			}
			if (Input.IsKeyDown(KeyEvent.VK_S)) {
				PlayerDelta.AddY(.5);
			}
			if (Input.IsKeyDown(KeyEvent.VK_A)) {
				PlayerDelta.AddX(-.5);
			}
			if (Input.IsKeyDown(KeyEvent.VK_D)) {
				PlayerDelta.AddX(.5);
			}
			if (Input.IsKeyPressed(KeyEvent.VK_1)) {
				ActiveSlot = 0;
			}
			if (Input.IsKeyPressed(KeyEvent.VK_2)) {
				ActiveSlot = 1;
			}
			if (Input.IsKeyPressed(KeyEvent.VK_3)) {
				ActiveSlot = 2;
			}
			if (Input.IsKeyPressed(KeyEvent.VK_Q)) {
				Gold += NextWaveTimer / 500;
				NextWaveTimer = 0;
			}
		}
		if (Input.IsKeyPressed(KeyEvent.VK_SPACE)) {
			if (Paused) {
				Resume();
			} else {
				Pause();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Vector2 MousePos = new Vector2(e.getX(), e.getY());
		Vector2 Offset = jMath.GetOffset();
		Vector2 OffsetMousePos = new Vector2(e.getX() + Offset.GetX(), e.getY() + Offset.GetY());
		if (SwingUtilities.isLeftMouseButton(e)) {
			if ((e.getX() < Renderer.WindowWidth - 400 && State == -1) || (ActivePanel == -1 && (MousePos.GetX() < UIWidth || MousePos.GetY() > 200))) {
				if (Cooldown[ActiveSlot] <= 0) {
					ProjectileCluster Shot = Slots[ActiveSlot].Shoot(PlayerPosition, OffsetMousePos);
					PJCs.add(Shot);
					Cooldown[ActiveSlot] = BaseCoolDown;
				}
			} else {
				for (int i = 0; i < BasicButtons.size(); i++) {
					if (BasicButtons.get(i).ContainsPoint(MousePos)) {
						ActivePanel = i;
						break;
					}
				}
				switch (ActivePanel) {	//This tells what specific buttons or clicks to be listening for
					case 0:	//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers//Towers
						if (!PlacedTower) {
							Alert("You must place towers on walls!");
						}
						if (State == 1) {
							int x = (int) (OffsetMousePos.GetX() / WallSeperation);
							int y = (int) (OffsetMousePos.GetY() / WallSeperation);
							if (OffsetMousePos.GetX() % WallSeperation > WallSeperation / 2) {
								x++;
							}
							if (OffsetMousePos.GetY() % WallSeperation > WallSeperation / 2) {
								y++;
							}
							if (Walls[y][x]) {
								switch (TowerSelected) {
									case 0:
										if (Gold > 50 && Towers[y][x] == null) {
											Gold -= 50;
											Towers[y][x] = new Arrow(new Vector2(x * WallSeperation, y * WallSeperation));
											PlacedTower = true;
											for (int i = 0; i < Level - 1; i++) {
												Towers[y][x].LevelUp();
											}
											if (!Input.IsKeyDown(KeyEvent.VK_SHIFT)) {
												State = -1;
												TowerSelected = -1;
											}
										} else {
											Alert("Not Enough Gold!");
										}
										break;
									case 1:
										if (Gold > 150 && Towers[y][x] == null) {
											Gold -= 150;
											Towers[y][x] = new Ice(new Vector2(x * WallSeperation, y * WallSeperation));
											PlacedTower = true;
											for (int i = 0; i < Level - 1; i++) {
												Towers[y][x].LevelUp();
											}
											if (!Input.IsKeyDown(KeyEvent.VK_SHIFT)) {
												State = -1;
												TowerSelected = -1;
											}
										} else {
											Alert("Not Enough Gold!");
										}
										break;
									case 2:
										if (Gold > 200 && Towers[y][x] == null) {
											Gold -= 200;
											Towers[y][x] = new FireTower(new Vector2(x * WallSeperation, y * WallSeperation));
											PlacedTower = true;
											for (int i = 0; i < Level - 1; i++) {
												Towers[y][x].LevelUp();
											}
											if (!Input.IsKeyDown(KeyEvent.VK_SHIFT)) {
												State = -1;
												TowerSelected = -1;
											}
										} else {
											Alert("Not Enough Gold!");
										}
										break;
									case 3:
										break;
									case 4:
										break;
								}
							}
						}
						for (int i = 0; i < TowerPanel.size(); i++) {
							if (TowerPanel.get(i).ContainsPoint(MousePos)) {
								TowerSelected = i;
								State = 1;
								break;
							}
						}
						break;
					case 1:	//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls//Walls
						if (!PlacedWall) {
							Alert("Walls must be horizontal or vertial!");
						}
						if (CreateWall.ContainsPoint(MousePos)) {
							State = 0;
							return;
						}
						if (SwapBuild.ContainsPoint(MousePos)) {
							CreateWalls = !CreateWalls;
							if (CreateWalls) {
								SwapBuild.Text = "ToggleBuilding (Creating)";
							} else {
								SwapBuild.Text = "ToggleBuilding (Destroying)";
							}
							return;
						}
						if (State == 0) {
							int x = (int) (OffsetMousePos.GetX() / WallSeperation);
							int y = (int) (OffsetMousePos.GetY() / WallSeperation);
							if (OffsetMousePos.GetX() % WallSeperation > WallSeperation / 2) {
								x++;
							}
							if (OffsetMousePos.GetY() % WallSeperation > WallSeperation / 2) {
								y++;
							}
							if (FirstWall == null) {
								FirstWall = new Vector2(x, y);
							} else if (x == FirstWall.GetX() || y == FirstWall.GetY()) {
								boolean[][] BackupWalls = new boolean[Walls.length][Walls[0].length];
								for (int i = 0; i < Walls.length; i++) {
									for (int j = 0; j < Walls.length; j++) {
										BackupWalls[i][j] = Walls[i][j];
									}
								}
								if (x == FirstWall.GetX()) {
									for (int i = Math.min(y, (int) FirstWall.GetY()); i <= Math.max(y, (int) FirstWall.GetY()); i++) {
										Walls[i][x] = CreateWalls;
									}
								} else {
									for (int i = Math.min(x, (int) FirstWall.GetX()); i <= Math.max(x, (int) FirstWall.GetX()); i++) {
										Walls[y][i] = CreateWalls;
									}
								}
								PlacedWall = true;
								boolean Blocking = true;
								for (int i = 0; i < Walls.length; i += 2) {
									if (!GetPath(new Vector2(0, i), new Vector2(MapSize / WallSeperation / 2, MapSize / WallSeperation / 2)).isEmpty()) {
										Blocking = false;
										break;
									}
									if (!GetPath(new Vector2(Walls.length - 1, i), new Vector2(MapSize / WallSeperation / 2, MapSize / WallSeperation / 2)).isEmpty()) {
										Blocking = false;
										break;
									}
									if (!GetPath(new Vector2(i, 0), new Vector2(MapSize / WallSeperation / 2, MapSize / WallSeperation / 2)).isEmpty()) {
										Blocking = false;
										break;
									}
									if (!GetPath(new Vector2(i, Walls[0].length - 1), new Vector2(MapSize / WallSeperation / 2, MapSize / WallSeperation / 2)).isEmpty()) {
										Blocking = false;
										break;
									}
								}
								if (Blocking) {
									Alert("Could not place wall because of blocking!");
									Walls = BackupWalls;
								}
								if (Input.IsKeyDown(KeyEvent.VK_SHIFT)) {
									FirstWall = new Vector2(x, y);
								} else {
									FirstWall = null;
									State = -1;
								}
								CalculatePaths();
							}
						}
						break;
					case 2:	//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons//Weapons
						for (int i = 0; i < FormulaPanel.size(); i++) {
							if (FormulaPanel.get(i).ContainsPoint(MousePos)) {
								if (i < 3) {
									ActiveFormula = i;
									FormulaPanel.get(3).Text = "Formula " + (ActiveFormula + 1);
									ActivesFormula = Slots[ActiveFormula].Formula;
									UpdateFormula();
									break;
								} else if (i == 5) {
									ActivesFormula = "";
									FormulaPanel.get(4).Text = "";
								} else if (i == 6) {
									if (ActivesFormula.length() > 0) {
										ActivesFormula = ActivesFormula.substring(0, ActivesFormula.length() - 1);
									}
								} else if (i == 7) {
									String Old = Slots[ActiveFormula].Formula;
									Slots[ActiveFormula].Formula = ActivesFormula;
									try {
										Projectile p = new Projectile(2, new Vector2(), 1, 1, 0, ActivesFormula, 1, false, -1, false, false);
										p.ParseFormula(ActivesFormula);
									} catch (Exception ex) {
										Slots[ActiveFormula].Formula = Old;
										FormulaPanel.get(4).Inner = Color.RED;
									}
								} else if (i == 8) {
									ActivesFormula += ')';
								} else if (i == 9) {
									ActivesFormula += '-';
								} else if (i == 10) {
									ActivesFormula += '.';
								} else if (i == 11) {
									ActivesFormula += 's';
								} else if (i == 12) {
									ActivesFormula += 'c';
								} else if (i == 13) {
									ActivesFormula += 't';
								} else if (i == 14) {
									ActivesFormula += 'x';
								} else if (i <= 23) {
									ActivesFormula += "" + (i - 14);
								} else if (i == 24) {
									ActivesFormula += '0';
								} else if (i == 25) {
									ActivesFormula = ActivesFormula.substring(0, ActivesFormula.length() - 2);
								} else if (i == 35) {
									Slots[ActiveFormula].Speed++;
								} else if (i == 36) {
									Slots[ActiveFormula].Duration++;
								} else if (i == 37) {
									Slots[ActiveFormula].Count++;
								} else if (i == 38) {
									if (Slots[ActiveFormula].Spread < 4) {
										int Temp = (int) (Slots[ActiveFormula].Spread * 10);
										Temp += 2;
										Slots[ActiveFormula].Spread = (double) Temp / 10;
									}
								} else if (i == 39) {
									if (Slots[ActiveFormula].Period < 10) {
										Slots[ActiveFormula].Period++;
									}
								} else if (i == 40) {
									if (Slots[ActiveFormula].Speed > 4) {
										Slots[ActiveFormula].Speed--;
									}
								} else if (i == 41) {
									if (Slots[ActiveFormula].Duration > 5) {
										Slots[ActiveFormula].Duration--;
									}
								} else if (i == 42) {
									if (Slots[ActiveFormula].Count > 1) {
										Slots[ActiveFormula].Count--;
									}
								} else if (i == 43) {
									if (Slots[ActiveFormula].Spread > 1) {
										int Temp = (int) (Slots[ActiveFormula].Spread * 10);
										Temp -= 2;
										Slots[ActiveFormula].Spread = (double) Temp / 10;
									}
								} else if (i == 44) {
									if (Slots[ActiveFormula].Period > 1) {
										Slots[ActiveFormula].Period--;
									}
								} else if (i == 47) {
									if (Slots[ActiveFormula].NegateX) {
										FormulaPanel.get(i).Text = "NORM";
										Slots[ActiveFormula].NegateX = false;
									} else if (Slots[ActiveFormula].NegateY) {
										FormulaPanel.get(i).Text = "NO-X";
										Slots[ActiveFormula].NegateY = false;
										Slots[ActiveFormula].NegateX = true;
									} else {
										FormulaPanel.get(i).Text = "NO-Y";
										Slots[ActiveFormula].NegateY = true;
									}
								} else if (i == 48) {
									if (Slots[ActiveFormula].Stationary) {
										FormulaPanel.get(i).Text = "Moving";
										Slots[ActiveFormula].Stationary = false;
									} else {
										FormulaPanel.get(i).Text = "Stationary";
										Slots[ActiveFormula].Stationary = true;
									}
								}
								UpdateFormula();
							}
						}
						break;
					case 3:	//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades//Upgrades
						if (UpgradePanel.get(0).ContainsPoint(MousePos)) {
							if (Gold >= UpgradeCostWeapon) {
								Gold -= UpgradeCostWeapon;
								BaseDamage += 5;
								UpgradeCostWeapon *= 1.5;
								UpgradePanel.get(0).Text = "Upgrade Weapons | " + UpgradeCostWeapon;
								BaseCoolDown -= 25;
								UpdateFormula();
							} else {
								Alert("Not Enough Gold!");
							}
						}
						if (UpgradePanel.get(1).ContainsPoint(MousePos)) {
							if (Gold >= UpgradeCostTowers) {
								Gold -= UpgradeCostTowers;
								UpgradeCostTowers *= 1.5;
								UpgradePanel.get(1).Text = "Upgrade Towers | " + UpgradeCostTowers;
								Level++;
								for (int i = 0; i < Towers.length; i++) {
									for (int j = 0; j < Towers.length; j++) {
										if (Towers[i][j] != null) {
											Towers[i][j].LevelUp();
										}
									}
								}
							} else {
								Alert("Not Enough Gold!");
							}
						}
						break;
				}
			}
		} else if (State != -1) {
			State = -1;
			FirstWall = null;
			TowerSelected = -1;
		} else {
			ActivePanel = -1;
		}
	}

	public void UpdateFormula() {
		FormulaPanel.get(4).Text = Projectile.ToEnglish(ActivesFormula);
		FormulaPanel.get(30).Text = Slots[ActiveFormula].Speed + "";
		FormulaPanel.get(31).Text = Slots[ActiveFormula].Duration + "";
		FormulaPanel.get(32).Text = Slots[ActiveFormula].Count + "";
		FormulaPanel.get(33).Text = Slots[ActiveFormula].Spread + "";
		FormulaPanel.get(34).Text = Slots[ActiveFormula].Period + "";
		FormulaPanel.get(46).Text = CalculateDamage() + "";
		if (Slots[ActiveFormula].NegateX) {
			FormulaPanel.get(47).Text = "NO-X";
		} else if (Slots[ActiveFormula].NegateY) {
			FormulaPanel.get(47).Text = "NO-Y";
		} else {
			FormulaPanel.get(47).Text = "NORM";
		}
	}

	public int CalculateDamage() {
		int Damage = BaseDamage;
		Weapon slot = Slots[ActiveFormula];
		Damage -= (slot.Count - 1) / slot.Spread;
		if (slot.Stationary) {
			Damage -= 3;
			if (slot.Duration > 5) {
				Damage -= Math.ceil(((double) slot.Duration - 5d) / 3d);
			}
		}
		Damage -= 3 - slot.Period;
		if (slot.Speed > 20) {
			Damage -= Math.ceil(((double) slot.Speed - 20d) / 5d);
		}
		return Damage;
	}

	public void CalculatePaths() {
		for (int i = 0; i < Enemies.size(); i++) {
			Enemies.get(i).SetPath(GetPath(Enemies.get(i).GetBasePosition(), new Vector2(MapSize / WallSeperation / 2, MapSize / WallSeperation / 2)));
		}
	}

	public void CalculatePaths(int Start) {
		for (int i = 0; i < Enemies.size(); i++) {
			Enemies.get(i).SetPath(GetPath(Enemies.get(i).GetBasePosition(), new Vector2(MapSize / WallSeperation / 2, MapSize / WallSeperation / 2)));
		}
	}

	public ArrayList<Vector2> GetPath(Vector2 Start, Vector2 Finish) {
		ArrayList<Vector2> Path = new ArrayList<>();
		if (Start.equals(Finish)) {
			return Path;
		}
		int[][] Value = new int[Walls.length][Walls[0].length];
		boolean[][] Visited = new boolean[Walls.length][Walls[0].length];
		ConcurrentLinkedQueue<Vector2> q = new ConcurrentLinkedQueue<>();
		q.add(Start);
		boolean Found = false;
		while (!q.isEmpty()) {
			Vector2 Position = q.remove();
			if (Visited[(int) Position.GetX()][(int) Position.GetY()]) {
				continue;
			}
			Visited[(int) Position.GetX()][(int) Position.GetY()] = true;
			if (Position.equals(Finish)) {
				Found = true;
				break;
			}
			int CurrentVal = Value[(int) Position.GetX()][(int) Position.GetY()];
			if (Position.GetX() - 1 >= 0) {
				if (Walls[(int) Position.GetY()][(int) Position.GetX() - 1] == false && Value[(int) Position.GetX() - 1][(int) Position.GetY()] == 0) {
					q.add(new Vector2((int) Position.GetX() - 1, (int) Position.GetY()));
					Value[(int) Position.GetX() - 1][(int) Position.GetY()] = CurrentVal + 1;
				}
			}
			if (Position.GetX() + 1 < Value.length) {
				if (Walls[(int) Position.GetY()][(int) Position.GetX() + 1] == false && Value[(int) Position.GetX() + 1][(int) Position.GetY()] == 0) {
					q.add(new Vector2((int) Position.GetX() + 1, (int) Position.GetY()));
					Value[(int) Position.GetX() + 1][(int) Position.GetY()] = CurrentVal + 1;
				}
			}
			if (Position.GetY() + 1 < Value[0].length) {
				if (Walls[(int) Position.GetY() + 1][(int) Position.GetX()] == false && Value[(int) Position.GetX()][(int) Position.GetY() + 1] == 0) {
					q.add(new Vector2((int) Position.GetX(), (int) Position.GetY() + 1));
					Value[(int) Position.GetX()][(int) Position.GetY() + 1] = CurrentVal + 1;
				}
			}
			if (Position.GetY() - 1 >= 0) {
				if (Walls[(int) Position.GetY() - 1][(int) Position.GetX()] == false && Value[(int) Position.GetX()][(int) Position.GetY() - 1] == 0) {
					q.add(new Vector2((int) Position.GetX(), (int) Position.GetY() - 1));
					Value[(int) Position.GetX()][(int) Position.GetY() - 1] = CurrentVal + 1;
				}
			}
		}
		if (Found) {
			int Val = Value[(int) Finish.GetX()][(int) Finish.GetY()];
			Vector2 Pos = Finish;
			boolean Swap = false;
			while (Val != 0) {
				if (Swap) {
					if (Next(Value, new Vector2(Pos.GetX() - 1, Pos.GetY()), Val)) {
						Path.add(Pos);
						Pos = new Vector2(Pos.GetX() - 1, Pos.GetY());
					}
					if (Next(Value, new Vector2(Pos.GetX() + 1, Pos.GetY()), Val)) {
						Path.add(Pos);
						Pos = new Vector2(Pos.GetX() + 1, Pos.GetY());
					}
				}
				if (Next(Value, new Vector2(Pos.GetX(), Pos.GetY() - 1), Val)) {
					Path.add(Pos);
					Pos = new Vector2(Pos.GetX(), Pos.GetY() - 1);
				}
				if (Next(Value, new Vector2(Pos.GetX(), Pos.GetY() + 1), Val)) {
					Path.add(Pos);
					Pos = new Vector2(Pos.GetX(), Pos.GetY() + 1);
				}
				if (Next(Value, new Vector2(Pos.GetX() - 1, Pos.GetY()), Val)) {
					Path.add(Pos);
					Pos = new Vector2(Pos.GetX() - 1, Pos.GetY());
				}
				if (Next(Value, new Vector2(Pos.GetX() + 1, Pos.GetY()), Val)) {
					Path.add(Pos);
					Pos = new Vector2(Pos.GetX() + 1, Pos.GetY());
				}
				Swap = !Swap;
				Val--;
			}
		}
		Collections.reverse(Path);
		return Path;
	}

	private boolean Next(int[][] Values, Vector2 Position, int Val) {
		if ((int) Position.GetX() >= 0 && (int) Position.GetX() < Values.length && (int) Position.GetY() >= 0 && (int) Position.GetY() < Values[0].length) {
			if (Values[(int) Position.GetX()][(int) Position.GetY()] == Val - 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void Update() {
		Vector2 MousePos = new Vector2(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
		HandleKeys();
		if (!Paused) {
			Gold += GoldPerSecond / 60;
			PlayerPosition.Add(PlayerDelta);
			PlayerDelta.Scale(.965);
			for (int i = 0; i < PJCs.size(); i++) {
				if (PJCs.get(i).Projectiles.isEmpty()) {
					PJCs.remove(i);
					i--;
					continue;
				}
				PJCs.get(i).Update();
			}
			for (int i = 0; i < Cooldown.length; i++) {
				Cooldown[i] -= 16;
			}
			for (int i = 0; i < PJCs.size(); i++) {
				for (int j = 0; j < PJCs.get(i).Projectiles.size(); j++) {
					for (int k = 0; k < Enemies.size(); k++) {
						if (BubbleTouch(Enemies.get(k).GetPosition(), Enemies.get(k).Size, PJCs.get(i).Projectiles.get(j).GetProjPos(), PJCs.get(i).Projectiles.get(j).Size)) {
							Enemies.get(k).DealDamage(PJCs.get(i).Projectiles.get(j).Damage, false);
							PJCs.get(i).Projectiles.remove(j);
							j--;
							break;
						}
					}
				}
			}
			for (int i = 0; i < Towers.length; i++) {
				for (int j = 0; j < Towers[0].length; j++) {
					if (Towers[j][i] != null) {
						Towers[j][i].Update();
					}
				}
			}
			for (int i = 0; i < Enemies.size(); i++) {
				if (Enemies.get(i).Health <= 0) {
					if (Enemies.get(i) instanceof Basic) {
						Gold += Wave / 5 + 1;
					}
					if (Enemies.get(i) instanceof Tank) {
						Gold += Wave * 3 / 4 + 1;
					}
					if (Enemies.get(i) instanceof Swarmling) {
						Gold += Wave / 4 + 2;
					}
					if (Enemies.get(i) instanceof Priest) {
						Gold += Wave + 6;
					}
					if (Enemies.get(i) instanceof Supresser) {
						Gold += Wave * 20 + 1;
					}
					Enemies.remove(i);
					i--;
					continue;
				}
				if (Enemies.get(i).GetBasePosition().equals(new Vector2(MapSize / 2 / WallSeperation, MapSize / 2 / WallSeperation))) {
					Enemies.remove(i);
					BaseHealth--;
					i--;
					if (BaseHealth == 0) {
						EndGame();
					}
					continue;
				}
				Enemies.get(i).Update();
			}
			if (NextWaveTimer < 0) {
				NextWaveTimer = 30000 - Wave * 200;
				int Begin = Enemies.size();
				Enemies.addAll(WaveManager.GenerateWave(Wave++, MapSize / WallSeperation));
				CalculatePaths(Begin);
			}
			NextWaveTimer -= 16;
		}
		for (int i = 0; i < BasicButtons.size(); i++) {
			BasicButtons.get(i).Update(MousePos);
		}
		for (int i = 0; i < FormulaPanel.size(); i++) {
			FormulaPanel.get(i).Update(MousePos);
		}
		for (int i = 0; i < UpgradePanel.size(); i++) {
			UpgradePanel.get(i).Update(MousePos);
		}
		for (int i = 0; i < TowerPanel.size(); i++) {
			TowerPanel.get(i).Update(MousePos);
		}
		CreateWall.Update(MousePos);
		SwapBuild.Update(MousePos);

		if (AlertOpacity > 0) {
			AlertOpacity -= 1;
		}
	}

	public void EndGame() {
		Controller.Pop();
	}

	public boolean BubbleTouch(Vector2 Pos1, int Radius1, Vector2 Pos2, int Radius2) {
		int CombinedRadius = Radius1 + Radius2;
		if (Math.abs(Pos1.GetX() - Pos2.GetX()) > CombinedRadius) {
			return false;
		}
		if (Math.abs(Pos1.GetY() - Pos2.GetY()) > CombinedRadius) {
			return false;
		}
		double dist = Math.sqrt(Math.pow(Pos1.GetX() - Pos2.GetX(), 2) + Math.pow(Pos1.GetY() - Pos2.GetY(), 2));
		return dist < CombinedRadius;
	}

	public static boolean EnemyInRange(Vector2 Pos, double Range) {
		for (int i = 0; i < Enemies.size(); i++) {
			if (Math.abs(Enemies.get(i).GetPosition().GetX() - Pos.GetX()) < Range) {
				if (Math.abs(Enemies.get(i).GetPosition().GetY() - Pos.GetY()) < Range) {
					double Distance = Math.sqrt(Math.pow(Pos.GetX() - Enemies.get(i).GetPosition().GetX(), 2) + Math.pow(Pos.GetY() - Enemies.get(i).GetPosition().GetY(), 2));
					if (Distance < Range) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static ArrayList<Enemy> EnemiesInRange(Vector2 Pos, double Range) {
		ArrayList<Enemy> EnemiesInRange = new ArrayList<>();
		for (int i = 0; i < Enemies.size(); i++) {
			if (Math.abs(Enemies.get(i).GetPosition().GetX() - Pos.GetX()) < Range) {
				if (Math.abs(Enemies.get(i).GetPosition().GetY() - Pos.GetY()) < Range) {
					double Distance = Math.sqrt(Math.pow(Pos.GetX() - Enemies.get(i).GetPosition().GetX(), 2) + Math.pow(Pos.GetY() - Enemies.get(i).GetPosition().GetY(), 2));
					if (Distance < Range) {
						EnemiesInRange.add(Enemies.get(i));
					}
				}
			}
		}
		return EnemiesInRange;
	}
}
