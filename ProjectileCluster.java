package wavesofterror;

import java.awt.Graphics;
import java.util.ArrayList;

public class ProjectileCluster {

	ArrayList<Projectile> Projectiles;

	double AngleOfMovement;

	public ProjectileCluster() {
		Projectiles = new ArrayList<>();
	}

	public void Update() {
		for (int i = 0; i < Projectiles.size(); i++) {
			Vector2 ProjPos = Projectiles.get(i).GetProjPos();
			if (ProjPos.GetX() < 0 || ProjPos.GetX() > PlayState.MapSize || ProjPos.GetY() < 0 || ProjPos.GetY() > PlayState.MapSize) {
				Projectiles.remove(i);
				i--;
				continue;
			}
			if (Projectiles.get(i).Duration < 0 && Projectiles.get(i).Stationary) {
				Projectiles.remove(i);
				i--;
				continue;
			}
			Projectiles.get(i).Update();
		}
	}

	public void Draw(Graphics g) {
		for (int i = 0; i < Projectiles.size(); i++) {
			Projectiles.get(i).Draw(g);
		}
	}
}
