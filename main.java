import rxtxrobot.*;
import java.io.*;
import java.util.*;

class Navigation implements Runnable {
	private Thread t;
	private String threadName;

	private double X_NOW,Y_NOW,THETA_NOW;

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

			X_NOW = X.tick(x,encoder_X,Q,R);
			Y_NOW = Y.tick(y,encoder_Y,Q,R);
			THETA_NOW = T.tick(theta,encoder_T,Q,R);
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void go(double x, double y, double th) {
		if (y > Y_NOW) {
			while (Y_NOW < y) {
				System.out.println(Y_NOW);
				main.theRobot.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,200,0);
			}
		}
		else if (y < Y_NOW) {
			while (Y_NOW > y)
				main.theRobot.runMotor(RXTXRobot.MOTOR1,-200,RXTXRobot.MOTOR2,-200,0);
		}
		else if (th > THETA_NOW) {
			while (THETA_NOW < th)
				main.theRobot.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,-200,0);
		}
		else if (th < THETA_NOW) {
			while (THETA_NOW > th)
				main.theRobot.runMotor(RXTXRobot.MOTOR1,-200,RXTXRobot.MOTOR2,200,0);
		}
	}
}


public class main {
	public static RXTXRobot theRobot;

	private static int type;

	private static double X_NOW,Y_NOW,THETA_NOW;

	public static void main(String args[]) {
		Initialize robot
		theRobot = robotFunctions.init();

		//Initialize navigation thread
		Navigation position = new Navigation("Position");
		position.start();

		//Actually do stuff
		position.go(0,36,0);
		theRobot.sleep(3000);
		position.go(0,0,90);

		//Close connection
		theRobot.close();
	}
}
