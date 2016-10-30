import rxtxrobot.*;
import java.io.*;
import java.util.*;

public class main {
	public static void main(String args[]) {
		//Initialize robot
		RXTXRobot theRobot = robotFunctions.init();

		//Initialize navigation thread
		Navigation position = new Navigation("Position",theRobot);
		position.start_encoders();

		//Actually do stuff
		//while (position.Y_NOW < 30)
		//	theRobot.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,200,0);

		//Close connection
		theRobot.close();
	}

		/*public static void init() {
		theRobot = new ArduinoUno();
		theRobot.setPort("/dev/ttyS80");
		theRobot.connect();
		theRobot.refreshAnalogPins();

		theRobot.attachMotor(RXTXRobot.MOTOR1,5);
		theRobot.attachMotor(RXTXRobot.MOTOR2,6);
		}*/
}
