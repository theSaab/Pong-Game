
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.*;

import acm.graphics.*;


/*
 * This class creates both the user rectangle myPaddle and 
 * theAgent rectangle used as paddles for the game.
 * They only move in the Y axis and have fixed dimensions.
 * 
 * Template provided by Prof. Ferrie in ECSE-202 Assignement 4
 * Corrections copied from ECSE-202 Assignement 3 Solution
 * 
 * @author sabaf
 *
 */

public class ppPaddle extends Thread {

	double X; // Paddle X location
	double Y; // Paddle Y location
	double Vx; // Paddle velocity in X
	double Vy; // Paddle velocity in Y
	double lastX; // X position on previous cycle
	double lastY; // Y position on previous cycle
	ppTable myTable; // Instance of pp table.
	GRect myPaddle; // GRect implements paddle
	Color myColor; // Color of paddle

	public ppPaddle(double X, double Y, Color myColor, ppTable myTable) {
		this.X = X;
		this.Y = Y;
		this.myTable = myTable;
		this.myColor = myColor;
		lastX = X;
		lastY = Y;

		// Screen display for paddle
		double ScrX = myTable.toScrX(X - ppPaddleW / 2);
		double ScrY = myTable.toScrY(Y + ppPaddleH / 2);
		myPaddle = new GRect(ScrX, ScrY, ppPaddleW * SCALE, ppPaddleH * SCALE);
		myPaddle.setColor(myColor);
		myPaddle.setFilled(true);
		myTable.getDisplay().add(myPaddle);
	}

	
	public void run() {
		while (true) {
			Vx = (X - lastX) / TICK;
			Vy = (Y - lastY) / TICK;
			lastX = X;
			lastY = Y;
			myTable.getDisplay().pause(TICK * TIMESCALE); // Time to mS
		}
	}

	// Sets the Y position of the paddle
	public void setY(double Y) {
		this.Y = Y;
		double ScrX = myTable.toScrX(this.X - ppPaddleW / 2);
		double ScrY = myTable.toScrY(this.Y + ppPaddleH / 2);
		myPaddle.setLocation(ScrX, ScrY); // Update display
	}

	// Sets the position of the paddle in X axis 
	public void setX(double X) {
		this.X = X;
		double ScrX = myTable.toScrX(this.X - ppPaddleW / 2);
		double ScrY = myTable.toScrY(this.Y + ppPaddleH / 2);
		myPaddle.setLocation(ScrX, ScrY); // Update display
	}

	// Returns true if the ball at position (Sx, Sy) is in contact with the paddle.
	// Sx is X position of contact surface
	// Sy is Y position of contact surface
	public boolean contact(double Sx, double Sy) {
	// if the ball is within the upper and lower bound of myPaddle it return true 
		return Sy >= (Y - ppPaddleH / 2) && Sy <= (Y + ppPaddleH / 2);
	}
	
	// Returns velocity in X direction of paddle
	public double getVx() {
		return Vx;
	}
	// Returns velocity in Y direction of paddle
	public double getVy() {
		return Vy;
	}

	// Returns the top left corner X position of the paddle.
	public double getX() {
		return X;
	}

	// Returns the top left corner Y position of the paddle.
	public double getY() {
		return Y;
	}

	// Return the sign of the Y velocity of the paddle
	public double getSgnVy() {
		if (Vy < 0) {
			return -1.0;
		} else {
			return 1.0;
		}
	}

}
