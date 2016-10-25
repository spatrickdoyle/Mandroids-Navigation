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
			if (leftrighttime.length == 3) {
				left = Integer.parseInt(leftrighttime[0]);
				right = Integer.parseInt(leftrighttime[1]);
				time = Integer.parseInt(leftrighttime[2]);
			}
			else if (leftrighttime.length == 2) {
				left = Integer.parseInt(leftrighttime[0]);
				right = left;
				time = Integer.parseInt(leftrighttime[1]);
			}
			else {
				left = 0;
				right = 0;
				time = 0;
			}

			System.out.print(left);
			System.out.print(' ');
			System.out.print(right);
			System.out.print(' ');
			System.out.println(time);

			if (leftrighttime.length == 3) {
				parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,RXTXRobot.MOTOR2,right,time*1000);
			}
			else {
				int initial = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
				int initial2 = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR2);
				int thismotor,othermotor;
				boolean one = false;
				boolean two = false;

				parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,0);
				parkerAvenger.runMotor(RXTXRobot.MOTOR2,right,0);
				while (true) {
					thismotor = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
					othermotor = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR2);
					if (Math.pow(thismotor-initial,2) >= Math.pow(time,2))
						one = true;
					if (Math.pow(othermotor-initial2,2) >= Math.pow(time,2))
						two = true;

					if (one&&two)
						break;
				}
				parkerAvenger.runMotor(RXTXRobot.MOTOR1,0,0);
				parkerAvenger.runMotor(RXTXRobot.MOTOR2,0,0);
			}
		}

		//parkerAvenger.close();
	}
}
