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
		turn(t,180,-180,-180,115);
	}

	public void turn(double t, int rr, int rl, int lr, int ll) {
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
                main.theRobot.runMotor(RXTXRobot.MOTOR1,rr,RXTXRobot.MOTOR2,rl,0);
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
                main.theRobot.runMotor(RXTXRobot.MOTOR1,lr,RXTXRobot.MOTOR2,ll,0);
			}
			System.out.println((THETA_NOW+THETA2_NOW));
        }
        main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public void reliableCCTurn(double y) {
		//main.theRobot.runMotor(RXTXRobot.MOTOR1,-180,RXTXRobot.MOTOR2,115,2250);
		turn(-90);
		main.theRobot.sleep(500);

		double baseline = robotFunctions.Sonar2(main.theRobot);
		main.theRobot.sleep(500);
		double newBaseline = baseline=1;

		int leftPower = -100;
		int rightPower = -100;

		Y_NOW = 0;
		Y_SENT = 0;
		Y.reset();

		while (Y_NOW < y) {
			newBaseline = robotFunctions.Sonar2(main.theRobot);
			//for when it drifts to the left
			while (baseline > newBaseline) {
				Y_PREV = Y_NOW;
				Y_NOW = Y.tick(Y_SENT-Y_PREV,Q,R);
				System.out.println(Y_NOW);
				newBaseline = robotFunctions.Sonar2(main.theRobot);
				//need to power the left motor now since it is drifting to the left
				main.theRobot.runMotor(RXTXRobot.MOTOR1, leftPower = leftPower - 150, RXTXRobot.MOTOR2, -100, 0);
				leftPower = -100;
				if (Y_NOW > y)
					break;
			}
			//for when it drifts to the right
			while (baseline < newBaseline) {
				Y_PREV = Y_NOW;
			Y_NOW = Y.tick(Y_SENT-Y_PREV,Q,R);
			System.out.println(Y_NOW);
				newBaseline = robotFunctions.Sonar2(main.theRobot);
				//need to power the left motor now since it is drifting to the left
				main.theRobot.runMotor(RXTXRobot.MOTOR1, leftPower, RXTXRobot.MOTOR2, rightPower = rightPower - 150, 0);
				rightPower = -100;
				if (Y_NOW > y)
					break;
			}
			leftPower = -100;
			rightPower = -100;
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public void reliableCWTurn(double y) {
		//main.theRobot.runMotor(RXTXRobot.MOTOR1,170,RXTXRobot.MOTOR2,-200,6500);
		turn(90);
		main.theRobot.sleep(500);

		double baseline = robotFunctions.Sonar2(main.theRobot);
		main.theRobot.sleep(500);
		double newBaseline = baseline=1;

		int leftPower = -100;
		int rightPower = -100;

		Y_NOW = 0;
		Y_SENT = 0;
		Y.reset();

		while (Y_NOW < y) {
			newBaseline = robotFunctions.Sonar2(main.theRobot);
			//for when it drifts to the left
			while (baseline > newBaseline) {
				Y_PREV = Y_NOW;
				Y_NOW = Y.tick(Y_SENT-Y_PREV,Q,R);
				System.out.println(Y_NOW);
				newBaseline = robotFunctions.Sonar2(main.theRobot);
				//need to power the left motor now since it is drifting to the left
				main.theRobot.runMotor(RXTXRobot.MOTOR1, leftPower = leftPower - 250, RXTXRobot.MOTOR2, -100, 0);
				leftPower = -100;
				if (Y_NOW > y)
					break;
			}
			//for when it drifts to the right
			while (baseline < newBaseline) {
				Y_PREV = Y_NOW;
				Y_NOW = Y.tick(Y_SENT-Y_PREV,Q,R);
				System.out.println(Y_NOW);
				newBaseline = robotFunctions.Sonar2(main.theRobot);
				//need to power the left motor now since it is drifting to the left
				main.theRobot.runMotor(RXTXRobot.MOTOR1, leftPower, RXTXRobot.MOTOR2, rightPower = rightPower - 250, 0);
				rightPower = -100;
				if (Y_NOW > y)
					break;
			}
			leftPower = -100;
			rightPower = -100;
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

		//Move out of the box
		theRobot.runMotor(RXTXRobot.MOTOR1,-180,RXTXRobot.MOTOR2,-180,1000);
		theRobot.sleep(1000);
		moveOutOfBox();

		//Turn 90 degrees left or right and move to the ramp
		firstTurn();
		theRobot.sleep(2000);

		//Go up the ramp
		rampStuff();
		//Do boom stuff
		boomStuff();

		//Turn
		turnOnPlatform();

		//Move off of the platform
		getOffThePlatform();

		//Get the ball
		secondTurn();
		getTheBall();

		//Back up
		position.go(-20);
		//Turn
		secondTurnAlt();
		position.go(60);

		//Approach the wall
		getOffThePlatform();

		//Turn
		thirdTurn();
		getOffThePlatform();
		thirdTurn();
		//Move over the bridge
		goOverTheBridge();
		//Turn
		thirdTurn();
		//Go to the sandbox
		getOffThePlatform();

		robotFunctions.moveArm(theRobot);
		double perc = robotFunctions.Conduct(theRobot);
		if (perc > 80)
		robotFunctions.dropBall(theRobot);

		//Close connection
		theRobot.close();
	}


	public static void moveOutOfBox() {
		double frontActual = robotFunctions.Sonar(theRobot);
		while (frontActual > 40) {
			frontActual = robotFunctions.Sonar(theRobot);
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-100,RXTXRobot.MOTOR2,-100,0);
		}
		theRobot.sleep(1000);
	}

	public static void firstTurn() {
		if (side == 0) {
			position.reliableCWTurn(22);
		}
		else if (side == 1) {
			position.reliableCCTurn(22);
		}
	}

	public static void rampStuff() {
		int initial = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		int pos = initial;
		double dist = robotFunctions.Sonar(theRobot);
		while (Math.pow(pos-initial,2) < Math.pow(500,2)) {
			dist = robotFunctions.Sonar(theRobot);
			if (dist > 30)
				theRobot.runMotor(RXTXRobot.MOTOR1,-400,RXTXRobot.MOTOR2,-400,0);
			pos = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		}
		theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
		theRobot.sleep(2000);
	}

	public static void turnOnPlatform() {
		if (side == 0)
			position.turn(-30,0,0,0,0);
		else if (side == 1)
			position.turn(30,0,0,0,0);
	}

	public static void boomStuff() {
		//Raise the boom
		robotFunctions.raiseBoom(theRobot);
		theRobot.sleep(1000);
		//Take the temperature and wind speed
		robotFunctions.TempTester(theRobot);
		theRobot.sleep(2000);
		//Lower the boom
		robotFunctions.lowerBoom(theRobot);
		theRobot.sleep(1000);
	}

	public static void getOffThePlatform() {
		double dist = robotFunctions.Sonar(theRobot);

		while (dist > 40) {
			dist = robotFunctions.Sonar(theRobot);
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-100,RXTXRobot.MOTOR2,-100,0);
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public static void secondTurn() {
		if (side == 0) {
			//position.turn(-90);
			position.reliableCCTurn(20);
		}
		else if (side == 1) {
			position.reliableCWTurn(20);
		}
	}

	public static void secondTurnAlt() {
		if (side == 0) {
			position.turn(-90);
		}
		else if (side == 1) {
			position.turn(90);
		}
	}

	public static void getTheBall() {
		double dist = robotFunctions.Sonar(theRobot);
		while (dist > 20) {
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-100,RXTXRobot.MOTOR2,-100,0);
			dist = robotFunctions.Sonar(theRobot);
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public static void thirdTurn() {
		if (side == 0) {
			position.reliableCWTurn(20);
		}
		else if (side == 1) {
			position.reliableCCTurn(20);
		}
	}

	public static void goOverTheBridge() {
		double dist = robotFunctions.Sonar(theRobot);

		while (dist > 40) {
			dist = robotFunctions.Sonar(theRobot);
			main.theRobot.runMotor(RXTXRobot.MOTOR1,-300,RXTXRobot.MOTOR2,-300,0);
		}
		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}
}
