import rxtxrobot.*;
import java.io.*;
import java.util.*;

public class Navigation implements Runnable {
	private Thread t;
	private String threadName;
	private RXTXRobot theRobot;

	private double inches_per_tick = 0.123;
	private double robot_width = 13.25;

	private int type;

	private double X_NOW,Y_NOW,THETA_NOW;

	Navigation(String name, RXTXRobot r) {
		threadName = name;
		theRobot = r;
	}

	public void run() {
		String line;
		String[] thetaxy;
		double theta,x,y;

		//Kalman filter variables
		/*double tmp[] = {0.0,0.0,0.0};
		ArrayList<Double> X = new ArrayList(Arrays.asList(tmp));
		double tmp2[] = {0.0};
		ArrayList<Double> Px = new ArrayList(Arrays.asList(tmp2));

		double tmp3[] = {0.0,0.0,0.0};
		ArrayList<Double> Y = new ArrayList(Arrays.asList(tmp3));
		double tmp4[] = {0.0};
		ArrayList<Double> Py = new ArrayList(Arrays.asList(tmp4));

		double tmp5[] = {0.0,0.0,0.0};
		ArrayList<Double> T = new ArrayList(Arrays.asList(tmp5));
		double tmp6[] = {0.0};
		ArrayList<Double> Pt = new ArrayList(Arrays.asList(tmp6));*/

		if (type == 1) {
			Kalman X = new Kalman();
			Kalman Y = new Kalman();
			Kalman T = new Kalman();

			double Q = 0.0001;
			double R = 0.001043;

			Scanner scan = new Scanner(System.in);
		}

		/*double Px_ = 0.0;
		double Kx = 0.0;
		double X_ = 0.0;

		double Py_ = 0.0;
		double Ky = 0.0;
		double Y_ = 0.0;

		double Pt_ = 0.0;
		double Kt = 0.0;
		double T_ = 0.0;*/

		double diff1,diff2,cur1,cur2;
		double prev1 = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		double prev2 = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
		double encoder_X;
		double encoder_Y;
		double encoder_T;
		double et;

		while (true) {
			/*if (type == 1) {
				line = scan.nextLine();
				thetaxy = line.split(" ");

				theta = Double.parseDouble(thetaxy[0]);
				x = Double.parseDouble(thetaxy[1]);
				y = Double.parseDouble(thetaxy[2]);
			}*/

			cur1 = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
			diff1 = inches_per_tick*(prev1-cur1);
			prev1 = cur1;

			cur2 = theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
			diff2 = inches_per_tick*(prev2-cur2);
			prev2 = cur2;

			encoder_X = ((robot_width/2.0)+((cur2*robot_width)/(cur1-cur2)))*(Math.cos((cur1-cur2)/robot_width)+1);
			encoder_Y = ((robot_width/2.0)+((cur2*robot_width)/(cur1-cur2)))*Math.sin((cur1-cur2)/robot_width);
			encoder_T = (cur1-cur2)/robot_width;

			encoder_X = encoder_X*Math.cos(THETA_NOW) + encoder_Y*Math.sin(THETA_NOW);
			encoder_Y = -encoder_X*Math.sin(THETA_NOW) + encoder_Y*Math.cos(THETA_NOW);

			/*X_ = X.get(X.size()-1) + encoder_X;// + (X.get(X.size()-1) - X.get(X.size()-2)) + ((X.get(X.size()-1)-X.get(X.size()-2)) - (X.get(X.size()-2)-X.get(X.size()-3)))/2.0;
			Px_ = Px.get(Px.size()-1) + Q;

			Y_ = Y.get(Y.size()-1) + encoder_Y;// + (Y.get(Y.size()-1) - Y.get(Y.size()-2)) + ((Y.get(Y.size()-1)-Y.get(Y.size()-2)) - (Y.get(Y.size()-2)-Y.get(Y.size()-3)))/2.0;
			Py_ = Py.get(Py.size()-1) + Q;

			T_ = T.get(T.size()-1) + encoder_T;// + (T.get(T.size()-1) - T.get(T.size()-2)) + ((T.get(T.size()-1)-T.get(T.size()-2)) - (T.get(T.size()-2)-T.get(T.size()-3)))/2.0;
			Pt_ = Pt.get(Pt.size()-1) + Q;

			Kx = Px_/(Px_+R);
			X.add(X_ + Kx*(x - (X.get(X.size()-1)-X.get(X.size()-2))));
			Px.add((1-Kx)*Px_);

			Ky = Py_/(Py_+R);
			Y.add(Y_ + Ky*(y - (Y.get(Y.size()-1)-Y.get(Y.size()-2))));
			Py.add((1-Ky)*Py_);

			Kt = Pt_/(Pt_+R);
			T.add(T_ + Kt*(theta - (T.get(T.size()-1)-T.get(T.size()-2))));
			Pt.add((1-Kt)*Pt_);*/

			if (type == 0) {
				X_NOW += encoder_X;//X.get(X.size()-1);
				Y_NOW += encoder_Y;//Y.get(Y.size()-1);
				THETA_NOW += encoder_T;//T.get(T.size()-1);
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
			while (Y_NOW < y)
				theRobot.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,200,0);
		}
		else if (y < Y_NOW) {
			while (Y_NOW > y)
				theRobot.runMotor(RXTXRobot.MOTOR1,-200,RXTXRobot.MOTOR2,-200,0);
		}
		else if (th > THETA_NOW) {
			while (THETA_NOW < th)
				theRobot.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,-200,0);
		}
		else if (th < THETA_NOW) {
			while (THETA_NOW > th)
				theRobot.runMotor(RXTXRobot.MOTOR1,-200,RXTXRobot.MOTOR2,200,0);
		}
	}
}
