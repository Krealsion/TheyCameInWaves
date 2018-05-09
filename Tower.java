package wavesofterror;

import java.awt.Graphics;

public abstract class Tower {

	protected Vector2 Position;
	protected int Level;

	public Tower(Vector2 Position) {
		this.Position = Position;
		Level = 1;
	}

	public Vector2 GetPosition() {
		return Position;
	}

	public int GetLevel() {
		return Level;
	}

	public void SetPosition(Vector2 Position) {
		this.Position = Position;
	}

	public void SetLevel(int Level) {
		this.Level = Level;
	}

	abstract public void Update();

	abstract public void LevelUp();

	abstract public void Draw(Graphics g);
}
