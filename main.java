import rxtxrobot.*;
import java.io.*;
import java.util.*;

class Navigation implements Runnable {
	private Thread t;
	private String threadName;

	private double Y_NOW,THETA_NOW,THETA2_NOW;
	private double Y_SENT,THETA_SENT,THETA2_SENT;
	private double Y_PREV,THETA_PREV,THETA2_PREV;

	Kalman Y = new Kalman();
	Kalman T = new Kalman();
    Kalman T2 = new Kalman();

	private double Q = 1000000;
	private double R = 1000000;

	Navigation(String name) {
		threadName = name;
	}

	public void run() {
		String line;
		String[] thetaxy;
		double theta,x,y,theta2;

		Scanner scan = new Scanner(System.in);

		while (true) {
			line = scan.nextLine();
			thetaxy = line.split(" ");

			theta = Double.parseDouble(thetaxy[0]);
			x = Double.parseDouble(thetaxy[1]);
			y = Double.parseDouble(thetaxy[2]);
			theta2 = Double.parseDouble(thetaxy[3]);

			if (Math.pow(y,2) < 400)
				Y_SENT += y;
			
			if ((Math.pow(theta,2) < 200)&&(Math.pow(theta2,2) < 200)) {
				THETA_SENT += theta;
				THETA2_SENT += theta2;
			}
			else {
				theta = 0.0;
				theta2 = 0.0;
			}
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void go(double y) {
		go(y,90);
	}

	public void go(double y, int power) {
		Y_NOW = 0;
		Y_SENT = 0;
		Y.reset();

		if (y < Y_NOW) {
			while (Y_NOW > y) {
				Y_PREV = Y_NOW;
				Y_NOW = Y.tick(Y_SENT-Y_PREV,Q,R);
				//Y_NOW += Y_SENT-Y_PREV;
				System.out.println(Y_SENT);
				System.out.println(Y_NOW);
				main.theRobot.runMotor(RXTXRobot.MOTOR1,power,RXTXRobot.MOTOR2,power,0);
			}
			System.out.println(Y_SENT);
			System.out.println(Y_NOW);
		}
		else if (y > Y_NOW) {
			while (Y_NOW < y) {
				Y_PREV = Y_NOW;
				Y_NOW = Y.tick(Y_SENT-Y_PREV,Q,R);
				//Y_NOW += Y_SENT-Y_PREV;
				System.out.println(Y_SENT);
				System.out.println(Y_NOW);
				main.theRobot.runMotor(RXTXRobot.MOTOR1,-power,RXTXRobot.MOTOR2,-power,0);
			}
			System.out.println(Y_SENT);
			System.out.println(Y_NOW);
		}

		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public void turn(double t) {
		THETA_NOW = 0;
		THETA2_NOW = 0;
		T.reset();
		T2.reset();

		if (t > 0) {
			while (THETA2_NOW > -t) {
				THETA_PREV = THETA_NOW;
				THETA2_PREV = THETA2_NOW;

				THETA_NOW = T.tick(THETA_SENT-THETA_PREV,Q,R);
				THETA2_NOW = T2.tick(THETA2_SENT-THETA2_PREV,Q,R);
				//THETA_NOW += THETA_SENT-THETA_PREV;
				//THETA2_NOW += THETA2_SENT-THETA2_PREV;
				//System.out.println((THETA_SENT-THETA2_SENT));
				System.out.println((THETA_NOW-THETA2_NOW));
                main.theRobot.runMotor(RXTXRobot.MOTOR1,180,RXTXRobot.MOTOR2,-180,0);
			}
			System.out.println((THETA_NOW-THETA2_NOW));
		}
        else if (t < 0) {
			while (THETA_NOW+THETA2_NOW < -t) {
				THETA_PREV = THETA_NOW;
				THETA2_PREV = THETA2_NOW;

				THETA_NOW = T.tick(THETA_SENT-THETA_PREV,Q,R);
				THETA2_NOW = T2.tick(THETA2_SENT-THETA2_PREV,Q,R);
				//THETA_NOW += THETA_SENT-THETA_PREV;
				//THETA2_NOW += THETA2_SENT-THETA2_PREV;
				//System.out.println((THETA_SENT+THETA2_SENT));
				System.out.println((THETA_NOW+THETA2_NOW));
                main.theRobot.runMotor(RXTXRobot.MOTOR1,-180,RXTXRobot.MOTOR2,115,0);
			}
			System.out.println((THETA_NOW+THETA2_NOW));
        }
        main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}
}


public class main {
	public static RXTXRobot theRobot;

	private static int side = 1;

	private static double X_NOW,Y_NOW,THETA_NOW;

	private static Navigation position;

	public static void main(String args[]) {
		//Initialize robot
		theRobot = robotFunctions.init();

		//Initialize navigation thread
		position = new Navigation("Position");
		position.start();

		//Actually do stuff
		//position.go(24);
		//position.turn(65);

		//Move out of the box
		moveOutOfBox();

		//Turn 90 degrees left or right
		firstTurn();

		position.go(52);
		main.theRobot.runMotor(RXTXRobot.MOTOR1,-180,RXTXRobot.MOTOR2,-180,3);

		turnOnPlatform();

		//Raise the boom, do some stuff, lower it
		boomStuff();

		//Move off of the platform
		getOffThePlatform();
		secondTurn();
		getTheBall();

		//Close connection
		//System.out.println("BREAK");
		theRobot.close();
	}

	public static void moveOutOfBox() {
		double dist = robotFunctions.Sonar(theRobot);
		//System.out.println(dist);
		while (dist > 40) {
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-90,RXTXRobot.MOTOR2,-90,0);
			dist = robotFunctions.Sonar(theRobot);
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public static void firstTurn() {
		if (side == 0) {
			position.turn(90);
		}
		else if (side == 1) {
			position.turn(-90);
		}
		/*int init = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		int pos = init;
		System.out.println(pos);
		while (pos-init > -175) {
			theRobot.runMotor(RXTXRobot.MOTOR1,-180,RXTXRobot.MOTOR2,115,0);
			pos = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
			}*/
	}

	public static void turnOnPlatform() {
		if (side == 0)
			position.turn(-90);
		else if (side == 1)
			position.turn(90);
	}

	public static void boomStuff() {
		//Raise the boom
		robotFunctions.raiseBoom(theRobot);
		//Take the temperature and wind speed
		robotFunctions.TempTester(theRobot);
		//Lower the boom
		robotFunctions.lowerBoom(theRobot);
	}

	public static void getOffThePlatform() {
		double dist = robotFunctions.Sonar(theRobot);
		//System.out.println(dist);
		while (dist > 30) {
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-100,RXTXRobot.MOTOR2,-100,0);
			dist = robotFunctions.Sonar(theRobot);
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public static void turnOnPlatform() {
		if (side == 0)
			position.turn(-90);
		else if (side == 1)
			position.turn(90);
	}

	public static void getTheBall() {
		double dist = robotFunctions.Sonar(theRobot);
		//System.out.println(dist);
		while (dist > 20) {
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-100,RXTXRobot.MOTOR2,-100,0);
			dist = robotFunctions.Sonar(theRobot);
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}
}
