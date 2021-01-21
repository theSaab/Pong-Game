
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;

import javax.swing.*;

import acm.graphics.*;
import acm.gui.*;

 /*
  * This class simulates the movement of the ball on the screen
  * through the area created. It has potential contact with both 
  * the user paddle myPaddle and the computer paddle theAgent.
  * When the ball is missed by either the user or the computer, 
  * points are given accordingly in the respective IntField.
  * 
  * Template provided by Prof. Ferrie in ECSE-202 Assignement 4
  * Corrections copied from ECSE-202 Assignement 3 Solution
  * 
  * @author sabaf
  *
  */


public class ppBall extends Thread {

	/**
	 * Get the current line number.
	 * @return int - Current line number.
	 */
	public static int getLineNumber() {
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}

	private double Xinit; // Initial position of ball in X
	private double Yinit; // Initial position of ball in Y
	private double Vo; // Initial velocity
	private double theta; // Initial angle
	private double loss; // Loss parameter
	private ppTable myTable; // Instance of ping-pong myTable
	private ppPaddle myPaddle; // Instance of paddle myPaddle
	private double tickValue; // Tick value representing ball speed
	boolean traceOn; // Boolean used to determine tracing 
	ppPaddleAgent theAgent; // Instance of paddle theAgent
	JSlider lagSlider;
	Color color; // Color of ball
	GOval myBall; // Graphics object representing ball
	double lag = 0;
	// Assigning a value to boolean hasEnergy
	boolean hasEnergy = true;	

	// Initialize instance variables and create an instance of the ppBall
	public ppBall(double Xinit, double Yinit, double Vo, double theta, Color color, double loss, ppTable myTable,
			ppPaddle myPaddle, boolean traceOn, double tickValue) {

		this.Xinit = Xinit;			// Copy constructor parameters to instance variables
		this.Yinit = Yinit;
		this.Vo = Vo;
		this.theta = theta;
		this.loss = loss;
		this.myTable = myTable;
		this.myPaddle = myPaddle;
		this.color = color;
		this.traceOn = traceOn;
		this.tickValue = tickValue;

		double ScrX = myTable.toScrX(Xinit - ballRadius); // Cartesian to screen
		double ScrY = myTable.toScrY(Yinit - ballRadius);

		
		// Create ball
		myBall = new GOval(ScrX, ScrY, 2 * ballRadius * SCALE, 2 * ballRadius * SCALE);
		myBall.setColor(color);
		myBall.setFilled(true);
		myTable.getDisplay().add(myBall);
		setAgent(myTable.theAgent);
	}

