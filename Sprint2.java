import rxtxrobot.*;

public class Sprint2 {
    public static void main(String args[])
    {
        RXTXRobot parkerAvenger = new ArduinoNano(); // Create RXTXRobot object
        parkerAvenger.setPort("/dev/tty.usbmodem1411"); // Set port to COM2
        parkerAvenger.connect();
        parkerAvenger.refreshAnalogPins(); // Cache the Analog pin information

        move3Meters(parkerAvenger);
        //moveServoAngle(parkerAvenger, 7);
        //Sonar(parkerAvenger);
        //Sonar(parkerAvenger);
        //BumpSensor(parkerAvenger);
        //TempTester(parkerAvenger);
        //moveDCMotor(parkerAvenger);

        parkerAvenger.close();

    }
    public static void moveDCMotor(RXTXRobot r)
    {
        r.attachMotor(RXTXRobot.MOTOR1, 6);
        r.runMotor(RXTXRobot.MOTOR1, 255, 3000);
    }
    public static void move3Meters(RXTXRobot r)
    {
        r.attachMotor(RXTXRobot.MOTOR1, 5);
        r.attachMotor(RXTXRobot.MOTOR2, 6);
        //have the encoders move the motor for approximately 32,740 ticks--rounded to 33 for the code
        r.runEncodedMotor(RXTXRobot.MOTOR1, 255, 3, RXTXRobot.MOTOR2, 255, 3);
    }
    public static void moveServoAngle(RXTXRobot r, int theta)
    {
        //Connect the servos to the Arduino to pin 9
        r.attachServo(RXTXRobot.SERVO1, 9);
        //Move the servo to the specified angle
        r.moveServo(RXTXRobot.SERVO2, theta); // Move Servo 2 to location 170
    }
    public static void Sonar(RXTXRobot r)
    {
        System.out.println("Response: " + r.getPing(12) + " cm"); //12 is the pin number
    }
    public static void BumpSensor(RXTXRobot r)
    {
        AnalogPin bump = r.getAnalogPin(11);
        while(bump.getValue() < 100)
        {
            bump = r.getAnalogPin(11);
            r.runEncodedMotor(RXTXRobot.MOTOR1, 255, 33);
        }
        r.runEncodedMotor(RXTXRobot.MOTOR1, 0, 0);
    }
    public static void TempTester(RXTXRobot r)
    {
        AnalogPin temp = r.getAnalogPin(10);
        System.out.println("Sensor " + 10 + " has value: " + temp.getValue());
    }
}
