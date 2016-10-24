import rxtxrobot.*;
import java.io.*;
import java.util.*;

public class main {
	public static void main(String args[]) {
		RXTXRobot parkerAvenger = new ArduinoUno();
		parkerAvenger.setPort("/dev/ttyS80");
		parkerAvenger.connect();
		parkerAvenger.refreshAnalogPins();

		String line;
		String[] thetaxy;
		Float theta,x,y;

		//Kalman filter variables
		Float tmp[] = {0.0F,0.0F,0.0F};
		ArrayList<Float> X = new ArrayList(Arrays.asList(tmp));
		Float tmp2[] = {0.0F};
		ArrayList<Float> Px = new ArrayList(Arrays.asList(tmp2));

		Float Q = 0.0001F;
		Float R = 0.001043F;

		Float Px_ = 0.0F;
		Float Kx = 0.0F;
		Float X_ = 0.0F;


		parkerAvenger.attachMotor(RXTXRobot.MOTOR1,5);
		parkerAvenger.attachMotor(RXTXRobot.MOTOR2,6);

		Scanner scan = new Scanner(System.in);

		Float diff;
		Float prev = (float)parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		Float cur;

		while (Math.pow(X.get(X.size()-1),2) < Math.pow(48,2)) {
			line = scan.nextLine();
			thetaxy = line.split(" ");

			theta = Float.parseFloat(thetaxy[0]);
			x = Float.parseFloat(thetaxy[1]);
			y = Float.parseFloat(thetaxy[2]);

			cur = (float)parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
			diff = prev-cur;
			prev = cur;

			System.out.println(diff);
			X_ = X.get(X.size()-1) + (13.5F/100.0F)*diff;// + (X.get(X.size()-1) - X.get(X.size()-2)) + ((X.get(X.size()-1)-X.get(X.size()-2)) - (X.get(X.size()-2)-X.get(X.size()-3)));
			Px_ = Px.get(Px.size()-1) + Q;

			Kx = Px_/(Px_+R);
			X.add(X_ + Kx*(y - (X.get(X.size()-1)-X.get(X.size()-2))));
			Px.add((1-Kx)*Px_);

			//System.out.println(y);
			System.out.println(X.get(X.size()-1));
			//while (X.get(X.size()-1) < 30)
			parkerAvenger.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,200,0);
		}

		parkerAvenger.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);

		parkerAvenger.close();
	}
}
