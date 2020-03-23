package org.frdmrt.distance;

import java.util.concurrent.ThreadLocalRandom;

public class AbPqDistanceObject {
	GridPoint A;
	GridPoint B;
	GridPoint P;
	GridPoint Q;
	double gridDimension;
	double distance;
	
	public AbPqDistanceObject (double gridDimension) {
		this.gridDimension = gridDimension;
		this.A = this.generateRandomGridPoint();
		this.B = this.generateRandomGridPoint();
		this.P = this.generateRandomGridPoint();
		this.calculateQ();
		this.distance = hypoteneuse(this.P, this.Q);
	}
	
	public GridPoint generateRandomGridPoint() {
		double X = ThreadLocalRandom.current().nextDouble(this.gridDimension * 2) - this.gridDimension;
		double Y = ThreadLocalRandom.current().nextDouble(this.gridDimension * 2) - this.gridDimension;
		return (new GridPoint(X,Y));
	}
	
	public void calculateQ() {
//      Return the point Q on line segment AB nearest to point P:
//	    Given the line A + t(B-A),
//	    find the point Q on the line
//	    where PQ is perpendicular to AB
//	    by setting the dot product of PQ and AB to zero
//	    and solving for t.
//	    If Q lands outside the line segment, return A or B.
		double x_leg = this.B.X - this.A.X;
		double y_leg = this.B.Y - this.A.Y;
		double len_squared = (x_leg * x_leg) + (y_leg * y_leg);
		double dot_product = ((this.P.X - this.A.X) * x_leg) + ((this.P.Y - this.A.Y) * y_leg);
		double t = dot_product / len_squared;
		if (t<0){
			this.Q = A;
		} else if (t>1) {
			this.Q = B;
		} else {
			this.Q = new GridPoint(this.A.X + (t * x_leg), this.A.Y + (t * y_leg));
		}
		return;
	}

	double hypoteneuse(GridPoint P, GridPoint Q) {
		double x_leg = Q.X - P.X;
		double y_leg = Q.Y - P.Y;
		return Math.sqrt((x_leg * x_leg) + (y_leg * y_leg));
	}
	public GridPoint getA() {
		return A;
	}

	public GridPoint getB() {
		return B;
	}

	public GridPoint getP() {
		return P;
	}

	public GridPoint getQ() {
		return Q;
	}

	public double getGridDimension() {
		return gridDimension;
	}

	public double getDistance() {
		return distance;
	}

	public String toString() {
		return("A: " + this.A + ", B: " + this.B + ", P: " + this.P + ", Q: " + this.Q + ", distance: " + this.distance);
	}
}
