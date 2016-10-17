import rxtxrobot.*;
import java.io.*;
import java.util.Scanner;

public class main {
	public static void main(String args[]) {
		RXTXRobot parkerAvenger = new ArduinoUno();
		parkerAvenger.setPort("/dev/ttyS80");
		parkerAvenger.connect();
		parkerAvenger.refreshAnalogPins();

		String line;
		String[] thetaxy;
		float theta,x,y;

		parkerAvenger.attachMotor(RXTXRobot.MOTOR1,5);
		parkerAvenger.attachMotor(RXTXRobot.MOTOR2,6);

		Scanner scan = new Scanner(System.in);

		while (true) {
			line = scan.nextLine();
			thetaxy = line.split(" ");
			
			theta = Float.parseFloat(thetaxy[0]);
			x = Float.parseFloat(thetaxy[1]);
			y = Float.parseFloat(thetaxy[2]);

			System.out.print(theta);
			System.out.print(' ');
			System.out.print(x);
			System.out.print(' ');
			System.out.println(y);

			//parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,RXTXRobot.MOTOR2,right,time*1000);

			//line = scan.nextLine();
		}

		//parkerAvenger.close();
	}
}
