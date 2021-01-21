package ppPackage;

import acm.gui.IntField;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

import static ppPackage.ppSimParams.*;

import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

/*
 * 
 * This program simulates a pong game by sending a ball in the direction of a
 * user controlled paddle. The paddle moves up and down according to the height
 * of the users mouse. Hitting the ball with the paddle will add some energy to
 * it and allow it to extend the length of the game. The ball then goes back towards
 * the computer controlled paddle that moves according to the height of myBall and returns it.
 * The speed of the ball is modified using JSlider tickSlider. A scoreboard is also
 * implemented and modified as the game goes on.
 * 
 * You cannot win on default settings. The user must increase the lag to 
 * have a chance of beating the computer. 
 * 
 * Template provided by Prof. Ferrie in ECSE-202 Assignement 4
 * Corrections copied from ECSE-202 Assignement 3 Solution
 * 
 * @author sabaf
 *
 */

public class ppSimPaddleAgent extends GraphicsProgram {

	ppPaddle myPaddle; // Paddle instance
	ppTable myTable; // Table instance
	ppBall myBall; // Ball instance
	static ppPaddleAgent theAgent; // Agent instance
	public JSlider tickSlider; // Tickslider instance
	public static JSlider lagSlider; // LagSlider instance
	public JToggleButton trace; // Trace instance
	public static IntField theAgentPoints; // Agent points
	public static IntField humanPoints; // Human points
	public static JTextField humanName; // Name of human
	public static JTextField agentName; // Name of human
	RandomGenerator ranGen; // Instance of rangen
	Color color; // Color of paddle
	boolean traceOn; // Tracing point
	double iYinit; // Initial y position
	double iLoss; // Loss parameter
	double iVel; // Initial velocity
	double iTheta; // Initial Angle
	double Xinit; // Initial x position
	boolean hasEnergy; // Determines if ball is moving

	public static void main(String[] args) {
		new ppSimPaddleAgent().start(args);  // Standalone applet
	}

	public void init() {
		// Appropriate window size
		this.resize(scrWIDTH + OFFSET, scrHEIGHT + OFFSET);

		// Create buttons, sliders, and scoreboard
		lagSlider = new JSlider(1, 20, 1); // lag used for speed of theAgent
		tickSlider = new JSlider(-30, -5, -20); // tick used to +/- ball speed
		trace = new JToggleButton("Trace"); // JToggleButton to determine whether or not to trace path of ball
		theAgentPoints = new IntField(0);  // Computer scoreboard points
		humanPoints = new IntField(0);    // Human scoreboard points
		humanName = new JTextField("Human", 10); // Human name
		agentName = new JTextField("Agent", 10); // Human name

		// Add components accordingly
		add(agentName, NORTH);
		add(theAgentPoints, NORTH);
		add(humanName, NORTH);
		add(humanPoints, NORTH);
		add(new JButton("Clear"), SOUTH); // Clear screen
		add(new JButton("New Serve"), SOUTH);
		add(trace, SOUTH); // Trace JToggleButton
		add(new JButton("Quit"), SOUTH); // Button quits program
		add(new JLabel("Decrease Speed"), SOUTH);
		add(tickSlider, SOUTH);
		add(new JLabel("Increase Speed"), SOUTH);
		add(new JLabel("-lag"), SOUTH);
		add(lagSlider, SOUTH);
		add(new JLabel("+lag"), SOUTH);

		// Generate Random Number
		ranGen = RandomGenerator.getInstance();
		ranGen.setSeed(RSEED);

		// Setup dissplay 
		myTable = new ppTable(this);
		
		// Create new ball, user paddle and agent paddle using methods 
		myBall = newBall(ranGen);
		myPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit, ColorPaddle, myTable);
		theAgent = new ppPaddleAgent(ppAgentXinit, ppAgentYinit, ColorAgent, myTable);

		// Export to setters to allow ppBall access to paddle objects	
		myBall.setPaddle(myPaddle);
		myBall.setAgent(theAgent);
		
