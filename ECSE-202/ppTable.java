
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;

import acm.graphics.*;

/*
 * This class has been modified to only create the floor of the display
 * and provides utility methods to convert world to display coordinates and vice-versa.
 * 
 * Template provided by Prof. Ferrie in ECSE-202 Assignement 4
 * Corrections copied from ECSE-202 Assignement 3 Solution
 * 
 * @author sabaf
 *
 */

public class ppTable {

	// Instance variables 
	ppSimPaddleAgent dispRef;
	ppBall myBall;
	ppPaddleAgent theAgent;
	ppPaddle myPaddle;
	ppTable myTable;

	public ppTable(ppSimPaddleAgent dispRef) {

		this.dispRef = dispRef;
		// Create floor plane
		GLine floor = new GLine(0, scrHEIGHT, scrWIDTH + OFFSET, scrHEIGHT);
		floor.setColor(Color.BLACK);
		dispRef.add(floor);
	}

	// Returns screen coordinates of X
	double toScrX(double X) {
		return X * SCALE;
	}

	// Returns screen coordinates of Y
	double toScrY(double Y) {
		return scrHEIGHT - Y * SCALE;
	}

	// Returns pixel coordinates of X
	public double ScrtoX(double ScrX) {
		return ScrX / SCALE;
	}
	
	// Returns pixel coordinates of X
	public double ScrtoY(double ScrY) {
		return (scrHEIGHT - ScrY) / SCALE;
	}

	// Reference to display
	public ppSimPaddleAgent getDisplay() {
		return dispRef;
	}
	
	// Method used when traceOn is true to plot trace points on the screen
	void trace(double x, double y, Color color) {
		double ScrX = toScrX(x);
		double ScrY = toScrY(y);
		GOval pt = new GOval(ScrX, ScrY, PD, PD);
		pt.setColor(color);
		pt.setFilled(true);
		dispRef.add(pt);
	}
	
	// Method called when Clear Screen button is presed
	// Wipes screen and draws new ground plane
	void newScreen() {
		dispRef.removeAll();
		myTable = new ppTable(dispRef);
	}
}
