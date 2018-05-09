package wavesofterror;

import java.awt.Graphics;
import java.util.ArrayList;

abstract public class Enemy {

	protected Vector2 Position;
	protected double Speed;			//This Represents how many frames a single move takes 60 is one second
	protected int Size;
	protected double MaxHealth;
	protected double Health;
	protected ArrayList<Vector2> MoveDestination;
	public double Slow;
	public double SlowDuration;
	protected double MovePerc;

	public Enemy(Vector2 Position, double Speed, int Size, double Health) {
		this.Position = Position;
		this.Speed = Speed;
		this.Size = Size;
		MaxHealth = Health;
		this.Health = Health;
		MoveDestination = new ArrayList<>();
	}

	public void SetPosition(Vector2 Position) {
		this.Position = Position;
	}

	public void SetSpeed(double Speed) {
		this.Speed = Speed;
	}

	public void SetSize(int Size) {
		this.Size = Size;
	}

	public void SetHealth(double Health) {
		this.Health = Health;
	}

	public Vector2 GetPosition() {
		if (MoveDestination.isEmpty()) {
			return new Vector2(Position.GetX() * PlayState.WallSeperation, Position.GetY() * PlayState.WallSeperation);
		}
		double x = Position.GetX() + (MoveDestination.get(0).GetX() - Position.GetX()) * (double) MovePerc / Speed;
		double y = Position.GetY() + (MoveDestination.get(0).GetY() - Position.GetY()) * (double) MovePerc / Speed;
		Vector2 EffectivePos = new Vector2(x * PlayState.WallSeperation, y * PlayState.WallSeperation);
		return EffectivePos;
	}

	public Vector2 GetBasePosition() {
		return Position;
	}

	public double GetSpeed() {
		return Speed;
	}

	public int GetSize() {
		return Size;
	}

	public double GetHealth() {
		return Health;
	}

	public void SetPath(ArrayList<Vector2> Path) {
		MoveDestination.clear();
		MoveDestination.addAll(Path);
	}

	abstract public void Update();
	abstract public void DealDamage(double Num, boolean Fire);
	abstract public void Draw(Graphics g);
}
