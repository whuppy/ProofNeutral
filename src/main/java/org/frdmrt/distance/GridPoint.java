package org.frdmrt.distance;

public class GridPoint {
	double X;
	double Y;
	
	public GridPoint(double X, double Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public double getX() {
		return X;
	}
	public void setX(double x) {
		X = x;
	}
	public double getY() {
		return Y;
	}
	public void setY(double y) {
		Y = y;
	}
	public String toString(){
		return(String.format("(%f, %f)", this.X, this.Y));
	}

}
