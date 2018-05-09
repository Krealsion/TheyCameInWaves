package wavesofterror;

public class Weapon {

	String Formula;

	int Count;
	double Spread;
	boolean Stationary;
	double Speed;
	int Size;
	double Period;
	double Duration;
	boolean NegateX;
	boolean NegateY;
	double Damage;

	boolean Invert;
	int Current;

	public Weapon(String Formula, int Count, double Damage, double Spread, boolean Stationary, double Speed, int Size, double Period, double Duration, boolean NegateX, boolean NegateY) {
		this.Formula = Formula;
		this.Count = Count;
		this.Spread = Spread;
		this.Stationary = Stationary;
		this.Speed = Speed;
		this.Size = Size;
		this.Period = Period;
		this.NegateX = NegateX;
		this.NegateY = NegateY;
		this.Duration = Duration;
		this.Damage = Damage;
	}

	public ProjectileCluster Shoot(Vector2 Center, Vector2 Target) {
		ProjectileCluster Clust = new ProjectileCluster();
		double Angle = jMath.getRadiansPointsTo(Center, Target);
		double SpreadMod = 1;
		for (int i = 0; i < Count; i++) {
			String tempForm = Formula;
			tempForm = SpreadMod + tempForm;
			if (Invert) {
				tempForm = "-" + tempForm;
			}
			Projectile p = new Projectile(Size, new Vector2(Center.GetX(), Center.GetY()), Speed, Damage, -Angle, tempForm, Period, Stationary, Duration * 1000, NegateX, NegateY);
			if (Stationary) {
				p.Time = (double) Current * Period / Count * 1000;
			}
			Clust.Projectiles.add(p);

			if (!Stationary) {
				if (i % 2 == 1) {
					SpreadMod *= Spread;
				}
				Invert = !Invert;
			} else {
				Current++;
				Current %= Count;
			}
		}
		return Clust;
	}
}
