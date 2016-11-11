import rxtxrobot.*;
import java.io.*;
import java.util.*;

class Navigation implements Runnable {
	private Thread t;
	private String threadName;

	private double X_NOW,Y_NOW,THETA_NOW;
	private double X_PREV1,Y_PREV1,THETA_PREV1;
	private double X_PREV2,Y_PREV2,THETA_PREV2;

	Navigation(String name) {
		threadName = name;
	}

	public void run() {
		String line;
		String[] thetaxy;
		double theta,x,y;

		Kalman X = new Kalman();
		Kalman Y = new Kalman();
		Kalman T = new Kalman();

		double Q = 0.0001;
		double R = 0.001043;

		Scanner scan = new Scanner(System.in);

		while (true) {
			line = scan.nextLine();
			thetaxy = line.split(" ");

			theta = Double.parseDouble(thetaxy[0]);
			x = Double.parseDouble(thetaxy[1]);
			y = Double.parseDouble(thetaxy[2]);

			//X_NOW += x;
			//Y_NOW += y;
			//THETA_NOW += theta;
			X_PREV2 = X_PREV1;
			X_PREV1 = X_NOW;
			Y_PREV2 = Y_PREV1;
                        Y_PREV1 = Y_NOW;
			THETA_PREV2 = THETA_PREV1;
                        THETA_PREV1 = THETA_NOW;

			X_NOW = X.tick(x,(X_NOW-X_PREV1) + ((X_NOW-X_PREV1)-(X_PREV1-X_PREV2))/2.0,Q,R);
			Y_NOW = Y.tick(y,(Y_NOW-Y_PREV1) + ((Y_NOW-Y_PREV1)-(Y_PREV1-Y_PREV2))/2.0,Q,R);
			THETA_NOW = T.tick(theta,(THETA_NOW-THETA_PREV1) + ((THETA_NOW-THETA_PREV1)-(THETA_PREV1-THETA_PREV2))/2.0,Q,R);
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void go(double x, double y, double th) {
		go(x,y,th,200);
	}

	public void go(double x, double y, double th, int power) {
		if (y > Y_NOW) {
			while (Y_NOW < y) {
				System.out.println(Y_NOW);
				main.theRobot.runMotor(RXTXRobot.MOTOR1,-power,RXTXRobot.MOTOR2,-power,0);
			}
		}
		else if (y < Y_NOW) {
			while (Y_NOW > y)
				main.theRobot.runMotor(RXTXRobot.MOTOR1,power,RXTXRobot.MOTOR2,power,0);
		}
		else if (th > THETA_NOW) {
			while (THETA_NOW < th)
				main.theRobot.runMotor(RXTXRobot.MOTOR1,-power,RXTXRobot.MOTOR2,power,0);
		}
		else if (th < THETA_NOW) {
			while (THETA_NOW > th)
				main.theRobot.runMotor(RXTXRobot.MOTOR1,power,RXTXRobot.MOTOR2,-power,0);
		}
		/*while (true) {
			System.out.println(X_NOW+" "+Y_NOW+" "+THETA_NOW);
		}*/
	}
}


public class main {
	public static RXTXRobot theRobot;

	private static int type;

	private static double X_NOW,Y_NOW,THETA_NOW;

	public static void main(String args[]) {
		//Initialize robot
		theRobot = robotFunctions.init();

		//Initialize navigation thread
		Navigation position = new Navigation("Position");
		position.start();
		theRobot.sleep(5000);

		//Actually do stuff
		position.go(0,36,0);
		//theRobot.sleep(3000);
		//position.go(0,0,90);

		//Close connection
		theRobot.close();
	}
}
