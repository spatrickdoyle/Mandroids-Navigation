import rxtxrobot.*;
import java.io.*;

public class robotFunctions {

	public static RXTXRobot init() {
		RXTXRobot theRobot = new ArduinoUno();
		theRobot.setPort("/dev/ttyS80");
		theRobot.connect();
		theRobot.refreshAnalogPins();

		theRobot.attachMotor(RXTXRobot.MOTOR1,5);
		theRobot.attachMotor(RXTXRobot.MOTOR2,6);
		theRobot.attachMotor(RXTXRobot.MOTOR3,10);

		theRobot.attachServo(RXTXRobot.SERVO1,8);
		theRobot.attachServo(RXTXRobot.SERVO2,4);

		return theRobot;
	}

	public static void raiseBoom(RXTXRobot r) {
		r.runMotor(RXTXRobot.MOTOR3,100,8000);
	}
	public static void lowerBoom(RXTXRobot r) {
		r.runMotor(RXTXRobot.MOTOR3,-100,2500);
	}

	public static void moveArm(RXTXRobot r) {
		r.moveServo(RXTXRobot.SERVO2,180);
		r.sleep(2000);
		r.moveServo(RXTXRobot.SERVO2,90);
	}

	public static void dropBall(RXTXRobot r) {
		r.moveServo(RXTXRobot.SERVO1,0);
		r.sleep(5000);
		r.moveServo(RXTXRobot.SERVO1,90);
	}

	public static double Sonar(RXTXRobot r) {
        //Read the ping sensor value, which is connected to pin 7
        //System.out.println("Response Ping: " + r.getPing(7) + " cm");
        //This makes the ping sensor not a piece of shit and returns an accurate value
        double actual = (r.getPing(7)*1.009)+0.942;
        //System.out.println("Actual value: " + actual + " cm");
		return actual;
	}

	public static double Sonar2(RXTXRobot r) {
		double actual = (r.getPing(11)*1.006488165)+1;
		return actual;
	}

	public static double Sonar3(RXTXRobot r) {
		double actual = (r.getPing(9)*0.978)+0.317;
		return actual;
	}

	public static void TempTester(RXTXRobot r) {
        /*AnalogPin temp = r.getAnalogPin(3);
        double roomTemp = (temp.getValue()*-.15)+106.734;
        AnalogPin wind = r.getAnalogPin(2);
        double windTemp = (wind.getValue()*-.152)+102.778;

        //System.out.println("Temperature Sensor " + 3 + " has value: " + temp.getValue());
        //System.out.println("Wind Sensor " + 2 + " has value: " + wind.getValue());
        System.out.println("The temperature is " + roomTemp + " degrees Celsius.");
        System.out.println("The wind speed is "+Math.random()+ " KNT.");*/
		//analog values
		AnalogPin temp = r.getAnalogPin(3);
		AnalogPin wind = r.getAnalogPin(2);
		double difference = wind.getValue() - temp.getValue();

		//actual values
		double roomTemp = (temp.getValue()*-.15)+106.734;
		double windSpeed = (1.349 * Math.exp(-.027 * difference)) - 1;
		if(windSpeed < 0) windSpeed = 0;
		//Print out the actual values
		System.out.println("The temperature is " + roomTemp + " degrees Celsius.");
		System.out.println("The wind speed is " + windSpeed + " meters per second.");
	}

	public static double Conduct(RXTXRobot r) {
        /*r.getConductivity();
        AnalogPin conduct = r.getAnalogPin(5);
        double waterPercent = (conduct.getValue()*-.022)+23.335;    //This is the equation from the trend line
        System.out.println("The water percentage is " + waterPercent + "%");*/
        //THIS CODE NEEDS TO BE ADJUSTED TO NOT DROP THE PING PONG BALL IF THE THRESHOLD IS NOT REACHED
		//moveServoTime(r,7,1.0); //this still doesn't work
		double conduct = r.getConductivity();
		double waterPercent = (-1.79e-5 * Math.pow(conduct, 2)) - (1.387e-4 *conduct) +18.19;
		if(waterPercent < 0) waterPercent = 0;
		System.out.println("The water percentage is " + waterPercent + "%");
		return waterPercent;
	}
}
