package wavesofterror.Towers;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import wavesofterror.*;

public class FireTower extends Tower {

	private int Opacity = 0;
	public static final int BaseCost = 40;

	public int Range;

	public double Damage = 3;

	public int BaseCooldown = 1400;

	public int Cooldown;

	public FireTower(Vector2 Position) {
		super(Position);
		Range = 100;
	}

	@Override
	public void Update() {
		if (Cooldown <= 0) {
			if (PlayState.EnemyInRange(Position, Range)) {
				ArrayList<Enemy> e = PlayState.EnemiesInRange(Position, Range);
				for (int i = 0; i < e.size(); i++) {
					e.get(i).DealDamage(Damage, true);
				}
				Cooldown = BaseCooldown;
				Opacity = 150;
			}
		}
		if (Opacity >= 10){
			Opacity -= 10;
		}else{
			Opacity = 0;
		}
		Cooldown -= 16;
	}

	@Override
	public void LevelUp() {
		Range *= 1.1;
		BaseCooldown -= 50;
		if (BaseCooldown < 300) {
			BaseCooldown = 300;
		}
		Damage += 2;
		Damage *= 1.2;
	}

	@Override
	public void Draw(Graphics g) {
		Vector2 Offset = jMath.GetOffset();
		g.setColor(new Color(255, 0, 0, Opacity));
		g.fillOval((int) (Position.GetX() - Range - Offset.GetX()), (int) (Position.GetY() - Range - Offset.GetY()), Range * 2, Range * 2);
		g.setColor(new Color(63, 0, 0));
		g.fillOval((int) (Position.GetX() - Offset.GetX() - (4 * Level + 20) / 2), (int) (Position.GetY() - Offset.GetY() - (4 * Level + 20) / 2), 4 * Level + 20, 4 * Level + 20);
	}
}
