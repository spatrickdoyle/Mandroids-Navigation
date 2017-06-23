import rxtxrobot.*;
import java.io.*;
import java.util.*;

public class commandline {

	public static void main(String args[]) {
		RXTXRobot parkerAvenger = new ArduinoUno();
		parkerAvenger.setPort("/dev/ttyS80");
		parkerAvenger.connect();
		parkerAvenger.refreshAnalogPins();

		String line;
		String[] leftrighttime;
		int left,right;
		float time;

		parkerAvenger.attachMotor(RXTXRobot.MOTOR1,5);
		parkerAvenger.attachMotor(RXTXRobot.MOTOR2,6);

		Scanner scan = new Scanner(System.in);

		while (true) {
			line = scan.nextLine();
			leftrighttime = line.split(" ");
			if (leftrighttime.length == 3) {
				left = Integer.parseInt(leftrighttime[0]);
				right = Integer.parseInt(leftrighttime[1]);
				time = Float.parseFloat(leftrighttime[2]);
			}
			else if (leftrighttime.length == 2) {
				left = Integer.parseInt(leftrighttime[0]);
				right = left;
				time = Integer.parseInt(leftrighttime[1]);
			}
			else if (leftrighttime.length == 1) {
				left = 200*(Integer.parseInt(leftrighttime[0])/Math.abs(Integer.parseInt(leftrighttime[0])));
				right = -left;
				time = Integer.parseInt(leftrighttime[0]);
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
				int initial = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
				parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,RXTXRobot.MOTOR2,right,(int)(time*1000));
				System.out.println(parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1) - initial);
			}
			else {
				int initial = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
				int initial2 = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR2);
				int thismotor = initial;
				int othermotor = initial2;;
				int prev1,prev2;
				boolean one = false;
				boolean two = false;
				double v1,v2;

				while (true) {
					prev1 = thismotor;
					prev2 = othermotor;
					thismotor = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR1);
					othermotor = parkerAvenger.getEncodedMotorPosition(RXTXRobot.MOTOR2);

					if (Math.pow(thismotor-initial,2) >= Math.pow(time,2))
						one = true;
					if (Math.pow(othermotor-initial2,2) >= Math.pow(time,2))
						two = true;

					if (one&&two)
						break;

					v1 = (thismotor-prev1)*(left/Math.abs(left));
					v2 = (othermotor-prev2)*(right/Math.abs(right));

					left += (v2-v1);//*(left/Math.abs(left));
					right += (v1-v2);//*(right/Math.abs(right));
					parkerAvenger.runMotor(RXTXRobot.MOTOR1,left,0);
					parkerAvenger.runMotor(RXTXRobot.MOTOR2,right,0);

					System.out.println(v1+" "+v2+" "+left+" "+right+" "+(thismotor-initial)+" "+(othermotor-initial2));
				}
				parkerAvenger.runMotor(RXTXRobot.MOTOR1,0,0);
				parkerAvenger.runMotor(RXTXRobot.MOTOR2,0,0);
			}
		}

		//parkerAvenger.close();
	}
}
