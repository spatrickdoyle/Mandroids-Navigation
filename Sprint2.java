import rxtxrobot.*;
import java.io.*;

public class Sprint2 {

	public static void main(String args[]) {
		RXTXRobot parkerAvenger = new ArduinoUno();
		parkerAvenger.setPort("/dev/ttyS80");
		parkerAvenger.connect();
		parkerAvenger.refreshAnalogPins();

		//turn(parkerAvenger);
		//raiseBoom(parkerAvenger);
		move3Meters(parkerAvenger);
		//moveServoAngle(parkerAvenger);
		//Sonar(parkerAvenger);
		//BumpSensor(parkerAvenger);
		//TempTester(parkerAvenger);
		//WindTester(parkerAvenger);

		parkerAvenger.close();
	}


	public static void turn(RXTXRobot r) {
		r.attachMotor(RXTXRobot.MOTOR1,5);
		r.attachMotor(RXTXRobot.MOTOR2,6);
		r.runMotor(RXTXRobot.MOTOR1,250,RXTXRobot.MOTOR2,-500,4600);
	}

	public static void raiseBoom(RXTXRobot r) {
		//Connect the servos to the Arduino to pin 8
		r.attachServo(RXTXRobot.SERVO1,8);
		r.moveServo(RXTXRobot.SERVO1,90);
		//Move the servo a certain amount of time to raise the boom
		r.moveServo(RXTXRobot.SERVO1,179);
		r.sleep(5000);
		r.moveServo(RXTXRobot.SERVO1,90);
	}

	public static void move3Meters(RXTXRobot r) {
		//Attach motors on pins 5 and 6 and move them forward for an amount of time we will tune for 3 meters
		r.attachMotor(RXTXRobot.MOTOR1,5);
		r.attachMotor(RXTXRobot.MOTOR2,6);
		int initial = r.getEncodedMotorPosition(RXTXRobot.MOTOR1);
		System.out.println(initial);
		while (r.getEncodedMotorPosition(RXTXRobot.MOTOR1)-initial > -100) {
			r.runMotor(RXTXRobot.MOTOR1,-100,RXTXRobot.MOTOR2,-100,0);
		}
	}

	public static void moveServoAngle(RXTXRobot r) {
		//Connect the servos to the Arduino to pin 9
		r.attachServo(RXTXRobot.SERVO1,7);
		//Move the servo to the specified angle
		r.moveServo(RXTXRobot.SERVO1,0);
		r.sleep(1000);
		r.moveServo(RXTXRobot.SERVO1,180);
		r.sleep(1000);
		r.moveServo(RXTXRobot.SERVO1,75);
		r.sleep(1000);
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
		double roomTemp = (temp.getValue() - 796.6969697)/-9.212121212;

		System.out.println("Sensor " + 0 + " has value: " + temp.getValue());
		System.out.println("The temperature is " + roomTemp + " degrees Celsius.");
	}

	public static void WindTester(RXTXRobot r) {
		AnalogPin wind = r.getAnalogPin(2);
		AnalogPin temp = r.getAnalogPin(0);
		System.out.println("Sensor " + 2 + " has value: " + wind.getValue());
		double roomTemp = (temp.getValue() - 796.6969697)/-9.212121212;
		double windTemp = (wind.getValue() - 796.6969697)/-9.212121212;
		double difference = (windTemp - roomTemp);
		System.out.println("Temperature difference is " + difference);
	}
}
