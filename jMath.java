package wavesofterror;

public class jMath {

	public static Vector2 EvaluatePolar(double Angle, double Magnitude) {
		double sin = Math.sin(Angle);
		double cos = Math.cos(Angle);
		return new Vector2(sin * Magnitude, cos * Magnitude);
	}

	public static Vector2 RotatePoint(Vector2 Point, double Angle) {
		double s = Math.sin(Angle);
		double c = Math.cos(Angle);
		return new Vector2(Point.GetX() * c + Point.GetY() * s, Point.GetY() * c + Point.GetX() * s);
	}

	public static Vector2 GetOffset() {
//		return new Vector2(Math.max(PlayState.MapSize - Renderer.WindowWidth, Math.min(0, PlayState.PlayerPosition.GetX() - Renderer.WindowWidth / 2)), 
//				Math.max(PlayState.MapSize - Renderer.WindowHight, Math.min(0, PlayState.PlayerPosition.GetY() - Renderer.WindowHight / 2)));
		return new Vector2(PlayState.PlayerPosition.GetX() - Renderer.WindowWidth / 2,
				PlayState.PlayerPosition.GetY() - Renderer.WindowHeight / 2);
	}

	public static double getRadiansPointsTo(Vector2 From, Vector2 To) {
		double DifX = To.GetX() - From.GetX();
		double DifY = To.GetY() - From.GetY();
		double Angle;
		if (DifY != 0) {
			Angle = Math.atan2(-DifY, DifX);
		} else {
			Angle = Math.PI;
		}
		if (Angle < 0) {
			Angle += Math.PI * 2;
		}
		return Angle;
	}
}