		// Sets the value of the myBall instance variable in ppPaddleAgent.
		theAgent.attachBall(myBall);

		add(myBall.getBall()); // Each thread must be explicitly started
		myBall.start();
		myPaddle.start();
		theAgent.start();

		// Add listeners
		addMouseListeners();
		addActionListeners();
	}

	// Returns the value on the tick slider
	// Used to determine ball speed
	public double getTickValue() {
		return (double) tickSlider.getValue();
	}
	
	// Returns the value on the tick slider
	// Used to determine theAgent speed
	public double getLagValue() {
		return (double) lagSlider.getValue();
	}

	// Mouse Handler - a moved event moves the paddle up and down in Y
	public void mouseMoved(MouseEvent e) {
		myPaddle.setY(myTable.ScrtoY((double) e.getY()));
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		// This command clears screen and creates new instances of theAgent, myPaddle, and myBall
		if (command.equals("Clear")) {
			System.out.println("New screen Sire. \n");

			// Kill previous ball
			myBall.hasEnergy = false;
			
			// Buffer to allow user to ready up
			pause(500);
			
			// Reset points
			clearPoints();
			
			myTable.newScreen();
			myBall = newBall(ranGen);
			myPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit, ColorPaddle, myTable);
			theAgent = new ppPaddleAgent(ppAgentXinit, ppAgentYinit, ColorAgent, myTable);

			myBall.setPaddle(myPaddle);
			myBall.setAgent(theAgent);
			theAgent.attachBall(myBall);
			
			myBall.start(); // Each thread must be explicitly started again
			myPaddle.start();
			theAgent.start();
			
		} else if (command.equals("New Serve")) {
			if (myBall.ballInPlay(myBall.hasEnergy)) {
				System.out.println("There is already a ball in play. \n");
			} else {
				System.out.println("Coming up my Lord. \n");
				
				// Buffer to allow user to ready up
				pause(500);
				
				myTable.newScreen();
				myBall = newBall(ranGen);
				myPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit, ColorPaddle, myTable);
				theAgent = new ppPaddleAgent(ppAgentXinit, ppAgentYinit, ColorAgent, myTable);

				myBall.setPaddle(myPaddle);
				myBall.setAgent(theAgent);
				theAgent.attachBall(myBall);
				
				myBall.start(); // Each thread must be explicitly started again
				myPaddle.start();
				theAgent.start();
			}
			
		} else if (command.equals("Quit")) { // Exits program
			System.exit(0);
		}
	}

	// Creates newBall and uses random generator ranGen to create different values 
	public ppBall newBall(RandomGenerator r) {
		// generates parameters and creates an instance of ppBall.

		iYinit = r.nextDouble(YinitMIN, YinitMAX); // Initial height
		iLoss = r.nextDouble(EMIN, EMAX); // Loss parameter
		iVel = r.nextDouble(VoMIN, VoMAX); // Initial velocity
		iTheta = r.nextDouble(ThetaMIN, ThetaMAX); // Initial Angle
		Xinit = XINIT; // Initial x position
		color = ColorBall;

		myBall = new ppBall(Xinit, iYinit, iVel, iTheta, color, iLoss, myTable, myPaddle, trace.isSelected(), Math.abs(getTickValue()));
		return myBall;
	}
	
	// Method to increment Human points
	public static void incrementHumanPoints() {
		ppSimPaddleAgent.humanPoints.setValue(ppSimPaddleAgent.humanPoints.getValue() + 1);
	}

	// Method to increment Agent points
	public static void incrementAgentPoints() {
		ppSimPaddleAgent.theAgentPoints.setValue(ppSimPaddleAgent.theAgentPoints.getValue() + 1);
	}
	
	// Called when user clears the display
	// Resets the points to 0
	public static void clearPoints() {
		theAgentPoints.setValue(0);
		humanPoints.setValue(0);
	}
}
