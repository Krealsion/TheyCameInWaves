package wavesofterror.Enemies;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import wavesofterror.Enemy;
import wavesofterror.PlayState;
import wavesofterror.Vector2;
import wavesofterror.jMath;

public class Priest extends Enemy {

	public static final double HealthMod = 1.1;

	public Priest(Vector2 Position, double BaseHealth) {
		super(Position, 21, 25, BaseHealth * HealthMod);
	}

	@Override
	public void Update() {
		if (!MoveDestination.isEmpty()) {
			if (MovePerc < Speed) {
				MovePerc += 1 - Slow;
			} else {
				MovePerc = 0;
				Position = MoveDestination.get(0);
				MoveDestination.remove(0);
			}
		}
		ArrayList<Enemy> e = PlayState.EnemiesInRange(Position, 400);
		for (Enemy en : e){
			en.DealDamage(-.5, false);
		}
		SlowDuration -= 16;
		if (SlowDuration <= 0){
			Slow = 0;
		}
	}

	@Override
	public void Draw(Graphics g) {
		Vector2 Offset = jMath.GetOffset();
		g.setColor(Color.CYAN);
		Vector2 EffectivePos = GetPosition();
		g.fillOval((int) EffectivePos.GetX() - (int) Offset.GetX() - Size / 2, (int) EffectivePos.GetY() - (int) Offset.GetY() - Size / 2, Size, Size);
	}

	@Override
	public void DealDamage(double Num, boolean Fire) {
		Health -= Num;
	}
}
