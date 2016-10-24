import rxtxrobot.*;
import java.io.*;

public class commandline {

	public static void main(String args[]) {
		RXTXRobot parkerAvenger = new ArduinoUno();
		parkerAvenger.setPort("/dev/ttyS80");
		parkerAvenger.connect();
		parkerAvenger.refreshAnalogPins();

		String line;
		String[] leftrighttime;
		int left,right,time;

		parkerAvenger.attachMotor(RXTXRobot.MOTOR1,5);
                parkerAvenger.attachMotor(RXTXRobot.MOTOR2,6);

		while (true) {
			line = System.console().readLine();
			leftrighttime = line.split(" ");

			left = Integer.parseInt(leftrighttime[0]);
			right = Integer.parseInt(leftrighttime[1]);
			time = Integer.parseInt(leftrighttime[2]);

			System.out.print(left);
			System.out.print(' ');
			System.out.print(right);
			System.out.print(' ');
			System.out.println(time);

			int initial = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
			while (Math.pow(parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1)-initial,2) < Math.pow(time,2)) {
				//System.out.println(Math.pow(parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1)-initial,2));
				parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,RXTXRobot.MOTOR2,right,0);
			}
			parkerAvenger.runMotor(RXTXRobot.MOTOR1,0,RXTXRobot.MOTOR2,0,0);
		}

		//parkerAvenger.close();
	}
}
