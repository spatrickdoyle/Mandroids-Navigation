import rxtxrobot.*;
import java.io.*;

public class remote_control {

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

			parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,RXTXRobot.MOTOR2,right,time*1000);
		}

		//parkerAvenger.close();
	}
}
