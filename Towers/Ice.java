package wavesofterror.Towers;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import wavesofterror.*;

public class Ice extends Tower {

	public int Range;
	public double Damage = 1;

	public int BaseCooldown = 500;

	public int Cooldown;
	double Slow = .5;
	double SlowLength = 3000;

	int Fired;
	Vector2 PosFiredAt;

	public Ice(Vector2 Position) {
		super(Position);
		Range = 500;
	}

	@Override
	public void Update() {
		if (Cooldown <= 0) {
			if (PlayState.EnemyInRange(Position, Range)) {
				ArrayList<Enemy> e = PlayState.EnemiesInRange(Position, Range);
				int Index = (int)(Math.random() * e.size());
				PosFiredAt = e.get(Index).GetPosition();
				e.get(Index).Slow = Slow;
				e.get(Index).SlowDuration = SlowLength;
				Fired = 4;
				Cooldown = BaseCooldown;
			}
		}
		Cooldown -= 16;
	}

	public void LevelUp() {
		Range *= 1.1;
		BaseCooldown -= 30;
		if (BaseCooldown < 30) {
			BaseCooldown = 30;
		}
		Damage += 2;
		Damage *= 1.2;
	}

	@Override
	public void Draw(Graphics g) {
		Vector2 Offset = jMath.GetOffset();
		g.setColor(Color.BLUE);
		if (Fired > 0) {
			g.drawLine((int) (Position.GetX() - Offset.GetX() - (4 * Level + 20) / 2), (int) (Position.GetY() - Offset.GetY() - (4 * Level + 20) / 2),
					(int) (PosFiredAt.GetX() - Offset.GetX()), (int) (PosFiredAt.GetY() - Offset.GetY()));
			Fired--;
		}
		g.setColor(new Color(12, 32, 220));
		g.fillOval((int) (Position.GetX() - Offset.GetX() - (4 * Level + 20) / 2), (int) (Position.GetY() - Offset.GetY() - (4 * Level + 20) / 2), 4 * Level + 20, 4 * Level + 20);
	}
}
