import rxtxrobot.*;
import java.io.*;
import java.util.*;

class Navigation implements Runnable {
	private Thread t;
	private String threadName;

	private Float inches_per_tick = 0.123F;
	protected Float X_NOW,Y_NOW,THETA_NOW;

	Navigation(String name) {
		threadName = name;
	}

	public void run() {
		String line;
		String[] thetaxy;
		Float theta,x,y;

		//Kalman filter variables
		Float tmp[] = {0.0F,0.0F,0.0F};
		ArrayList<Float> X = new ArrayList(Arrays.asList(tmp));
		Float tmp2[] = {0.0F};
		ArrayList<Float> Px = new ArrayList(Arrays.asList(tmp2));

		Float tmp3[] = {0.0F,0.0F,0.0F};
		ArrayList<Float> Y = new ArrayList(Arrays.asList(tmp3));
		Float tmp4[] = {0.0F};
		ArrayList<Float> Py = new ArrayList(Arrays.asList(tmp4));

		Float tmp5[] = {0.0F,0.0F,0.0F};
		ArrayList<Float> T = new ArrayList(Arrays.asList(tmp5));
		Float tmp6[] = {0.0F};
		ArrayList<Float> Pt = new ArrayList(Arrays.asList(tmp6));

		Float Q = 0.0001F;
		Float R = 0.001043F;

		Float Px_ = 0.0F;
		Float Kx = 0.0F;
		Float X_ = 0.0F;

		Float Py_ = 0.0F;
		Float Ky = 0.0F;
		Float Y_ = 0.0F;

		Float Pt_ = 0.0F;
		Float Kt = 0.0F;
		Float T_ = 0.0F;

		Scanner scan = new Scanner(System.in);

		Float diff1,diff2,cur1,cur2;
		Float prev1 = (float)main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		Float prev2 = (float)main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);

		while (Math.pow(X.get(X.size()-1),2) < Math.pow(48,2)) {
			line = scan.nextLine();
			thetaxy = line.split(" ");

			theta = Float.parseFloat(thetaxy[0]);
			x = Float.parseFloat(thetaxy[1]);
			y = Float.parseFloat(thetaxy[2]);

			cur1 = (float)main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR1);
			diff1 = prev1-cur1;
			prev1 = cur1;

			cur2 = (float)main.theRobot.getEncodedMotorPosition(RXTXRobot.MOTOR2);
			diff2 = prev2-cur2;
			prev2 = cur2;

			X_ = X.get(X.size()-1) + (X.get(X.size()-1) - X.get(X.size()-2)) + ((X.get(X.size()-1)-X.get(X.size()-2)) - (X.get(X.size()-2)-X.get(X.size()-3)))/2.0F;
			Px_ = Px.get(Px.size()-1) + Q;

			Y_ = Y.get(Y.size()-1) + inches_per_tick*(diff1+diff2)/2.0F;// + (Y.get(Y.size()-1) - Y.get(Y.size()-2)) + ((Y.get(Y.size()-1)-Y.get(Y.size()-2)) - (Y.get(Y.size()-2)-Y.get(Y.size()-3)))/2.0F;
			Py_ = Py.get(Py.size()-1) + Q;

			T_ = T.get(T.size()-1) + (T.get(T.size()-1) - T.get(T.size()-2)) + ((T.get(T.size()-1)-T.get(T.size()-2)) - (T.get(T.size()-2)-T.get(T.size()-3)))/2.0F;
			Pt_ = Pt.get(Pt.size()-1) + Q;

			Kx = Px_/(Px_+R);
			X.add(X_ + Kx*(x - (X.get(X.size()-1)-X.get(X.size()-2))));
			Px.add((1-Kx)*Px_);

			Ky = Py_/(Py_+R);
			Y.add(Y_ + Ky*(y - (Y.get(Y.size()-1)-Y.get(Y.size()-2))));
			Py.add((1-Ky)*Py_);

			Kt = Pt_/(Pt_+R);
			T.add(T_ + Kt*(theta - (T.get(T.size()-1)-T.get(T.size()-2))));
			Pt.add((1-Kt)*Pt_);

			X_NOW = X.get(X.size()-1);
			Y_NOW = Y.get(Y.size()-1);
			THETA_NOW = T.get(T.size()-1);
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void go(float x, float y, float th) {
		
	}
}

public class main {
	protected static RXTXRobot theRobot;

	public static void main(String args[]) {
		//Initialize navigation thread
		Navigation position = new Navigation("Position");
		position.start();

		//Initialize robot
		init();

		//Actually do stuff
		while (Math.pow(position.Y_NOW,2) < Math.pow(48,2))
			theRobot.runMotor(RXTXRobot.MOTOR1,300,RXTXRobot.MOTOR2,300,0);

		//Close connection
		theRobot.close();
	}

	public static void init() {
		theRobot = new ArduinoUno();
		theRobot.setPort("/dev/ttyS80");
		theRobot.connect();
		theRobot.refreshAnalogPins();

		theRobot.attachMotor(RXTXRobot.MOTOR1,5);
		theRobot.attachMotor(RXTXRobot.MOTOR2,6);
	}
}
