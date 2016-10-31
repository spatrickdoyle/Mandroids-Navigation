import rxtxrobot.*;
import java.io.*;
import java.util.*;

class Navigation implements Runnable {
	private Thread t;
	private String threadName;

	private double inches_per_tick = 0.123;
	private double robot_width = 13.25;

	private int type;

	private double X_NOW,Y_NOW,THETA_NOW;

	Navigation(String name) {
		threadName = name;
	}

	public void run() {
		String line;
		String[] thetaxy;
		double theta,x,y;

		if (type == 1) {
			Kalman X = new Kalman();
			Kalman Y = new Kalman();
			Kalman T = new Kalman();

			double Q = 0.0001;
			double R = 0.001043;

			Scanner scan = new Scanner(System.in);
		}

		double diff1,diff2;
		int cur1,cur2;
		int prev1 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		int prev2 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
		double encoder_X;
		double encoder_Y;
		double encoder_T;

		while (true) {
			/*if (type == 1) {
				line = scan.nextLine();
				thetaxy = line.split(" ");

				theta = Double.parseDouble(thetaxy[0]);
				x = Double.parseDouble(thetaxy[1]);
				y = Double.parseDouble(thetaxy[2]);
			}*/
			cur1 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
			diff1 = inches_per_tick*(prev1-cur1);
			prev1 = cur1;

			cur2 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
			diff2 = inches_per_tick*(prev2-cur2);
			prev2 = cur2;

			encoder_X = ((robot_width/2.0)+((cur2*robot_width)/(cur1-cur2)))*(Math.cos((cur1-cur2)/robot_width)+1);
			encoder_Y = ((robot_width/2.0)+((cur2*robot_width)/(cur1-cur2)))*Math.sin((cur1-cur2)/robot_width);
			encoder_T = (cur1-cur2)/robot_width;

			encoder_X = encoder_X*Math.cos(THETA_NOW) + encoder_Y*Math.sin(THETA_NOW);
			encoder_Y = -encoder_X*Math.sin(THETA_NOW) + encoder_Y*Math.cos(THETA_NOW);

			if (type == 0) {
				X_NOW += encoder_X;
				Y_NOW += encoder_Y;
				THETA_NOW += encoder_T;
			}
			/*else if (type == 1) {
				X_NOW = X.tick(x,encoder_X,Q,R);
				Y_NOW = Y.tick(y,encoder_Y,Q,R);
				THETA_NOW = T.tick(theta,encoder_T,Q,R);
			}*/
		}
	}

	public void start() {
		type = 1;
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void start_encoders() {
		type = 0;
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

	private static double inches_per_tick = 0.123;
        private static double robot_width = 13.25;

        private static int type;

        private static double X_NOW,Y_NOW,THETA_NOW;

	public static void main(String args[]) {
		//Initialize robot
		//theRobot = robotFunctions.init();
		theRobot = new ArduinoUno();
                theRobot.setPort("/dev/ttyS80");
                theRobot.connect();
                theRobot.refreshAnalogPins();

                theRobot.attachMotor(RXTXRobot.MOTOR1,5);
                theRobot.attachMotor(RXTXRobot.MOTOR2,6);

		//System.out.println(theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1));
		//theRobot.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,200,3000);
		//System.out.println(theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1));

		//Initialize navigation thread
		//Navigation position = new Navigation("Position");
		//position.start_encoders();

		double diff1,diff2;
                int cur1,cur2;
                int prev1 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
                int prev2 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
                double encoder_X;
                double encoder_Y;
                double encoder_T;

		while (true) {
                        /*if (type == 1) {
                                line = scan.nextLine();
                                thetaxy = line.split(" ");

                                theta = Double.parseDouble(thetaxy[0]);
                                x = Double.parseDouble(thetaxy[1]);
                                y = Double.parseDouble(thetaxy[2]);
                        }*/
                        cur1 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
                        diff1 = inches_per_tick*(cur1-prev1);
                        prev1 = cur1;

                        cur2 = main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
                        diff2 = inches_per_tick*(cur2-prev2);
                        prev2 = cur2;

			System.out.println(diff1 + " " + diff2);

			if (diff1-diff2 != 0) {
	                        encoder_X = ((robot_width/2.0)+((diff2*robot_width)/(diff1-diff2)))*(Math.cos((diff1-diff2)/robot_width)+1);
	                        encoder_Y = ((robot_width/2.0)+((diff2*robot_width)/(diff1-diff2)))*Math.sin((diff1-diff2)/robot_width);
        	                encoder_T = (diff1-diff2)/robot_width;
				System.out.println(encoder_X + " " + encoder_Y + " " + encoder_T);
			}
			else {
				encoder_X = 0;
				encoder_Y = diff1;
				encoder_T = 0;
			}

                        encoder_X = encoder_X*Math.cos(THETA_NOW) + encoder_Y*Math.sin(THETA_NOW);
                        encoder_Y = -encoder_X*Math.sin(THETA_NOW) + encoder_Y*Math.cos(THETA_NOW);

                        if (type == 0) {
                                X_NOW += encoder_X;
                                Y_NOW += encoder_Y;
                                THETA_NOW += encoder_T;
                        }
                        /*else if (type == 1) {
                                X_NOW = X.tick(x,encoder_X,Q,R);
                                Y_NOW = Y.tick(y,encoder_Y,Q,R);
                                THETA_NOW = T.tick(theta,encoder_T,Q,R);
                        }*/
			//System.out.println(X_NOW + " " + Y_NOW + " " + THETA_NOW);
		}

		//Actually do stuff
		//position.go(0,36,0);
		//theRobot.sleep(3000);
		//position.go(0,0,90);

		//Close connection
		//theRobot.close();
	}
}
