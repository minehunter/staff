package io.vedder.robotics.particlefilter;

import java.util.Optional;

public class Vector2d {

  private final double x;
  private final double y;

  public Vector2d(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  @Override
  public String toString() {
    return ("(" + x + "," + y + ")");
  }

  public Vector2d minus(final Vector2d other) {
    return new Vector2d(this.getX() - other.getX(), this.getY() - other.getY());
  }

  public Vector2d plus(final Vector2d other) {
    return new Vector2d(this.getX() + other.getX(), this.getY() + other.getY());
  }

  public double norm() {
    return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
  }

  public Vector2d normalized() {
    return new Vector2d(this.getX() / this.norm(), this.getY() / this.norm());
  }

  public double dot(final Vector2d other) {
    return this.getX() * other.getX() + this.getY() * other.getY();
  }

  public Vector2d times(final double scale) {
    return new Vector2d(this.getX() * scale, this.getY() * scale);
  }

  // I hope this works...
  public static boolean isIntersecting(final Vector2d p1Start, final Vector2d p1End,
      final Vector2d p2Start, final Vector2d p2End) {
    final Vector2d A = p1Start;
    final Vector2d B = p1End;
    final Vector2d C = p2Start;
    final Vector2d D = p2End;

    final Vector2d P = B.minus(A);
    final Vector2d Q = D.minus(C);

    final Vector2d R = (C.minus(A)).minus(P.normalized().times(C.minus(A).dot(P.normalized())));
    final Vector2d S = (D.minus(A)).minus(P.normalized().times(D.minus(A).dot(P.normalized())));

    final Vector2d T = (A.minus(D)).minus(Q.normalized().times(A.minus(D).dot(Q.normalized())));
    final Vector2d U = (B.minus(D)).minus(Q.normalized().times(B.minus(D).dot(Q.normalized())));

    return (R.dot(S) <= 0 && T.dot(U) <= 0);
  }

  public static Optional<Vector2d> getIntersection(final Vector2d p1Start, final Vector2d p1End,
      final Vector2d p2Start, final Vector2d p2End) {
    if (!isIntersecting(p1Start, p1End, p2Start, p2End)) {
      return Optional.empty();
    } else {
      final double x1 = p1Start.getX();
      final double y1 = p1Start.getY();
      final double x2 = p1End.getX();
      final double y2 = p1End.getY();
      final double x3 = p2Start.getX();
      final double y3 = p2Start.getY();
      final double x4 = p2End.getX();
      final double y4 = p2End.getY();
      final double px = ((x1 * y1 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
          / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
      final double py = ((x1 * y1 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
          / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
      return Optional.of(new Vector2d(px, py));
    }
  }
}
