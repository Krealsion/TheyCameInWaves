package wavesofterror;

import java.awt.Color;
import java.awt.Graphics;

public class Projectile {

	double Duration;

	int Size;
	Vector2 Position;
	Vector2 ProjPos;
	double Speed;
	double Angle;
	String Formula;
	double Period;
	double Damage;

	boolean NegateX;
	boolean NegateY;

	boolean Stationary;

	double Time;

	public Projectile(int Size, Vector2 Position, double Speed, double Damage, double Angle, String Formula, double Period, boolean Stationary, double Duration, boolean NegateX, boolean NegateY) {
		this.Size = Size;
		this.Position = Position;
		this.Speed = Speed;
		this.Angle = Angle;
		this.Formula = Formula;
		this.Period = Period;
		this.Stationary = Stationary;
		this.NegateX = NegateX;
		this.NegateY = NegateY;
		this.Duration = Duration;
		this.Damage = Damage;
		ProjPos = new Vector2();
	}

	public void Update() {
		if (!Stationary) {
			Position.AddX(Speed * Math.cos(Angle));
			Position.AddY(Speed * Math.sin(Angle));
		}
		double r = ParseFormula(Formula);
		ProjPos.SetX(Position.GetX());
		ProjPos.SetY(Position.GetY());
		Vector2 PolarOut = jMath.EvaluatePolar(Time / 500d * Math.PI / Period, r * 100);
		if (NegateX) {
			PolarOut.SetX(0);
		}
		if (NegateY) {
			PolarOut.SetY(0);
		}
		if (!Stationary) {
			PolarOut = jMath.RotatePoint(PolarOut, -Angle);
		}
		ProjPos.AddX(PolarOut.GetX());
		ProjPos.AddY(PolarOut.GetY());
		Time += 16;
		Duration -= 16;
	}

	public int GetSize() {
		return Size;
	}

	public Vector2 GetProjPos() {
		return ProjPos;
	}

	public double ParseFormula(String s) {
		double r = 1;
		for (int i = 0; i < s.length(); i++) {
			char t = s.charAt(i);
			if (Character.isDigit(t)) {
				if (i + 1 != s.length()) {
					char Next = s.charAt(i + 1);
					if (Next == '.') {
						Next = s.charAt(i + 2);
						if (Character.isDigit(Next)) {
							r *= Character.getNumericValue(t) + (double) Character.getNumericValue(Next) / 10;
							i += 2;
						}
					} else {
						r *= (int) Character.getNumericValue(t);
					}
				} else {
					r *= (int) Character.getNumericValue(t);
				}
			} else if (Character.isLetter(t)) {
				if (t == 's') {
					r *= Math.sin(ParseFormula(s.substring(i + 1)));
					for (int k = i; k < s.length(); k++) {
						i++;
						if (s.charAt(k) == ')') {
							break;
						}
					}
				} else if (t == 'c') {
					r *= Math.cos(ParseFormula(s.substring(i + 1)));
					for (int k = i; k < s.length(); k++) {
						i++;
						if (s.charAt(k) == ')') {
							break;
						}
					}
				} else if (t == 't') {
					r *= Math.tan(ParseFormula(s.substring(i + 1)));
					for (int k = i; k < s.length(); k++) {
						i++;
						if (s.charAt(k) == ')') {
							break;
						}
					}
				} else if (t == 'x') {
					r *= Time / 500d * Math.PI / Period;		//Seconds in * 2* PI/Period
				}
			} else if (t == ')') {
				return r;
			} else if (t == '.') {
				char Next = s.charAt(i + 1);
				if (Character.isDigit(Next)) {
					r *= Character.getNumericValue(Next) / 10;
					i++;
				}
			} else if (t == '-') {
				r *= -1;
			}
		}
		return r;
	}

	public static String ToEnglish(String Formula) {
		String s = "";
		for (int i = 0; i < Formula.length(); i++) {
			char t = Formula.charAt(i);
			if (Character.isDigit(t)) {
				if (i + 1 != Formula.length()) {
					char Next = Formula.charAt(i + 1);
					if (Next == '.') {
						Next = Formula.charAt(i + 2);
						if (Character.isDigit(Next)) {
							s += t + '.' + Next + " * ";
							i += 2;
						}
					} else {
						s += t + " * ";
					}
				} else {
					s += t + " * ";
				}
			} else if (Character.isLetter(t)) {
				if (t == 's') {
					s += "Sin(";
				} else if (t == 'c') {
					s += "Cos(";
				} else if (t == 't') {
					s += "Tan(";
				} else if (t == 'x') {
					s += t;
				}
			} else {
				s += t;
			}
		}
		return s;
	}

	public void Draw(Graphics g) {
		Vector2 Offset = jMath.GetOffset();
		int Size = this.Size;
		Color c = Color.BLUE;
		for (; Size > 2; Size -= 2) {
			g.setColor(c);
			g.fillOval((int) (ProjPos.GetX() - Size / 2 - Offset.GetX()), (int) (ProjPos.GetY() - Size / 2 - Offset.GetY()), Size, Size);
			if (c == Color.BLUE) {
				c = Color.BLACK;
			} else {
				c = Color.BLUE;
			}
		}
	}
}