	public void run() {
		// time (reset at each interval)
		double time = 0;
		// Terminal velocity
		double Vt = ballMass * gravity / (4.0 * Pi * ballRadius * ballRadius * k);
		// Kinetic energy in X and Y directions
		double KEx = ETHR, KEy = ETHR;
		// X initial position and velocity variables
		double Xo, X, Vx;
		// Y initial position and velocity variables
		double Yo, Y, Vy;
		// Potential Energy
		double PE;

		// Setting up velocities in x and y
		// Velocity in x
		double Vox = Vo * Math.cos(theta * Pi / 180);
		// Velocity in y
		double Voy = Vo * Math.sin(theta * Pi / 180);
		// Initial position in X and Y
		Xo = Xinit;
		Yo = Yinit;

		double ScrX, ScrY;

		// Assigning a value to boolean hasEnergy
		hasEnergy = true;

		// Simulation will run as long as ball has energy
		while (hasEnergy) {

			// Update parameters
			X = (Vox * Vt / gravity) * (1.0 - Math.exp(-gravity * time / Vt));
			Y = (Vt / gravity) * (Voy + Vt) * (1.0 - Math.exp(-gravity * time / Vt)) - Vt * time;
			Vx = Vox * Math.exp(-gravity * time / Vt);
			Vy = (Voy + Vt) * Math.exp(-gravity * time / Vt) - Vt;

			// Collision with floor
			if (Vy < 0 && (Yo + Y) <= ballRadius) {

				// Kinetic energy in X and Y direction after collision
				KEx = 0.5 * ballMass * Vx * Vx * (1 - loss);
				KEy = 0.5 * ballMass * Vy * Vy * (1 - loss);

				PE = 0; // Potential energy (at ground)

				Vox = Math.sqrt(2 * KEx / ballMass); // Resulting horizontal velocity
				Voy = Math.sqrt(2 * KEy / ballMass); // Resulting vertical velocity

				if (Vx < 0)
					Vox = -Vox; // Preserve sign of Vox

				time = 0; // Reset current interval time
				Xo += X; // Update X and Y offsets
				Yo = ballRadius;
				X = 0; // Reset X and Y for next iteration
				Y = 0;

				// Terminate if insufficient energy
				if ((KEx + KEy + PE) < ETHR)
					hasEnergy = false;
			}

			// Paddle Collision
			// Paddle myPaddle is controlled by user and moves only in the Y axis 
			// according to user mouse pointer height. 
			if ((Vx > 0) && ((Xo + X) > myPaddle.getX() - ballRadius - ppPaddleW / 2)) {

				// Determine if contact with user paddle
				if (myPaddle.contact(Xo + X + ballRadius, Yo + Y)) {

					// Kinetic energy calculations
					KEx = 0.5 * Vx * Vx * (1.0 - loss) * ballMass;
					KEy = 0.5 * Vy * Vy * (1.0 - loss) * ballMass;

					// Potential energy calculations
					PE = ballMass * gravity * Y;

					// Velocity calculations
					Vox = -Math.sqrt(2 * KEx / ballMass);
					Voy = Math.sqrt(2 * KEy / ballMass);
					Vox = Math.min(Vox * ppPaddleXgain, 7); // Scale X component of velocity
					Voy = Math.max(Voy * ppPaddleYgain * myPaddle.getSgnVy(), -7); // Scale Y + same dir. as paddle

					time = 0; // time is reset at every collision
					Xo = myPaddle.getX() - ballRadius - ppPaddleW / 2; // need to accumulate distance between collisions
					Yo += Y; // the absolute position of the ball on the ground
					X = 0; // (X,Y) is the instantaneous position along an arc,
					Y = 0; // Absolute position is (Xo+X,Yo+Y).
				} else {
					
					// If user is unable to hit ball due to height, out of bounds 
					// point awarded to player
					if ((Y + Yo) > scrHEIGHT / SCALE) {
						
						// Increment humanPoints on scoreboard
						ppSimPaddleAgent.incrementHumanPoints();
						
						System.out.println("Too high for you? Point given to " + ppSimPaddleAgent.humanName.getText() + " \n");
						// End program
						hasEnergy = false;
					}
					else {
					// Increment theAgentPoints on scoreboard
					ppSimPaddleAgent.incrementAgentPoints();
					
					// If user misses, mockery ensues
					System.out.println("You missed. ");
					System.out.println("Perhaps reducing the speed would increase your chances of winning.");
					System.out.println("May I propose increasing the lag to something of your caliber? \n");
					// End program
					hasEnergy = false;
					}
				}
			}

			// Agent Collision
			// Agent paddle moves according to the height of 
			// myBall and should not miss unless lag is involved
			if ((Vx < 0) && ((Xo + X) <= XLWALL + ballRadius)) {
				
				
				// If ball is out of bounds, award points to theAgent
				if ((Y + Yo) > scrHEIGHT / SCALE) {
					
					// Increment theAgentPoints on scoreboard
					ppSimPaddleAgent.incrementAgentPoints();
					
					System.out.println("Out of bounds, point given to " + ppSimPaddleAgent.agentName.getText() + " \n");
					// End program
					hasEnergy = false;
				}
				
				// Determine if contact with Agent paddle
				else if (ppSimPaddleAgent.theAgent.contact(Xo + X, Yo + Y)) {
	
					// Kinetic energy calculations
					KEx = 0.5 * Vx * Vx * (1.0 - loss) * ballMass; // Scale X component of kinetic energy
					KEy = 0.5 * Vy * Vy * (1.0 - loss) * ballMass; // Scale Y component of kinetic energy
	
					// Potential energy calculations
					PE = ballMass * gravity * Y;
	
					// Velocity calculations
					Vox = Math.sqrt(2 * KEx / ballMass);
					Voy = Math.sqrt(2 * KEy / ballMass);
					Vox = Math.min(Vox * ppAgentXgain, 7); // Scale X component of velocity
					Voy = Math.min(Voy * ppAgentYgain, 7); // Scale Y + same dir. as paddle
	
					if (Vy < 0)
						Voy = -Voy;
	
					time = 0; // time is reset at every collision
					Xo = XINIT + ballRadius; // need to accumulate distance between collisions
					Yo += Y; // the absolute position of the ball on the ground
					X = 0; // (X,Y) is the instantaneous position along an arc,
					Y = 0; // Absolute position is (Xo+X,Yo+Y).
				}
				else {
					
					System.out.println("Not bad, point given to " + ppSimPaddleAgent.humanName.getText() + " \n");

					// Increment humanPoints on scoreboard
					ppSimPaddleAgent.incrementHumanPoints();
					// End program
					hasEnergy = false;
				}
			}
			
			// Debug statement
			if (DEBUG)
				System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f\n", time, Xo + X, Yo + Y, Vx, Vy);

			// Calculate position of ball on screen
			ScrX = myTable.toScrX(Xo + X - ballRadius); // Convert to screen units
			ScrY = myTable.toScrY(Yo + Y + ballRadius);

			myBall.setLocation(ScrX, ScrY); // Change position of ball in display

			if (lag % Math.floor(ppSimPaddleAgent.lagSlider.getValue()) == 0) {
				theAgent.setY(((Y + Yo) + ppPaddleH / 8));
				lag += 1;
			} else {
				lag += 1;
			}
			
			// Tracing statement
			if (traceOn) {
				myTable.trace(Xo + X, Yo + Y, Color.BLACK); // Place a marker on the current position
			}

			// Delay and update clocks
			myTable.getDisplay().pause(tickValue); // Convert time
			time += TICK;
		}
	}

	
	public GObject getBall() {
		return myBall;
	}

	// A predicate that is true if a ppBall simulation is running
	public boolean ballInPlay(boolean hasEnergy) {
		return (hasEnergy);
	}

	// Sets the value of the reference to the Player paddle
	public void setPaddle(ppPaddle myPaddle) {
		this.myPaddle = myPaddle;
	}

	// Sets the value of the references to the Agent paddle
	public void setAgent(ppPaddleAgent theAgent) {
		this.theAgent = theAgent;
	}

}
