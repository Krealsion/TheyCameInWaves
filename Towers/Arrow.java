package wavesofterror.Towers;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import wavesofterror.*;

public class Arrow extends Tower {

	public int Range;
	public double Damage = 2;

	public int BaseCooldown = 600;

	public int Cooldown;

	int Fired;
	Vector2 PosFiredAt;

	public Arrow(Vector2 Position) {
		super(Position);
		Range = 400;
	}

	@Override
	public void Update() {
		if (Cooldown <= 0) {
			if (PlayState.EnemyInRange(Position, Range)) {
				ArrayList<Enemy> e = PlayState.EnemiesInRange(Position, Range);
				int MinDistIndex = 0;
				double MinDist = Math.sqrt(Math.pow(Position.GetX() - e.get(0).GetPosition().GetX(), 2) + Math.pow(Position.GetY() - e.get(0).GetPosition().GetY(), 2));
				for (int i = 1; i < e.size(); i++) {
					if (Math.sqrt(Math.pow(Position.GetX() - e.get(i).GetPosition().GetX(), 2) + Math.pow(Position.GetY() - e.get(i).GetPosition().GetY(), 2)) < MinDist) {
						MinDistIndex = i;
					}
				}
				PosFiredAt = e.get(MinDistIndex).GetPosition();
				e.get(MinDistIndex).DealDamage(Damage, false);
				Fired = 4;
				Cooldown = BaseCooldown;
			}
		}
		Cooldown -= 16;
	}

	@Override
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
		g.setColor(Color.BLACK);
		if (Fired > 0) {
			g.drawLine((int) (Position.GetX() - Offset.GetX() - (4 * Level + 20) / 2), (int) (Position.GetY() - Offset.GetY() - (4 * Level + 20) / 2),
					(int) (PosFiredAt.GetX() - Offset.GetX()), (int) (PosFiredAt.GetY() - Offset.GetY()));
			Fired--;
		}
		g.setColor(new Color(73, 105, 47));
		g.fillOval((int) (Position.GetX() - Offset.GetX() - (4 * Level + 20) / 2), (int) (Position.GetY() - Offset.GetY() - (4 * Level + 20) / 2), 4 * Level + 20, 4 * Level + 20);
	}
}
