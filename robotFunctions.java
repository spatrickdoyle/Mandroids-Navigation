import rxtxrobot.*;
import java.io.*;

public class robotFunctions {
	public static void main(String[] args) {
		//RXTXRobot theRobot = init();
		RXTXRobot theRobot = new ArduinoUno();
		theRobot.setPort("/dev/ttyS80");
		theRobot.connect();
		theRobot.refreshAnalogPins();

		theRobot.attachMotor(RXTXRobot.MOTOR1,5);
		theRobot.attachMotor(RXTXRobot.MOTOR2,6);
		theRobot.attachMotor(RXTXRobot.MOTOR3,10);

		theRobot.attachServo(RXTXRobot.SERVO1,8);

		theRobot.runMotor(RXTXRobot.MOTOR1,300,RXTXRobot.MOTOR2,300,2000);
		//move(theRobot);
		//raiseBoom(theRobot);
		//TempTester(theRobot);
		//WindTester(theRobot);
		//lowerBoom(theRobot);
		//move(theRobot);
		//theRobot.getConductivity();
		//dropBall(theRobot);
	}

	public static RXTXRobot init() {
		RXTXRobot theRobot = new ArduinoUno();
		theRobot.setPort("/dev/ttyS80");
		theRobot.connect();
		theRobot.refreshAnalogPins();

		theRobot.attachMotor(RXTXRobot.MOTOR1,5);
		theRobot.attachMotor(RXTXRobot.MOTOR2,6);
		theRobot.attachMotor(RXTXRobot.MOTOR3,10);

		theRobot.attachServo(RXTXRobot.SERVO1,8);

		return theRobot;
	}

	public static void turn(RXTXRobot r) {
		r.attachMotor(RXTXRobot.MOTOR1,5);
		r.attachMotor(RXTXRobot.MOTOR2,6);
		r.runMotor(RXTXRobot.MOTOR1,250,RXTXRobot.MOTOR2,-500,4600);
	}

	public static void raiseBoom(RXTXRobot r) {
		r.runMotor(RXTXRobot.MOTOR3,150,10000);
	}
	public static void lowerBoom(RXTXRobot r) {
		r.runMotor(RXTXRobot.MOTOR3,-100,5500);
	}

	public static void move(RXTXRobot r) {
		int initial = r.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		System.out.println(initial);
		while (Math.pow(r.getEncodedMotorPosition(RXTXRobot.MOTOR1)-initial,2) < Math.pow(100,2)) {
			r.runMotor(RXTXRobot.MOTOR1,200,RXTXRobot.MOTOR2,200,0);
		}
	}

	public static moveArm(RXTXRobot r) {
		r.moveServo(RXTXRobot.SERVO2,180);
		r.sleep(2000);
		r.moveServo(RXTXRobot.SERVO2,90);
	}

	public static void dropBall(RXTXRobot r) {
		r.moveServo(RXTXRobot.SERVO1,0);
		r.sleep(5000);
		r.moveServo(RXTXRobot.SERVO1,90);
	}

	public static void Sonar(RXTXRobot r) {
		for (int x=0; x < 100; ++x) {
			//Read the ping sensor value, which is connected to pin 12, 100 times
			System.out.println("Response: " + r.getPing(12) + " cm");
			r.sleep(300);
		}
	}

	public static void BumpSensor(RXTXRobot r) {
		AnalogPin bump = r.getAnalogPin(1);
		r.attachMotor(RXTXRobot.MOTOR1,5);
		r.attachMotor(RXTXRobot.MOTOR2,6);
		while (bump.getValue() > 1000) {
			r.refreshAnalogPins();
			bump = r.getAnalogPin(1);
			r.runMotor(RXTXRobot.MOTOR1,100,RXTXRobot.MOTOR2,100,0);
		}
		r.runMotor(RXTXRobot.MOTOR1,100,RXTXRobot.MOTOR2,0,0);
	}

	public static void TempTester(RXTXRobot r) {
		AnalogPin temp = r.getAnalogPin(0);
		double roomTemp = temp.getValue()*(-0.15) + 106.734;

		System.out.println("Sensor " + 0 + " has value: " + temp.getValue());
		System.out.println("The temperature is " + roomTemp + " degrees Celsius.");
	}

	public static void WindTester(RXTXRobot r) {
		AnalogPin wind = r.getAnalogPin(2);
		AnalogPin temp = r.getAnalogPin(0);
		//System.out.println("Sensor " + 2 + " has value: " + wind.getValue());
		double roomTemp = temp.getValue()*(-0.15) + 106.734;
		double windTemp = wind.getValue()*(-0.152) + 102.778;
		double difference = (windTemp - roomTemp);
		System.out.println("Wind speed is " + (difference*1.002 - 0.928) + " knots");
	}
}
