package wavesofterror;

import java.util.ArrayList;
import wavesofterror.Enemies.*;

public class WaveManager {

	public static ArrayList<Enemy> GenerateWave(int Wave, int MapSize) {
		int NumberOfEnemies = (int) (20 * Math.pow(1.3, Wave));
		ArrayList<Enemy> Enemies = new ArrayList<>();
		Enemies.ensureCapacity(NumberOfEnemies);
		double[] Ratio = new double[5];		//Basic, Swarmling, Tank, Priest, Growth, Supresser
		if (Wave < 5) {
			Ratio[0] = 1;
		} else if (Wave < 10) {
			Ratio[0] = Math.random() * .6 + .4;
			Ratio[1] = 1 - Ratio[0];
		} else if (Wave < 15) {
			Ratio[0] = Math.random() * .6 + .2;
			Ratio[1] = Math.random() * (1 - Ratio[0]);
			Ratio[2] = 1 - (Ratio[0] + Ratio[1]);
		} else if (Wave < 20) {
			Ratio[0] = Math.random() * .5;
			Ratio[1] = Math.random() * (1 - Ratio[0]);
			Ratio[2] = 1 - (Ratio[0] + Ratio[1]);
			Ratio[3] = 4;
		}else if (Wave < 30) {
			Ratio[0] = Math.random() * .3;
			Ratio[1] = Math.random() * (.7 - Ratio[0]);
			Ratio[2] = 1 - (Ratio[0] + Ratio[1]);
			Ratio[3] = Wave / 4;
		}else if (Wave < 40) {
			Ratio[0] = Math.random() * .2;
			Ratio[1] = Math.random() * (.5 - Ratio[0]);
			Ratio[2] = 1 - (Ratio[0] + Ratio[1]);
			Ratio[3] = Wave / 4;
			Ratio[4] = 1;
		}
		for (int i = 0; i < NumberOfEnemies; i++) {
			int Where = (int) (Math.random() * 4);
			int Position = (int) (Math.random() * (double) MapSize);
			Vector2 Location;
			if (Where == 1) {	// Top
				Location = new Vector2(0, Position);
			} else if (Where == 2) {	//Right
				Location = new Vector2(Position, MapSize - 1);
			} else if (Where == 3) {	//Bot
				Location = new Vector2(MapSize - 1, Position);
			} else {	//Left
				Location = new Vector2(Position, 0);
			}
			double Selection = Math.random();
			if (Ratio[4] > 0){
				Enemies.add(new Supresser(Location, Math.pow(1.2, Wave)));
				Ratio[4]--;
			}else if (Ratio[3] > 0){
				Enemies.add(new Priest(Location, Math.pow(1.2, Wave)));
				Ratio[3]--;
			}else if (Selection < Ratio[0]) {
				Enemies.add(new Basic(Location, Math.pow(1.2, Wave)));
			} else if (Selection < Ratio[0] + Ratio[1]) {
				Enemies.add(new Swarmling(Location, Math.pow(1.2, Wave)));
			} else if (Selection < Ratio[0] + Ratio[1] + Ratio[2]) {
				Enemies.add(new Tank(Location, Math.pow(1.2, Wave)));
			}
		}
		return Enemies;
	}
}
