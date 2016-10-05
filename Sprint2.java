import rxtxrobot.*;

public class Sprint2 {
    public static void main(String args[])
    {
        RXTXRobot parkerAvenger = new ArduinoNano(); // Create RXTXRobot object
        parkerAvenger.setPort("/dev/ttyS80"); // Set port to COM2
        parkerAvenger.connect();
        parkerAvenger.refreshAnalogPins(); // Cache the Analog pin information

	//turn(parkerAvenger);
        //raiseBoom(parkerAvenger);
        move3Meters(parkerAvenger);
        //moveServoAngle(parkerAvenger, 90);
        //Sonar(parkerAvenger);
        //Sonar(parkerAvenger);
        //BumpSensor(parkerAvenger);
        //TempTester(parkerAvenger);
        //getThermistorReading(parkerAvenger);

        parkerAvenger.close();

    }

    public static void turn(RXTXRobot r)
    {
	r.attachMotor(RXTXRobot.MOTOR1, 5);
	r.attachMotor(RXTXRobot.MOTOR2, 6);
	r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, -255, 4600);
    }
    public static void raiseBoom(RXTXRobot r)
    {
        //Connect the servos to the Arduino to pin 8
        r.attachServo(RXTXRobot.SERVO1, 8);
        r.moveServo(RXTXRobot.SERVO1, 90);
        //Move the servo a certain amount of time to raise the boom
        r.moveServo(RXTXRobot.SERVO1, 179);
        r.sleep(5000);
        r.moveServo(RXTXRobot.SERVO1, 90);
    }
    public static void move3Meters(RXTXRobot r)
    {
        //Attach motors on pins 5 and 6 and move them forward for an amount of time we will tune for 3 meters
        r.attachMotor(RXTXRobot.MOTOR1, 5);
        r.attachMotor(RXTXRobot.MOTOR2, 6);
        r.runMotor(RXTXRobot.MOTOR1, 255, RXTXRobot.MOTOR2, 255, 4600);
    }
    public static void moveServoAngle(RXTXRobot r, int theta)
    {
        //Connect the servos to the Arduino to pin 9
        r.attachServo(RXTXRobot.SERVO1, 7);
	//r.sleep(10000);
        //Move the servo to the specified angle
        r.moveServo(RXTXRobot.SERVO1, 0);
	r.sleep(1000);
	r.moveServo(RXTXRobot.SERVO1, 30);
	r.sleep(1000);
	r.moveServo(RXTXRobot.SERVO1, 180);
	r.sleep(1000);
	//r.sleep(20000);
	//r.moveServo(RXTXRobot.SERVO1, 0);
    }
    public static void Sonar(RXTXRobot r)
    {
        //Take a distance reading - not working at all
        System.out.println("Response: " + r.getPing(9) + " cm");
    }
    public static void BumpSensor(RXTXRobot r)
    {
        AnalogPin bump = r.getAnalogPin(0);
        r.attachMotor(RXTXRobot.MOTOR1, 5);
        r.attachMotor(RXTXRobot.MOTOR2, 6);
        while (bump.getValue() > 1000) {
	    r.refreshAnalogPins();
            bump = r.getAnalogPin(0);
            r.runMotor(RXTXRobot.MOTOR1, 100, RXTXRobot.MOTOR2, 100, 0);
        }
        r.runMotor(RXTXRobot.MOTOR1, 100, RXTXRobot.MOTOR2, 0, 0);
    }
    public static void TempTester(RXTXRobot r)
    {
        AnalogPin temp = r.getAnalogPin(10);
        System.out.println("Sensor " + 10 + " has value: " + temp.getValue());
    }
    //This function reads the voltage coming into analog pin 0 ten times //takes the average, then returns the result.
    /*public static int getThermistorReading(RXTXRobot r) {
        int sum = 0;         int readingCount = 10;                 //Read the analog pin values ten times, adding to sum each time         for (int i = 0; i < readingCount; i++) {
        //Refresh the analog pins so we get new readings
        r.refreshAnalogPins();             int reading = r.getAnalogPin(0).getValue();             sum += reading;         }                 //Return the average reading         return sum / readingCount;
        }*/
}
