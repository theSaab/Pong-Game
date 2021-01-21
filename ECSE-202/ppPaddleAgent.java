package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.*;

import acm.graphics.*;

/*
 * This class extends ppPaddle class and creates theAgent rectangle.
 * Also exports attachBall to set value of myBall instance.
 * 
 * Template provided by Prof. Ferrie in ECSE-202 Assignement 4
 * 
 * @author sabaf
 *
 */

public class ppPaddleAgent extends ppPaddle{
	
	double X; // Paddle X location
	double Y; // Paddle Y location
	double Vx; // Paddle velocity in X
	double Vy; // Paddle velocity in Y
	double lastX; // X position on previous cycle
	double lastY; // Y position on previous cycle

	static GRect theAgent;
	ppBall myBall;
	ppTable myTable;
	
	public ppPaddleAgent(double X, double Y, Color myColor, ppTable myTable) {
		super(X, Y, myColor, myTable);
		this.X = X;
		this.Y = Y;
		this.myTable = myTable;
		this.myColor = myColor;
		lastX = X;
		lastY = Y;
		
		theAgent = myPaddle;
		myTable.getDisplay().add(theAgent);
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
	
	// Sets the value of the myBall instance variable in ppPaddleAgent.	
	public void attachBall(ppBall myBall) {
		this.myBall = myBall;
	}
}
