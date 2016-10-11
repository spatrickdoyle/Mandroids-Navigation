//import rxtxrobot.*;
import java.io.*;

public class remote_control {

	public static void main(String args[]) {
		/*RXTXRobot parkerAvenger = new ArduinoUno();
		parkerAvenger.setPort("/dev/ttyS80");
		parkerAvenger.connect();
		parkerAvenger.refreshAnalogPins();*/

		String line;
		char next;
		int left,right,time;

		try {
			while (true) {
				line = "";
				while ((next = (char)System.in.read()) != ' ') {
					line += next;
				}
				left = Integer.parseInt(line);

				line = "";
				while ((next = (char)System.in.read()) != ' ') {
					line += next;
				}
				right = Integer.parseInt(line);

				line = "";
				while ((next = (char)System.in.read()) != '\n') {
					line += next;
				}
				time = Integer.parseInt(line);

				System.out.print(left);
				System.out.print(' ');
				System.out.print(right);
				System.out.print(' ');
				System.out.println(time);
			}
		}
		catch(IOException ex) {
			System.exit(-1);
		}

		/*parkerAvenger.attachMotor(RXTXRobot.MOTOR1,5);
		parkerAvenger.attachMotor(RXTXRobot.MOTOR2,6);
		parkerAvenger.runMotor(RXTXRobot.MOTOR1,255,RXTXRobot.MOTOR2,255,0);*/


		//parkerAvenger.close();
	}
}