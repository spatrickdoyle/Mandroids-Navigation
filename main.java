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
		float theta,x,y;

		//Kalman filter variables
		float tmp[] = {0.0f,0.0f,0.0f};
		ArrayList<Float> X = new ArrayList(Arrays.asList(tmp));
		float tmp2[] = {0.0f};
		ArrayList<Float> Px = new ArrayList(Arrays.asList(tmp2));

		float Q = 0.0001f;
		float R = 0.001043f;

		float Px_ = 0.0f;
		float Kx = 0.0f;
		float X_ = 0.0f;


		parkerAvenger.attachMotor(RXTXRobot.MOTOR1,5);
		parkerAvenger.attachMotor(RXTXRobot.MOTOR2,6);

		Scanner scan = new Scanner(System.in);

		while (true) {
			line = scan.nextLine();
			thetaxy = line.split(" ");

			theta = Float.parseFloat(thetaxy[0]);
			x = Float.parseFloat(thetaxy[1]);
			y = Float.parseFloat(thetaxy[2]);

			X_ = X.get(X.size()-1) + (X.get(X.size()-1) - X.get(X.size()-2)) + 0.5f*((X.get(X.size()-1)-X.get(X.size()-2)) - (X.get(X.size()-2)-X.get(X.size()-3)));
			Px_ = Px.get(Px.size()-1) + Q;

			Kx = Px_/(Px_+R);
			X.add(X_ + Kx*(y - (X.get(X.size()-1)-X.get(X.size()-2))));
			Px.add((1-Kx)*Px_);*/

			//System.out.println(y);
			System.out.println(X.get(X.size()-1));
			//while (y < 60)//X.get(X.size()-1) < 60)
			//	parkerAvenger.runMotor(RXTXRobot.MOTOR1,100,RXTXRobot.MOTOR2,100,1);
		}

		//parkerAvenger.close();
	}
}
