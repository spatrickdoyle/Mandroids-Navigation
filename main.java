import rxtxrobot.*;
import java.io.*;
import java.util.*;

class Navigation implements Runnable {
	private Thread t;
	private String threadName;

	private double X_NOW,Y_NOW,THETA_NOW,THETA_NOW2;
	private double X_PREV1,Y_PREV1,THETA_PREV1;
	private double X_PREV2,Y_PREV2,THETA_PREV2;

	Navigation(String name) {
		threadName = name;
	}

	public void run() {
		String line;
		String[] thetaxy;
		double theta,x,y,theta2;

		//Kalman X = new Kalman();
		Kalman Y = new Kalman();
		//Kalman T = new Kalman();

		double Q = 0.0001;
		double R = 0.001043;

		Scanner scan = new Scanner(System.in);

		while (true) {
			line = scan.nextLine();
			thetaxy = line.split(" ");

			theta = Double.parseDouble(thetaxy[0]);
			x = Double.parseDouble(thetaxy[1]);
			y = Double.parseDouble(thetaxy[2]);
			theta2 = Double.parseDouble(thetaxy[3]);

			X_NOW += x;
			//Y_NOW += y;
			if ((Math.pow(theta,2) < 400)&&(Math.pow(theta2,2) < 400)) {
				THETA_NOW += theta;
				THETA_NOW2 += theta2;
			}
			else {
				theta = 0.0;
				theta2 = 0.0;
			}

			System.out.println(theta+" "+theta2+" "+x+" "+y);
			/*X_PREV2 = X_PREV1;
			  X_PREV1 = X_NOW;*/
			Y_PREV2 = Y_PREV1;
            Y_PREV1 = Y_NOW;
			/*THETA_PREV2 = THETA_PREV1;
			  THETA_PREV1 = THETA_NOW;*/

			//X_NOW = X.tick(x,(X_NOW-X_PREV1) + ((X_NOW-X_PREV1)-(X_PREV1-X_PREV2))/2.0,Q,R);
			Y_NOW = Y.tick(y,(Y_NOW-Y_PREV1) + ((Y_NOW-Y_PREV1)-(Y_PREV1-Y_PREV2))/2.0,Q,R);
			//THETA_NOW = T.tick(theta,(THETA_NOW-THETA_PREV1) + ((THETA_NOW-THETA_PREV1)-(THETA_PREV1-THETA_PREV2))/2.0,Q,R);
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void go(double y) {
		go(y,200);
	}

	public void go(double y, int power) {
		Y_NOW = 0;
		if (y < Y_NOW) {
			while (Y_NOW > y) {
				//System.out.println(Y_NOW);
				main.theRobot.runMotor(RXTXRobot.MOTOR1,power,RXTXRobot.MOTOR2,power,0);
			}
			//System.out.println(Y_NOW);
		}
		else if (y > Y_NOW) {
			while (Y_NOW < y) {
				//System.out.println(Y_NOW);
				main.theRobot.runMotor(RXTXRobot.MOTOR1,-power,RXTXRobot.MOTOR2,-power,0);
			}
			//System.out.println(Y_NOW);
		}

		main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
	}

	public void turn(double t) {
		THETA_NOW = 0;
		THETA_NOW2 = 0;
		//main.theRobot.runMotor(RXTXRobot.MOTOR1,power,RXTXRobot.MOTOR2,-power,(int)(t*1000));
		//System.out.println(THETA_NOW+" "+THETA_NOW2);

                if (t > 0) {
                        while (THETA_NOW-THETA_NOW2 < t) {
                                //System.out.println(THETA_NOW+" "+THETA_NOW2+" "+(THETA_NOW-THETA_NOW2));
                                main.theRobot.runMotor(RXTXRobot.MOTOR1,125,RXTXRobot.MOTOR2,-180,0);
                        }
			//System.out.println(THETA_NOW+" "+THETA_NOW2+" "+(THETA_NOW-THETA_NOW2));
                }
                else if (t < 0) {
			while (THETA_NOW+THETA_NOW2 < -t) {
                                //System.out.println(THETA_NOW+" "+THETA_NOW2+" "+(THETA_NOW+THETA_NOW2));
                                main.theRobot.runMotor(RXTXRobot.MOTOR1,-180,RXTXRobot.MOTOR2,115,0);
                        }
                        //System.out.println(THETA_NOW+" "+THETA_NOW2+" "+(THETA_NOW+THETA_NOW2));
                }

                main.theRobot.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
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
		theRobot.sleep(7000);

		//Actually do stuff
		//position.go(60,90);
		/*theRobot.sleep(1000);
		position.go(48,90);
		theRobot.sleep(1000);*/
		position.turn(90);

		//Close connection
		theRobot.close();
	}
}
