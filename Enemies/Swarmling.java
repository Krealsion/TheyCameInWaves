package wavesofterror.Enemies;

import java.awt.Color;
import java.awt.Graphics;
import wavesofterror.Enemy;
import wavesofterror.Vector2;
import wavesofterror.jMath;

public class Swarmling extends Enemy {

	public static final double HealthMod = .7;

	public Swarmling(Vector2 Position, double BaseHealth) {
		super(Position, 8, 10, BaseHealth * HealthMod);
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
		SlowDuration -= 16;
		if (SlowDuration <= 0){
			Slow = 0;
		}
	}

	@Override
	public void Draw(Graphics g) {
		Vector2 Offset = jMath.GetOffset();
		g.setColor(Color.RED);
		Vector2 EffectivePos = GetPosition();
		g.fillOval((int) EffectivePos.GetX() - (int) Offset.GetX() - Size / 2, (int) EffectivePos.GetY() - (int) Offset.GetY() - Size / 2, Size, Size);
	}

	@Override
	public void DealDamage(double Num, boolean Fire) {
		if (Fire){
			Num *=2;
		}
		Health -= Num;
	}
}
