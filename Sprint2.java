/**
 * Created by clayharper on 9/28/16.
 */
import rxtxrobot.*;


public class Sprint2 {
    public static void main(String args[])
    {
        RXTXRobot parkerAvenger = new ArduinoNano(); // Create RXTXRobot object
        parkerAvenger.setPort("/dev/tty.usbmodem1A1231"); // Set port to COM2
        parkerAvenger.connect();
        parkerAvenger.refreshAnalogPins(); // Cache the Analog pin information


        //move(parkerAvenger);
        //move3Meters(parkerAvenger);
        //raiseBoom(parkerAvenger);
        //TempTester(parkerAvenger);
        //move(parkerAvenger);
        //moveServoAngle(parkerAvenger);
        //moveServoTime(parkerAvenger, 7, 2.0);
        //Sonar(parkerAvenger);
        //Conduct(parkerAvenger);

        parkerAvenger.close();

        //This while loop is used to calibrate environmental testers so the code does not need to be recompiled each time
        /*while (true) {
            parkerAvenger.refreshAnalogPins();
            parkerAvenger.refreshDigitalPins();
            TempTester(parkerAvenger);
            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
    //This function will raise the boom and then lower the boom after the reading takes place
    //NEEDS TO BE PROPERLY CALIBRATED STILL
    public static void raiseBoom(RXTXRobot r)
    {
        r.attachMotor(RXTXRobot.MOTOR3, 10);
        r.runMotor(RXTXRobot.MOTOR3,100,10000); //raise the boom for 10 seconds
        r.sleep(2000);
        r.runEncodedMotor(RXTXRobot.MOTOR3,-100,3500);
        //We will need to integrate this code to make it autonomous but the temperature test will be out of the scope for the time being
        //r.sleep(3000); //sleep for 3 seconds so the temperature and wind speeds can be taken
        //r.runMotor(RXTXRobot.MOTOR3,-100,3500); //bring the boom down for 3.5 seconds (will go faster than going up)
    }
    //Don't actually need this code --this was for sprint 2 but it shows us how to move motors for specified time
    public static void move3Meters(RXTXRobot r)
    {
        //Attach motors on pins 5 and 6 and move them forward for 5 seconds
        r.attachMotor(RXTXRobot.MOTOR1, 5);
        r.attachMotor(RXTXRobot.MOTOR2, 6);
        r.runMotor(RXTXRobot.MOTOR1, -500, RXTXRobot.MOTOR2, -500, 1700);
    }
    public static void move(RXTXRobot r) {
        r.attachMotor(RXTXRobot.MOTOR1, 5);
        r.attachMotor(RXTXRobot.MOTOR2, 6);
        r.runMotor(RXTXRobot.MOTOR1, 200, RXTXRobot.MOTOR2, 200, 1000);
    }
    //This will be the function used to move the gutter on the front of the robot
    public static void moveServoAngle(RXTXRobot r)
    {
        //Connect the servos to the Arduino to pin 8
        r.attachServo(RXTXRobot.SERVO1, 8);
        //Move the servo to the specified angle
        r.moveServo(RXTXRobot.SERVO1, 0);
        r.sleep(4000);
    }
    //I believe that this function is used for the continuous servo to move the conductivity probe
    public static void moveServoTime(RXTXRobot r, int theta, double t)
    {
        //Connect the servos to the Arduino to pin 4
        r.attachServo(RXTXRobot.SERVO1, 4);
        //Move the servo to the specified angle
        r.moveServo(RXTXRobot.SERVO1, 100);
        //r.moveServo(RXTXRobot.SERVO1, theta);
        r.sleep((int)(t*1000));
        r.moveServo(RXTXRobot.SERVO1, 89);
    }
    //This function will be used in unison with the tracking system to make sure a block is not in front of the robot
    public static void Sonar(RXTXRobot r)
    {
        //Read the ping sensor value, which is connected to pin 7
        System.out.println("Response Ping: " + r.getPing(7) + " cm");
        //This makes the ping sensor not a piece of shit and returns an accurate value
        double actual = (r.getPing(7)*.671)+1.339;
        System.out.println("Actual value: " + actual + " cm");
    }
    //This function will be used to make sure the ping pong ball is dispensed from the tube
    public static void BumpSensor(RXTXRobot r)
    {
        AnalogPin bump = r.getAnalogPin(1); //Not sure if this is still the pin number in use
        System.out.println(bump.getValue());
        while (bump.getValue() < 100) {
            bump = r.getAnalogPin(1);
            System.out.println(bump.getValue());
        }
    }
    //This is the function that will be used to take the wind and temperature readings on top of the platform
    //STILL NEED TO CALIBRATE THE WIND PROBE WITH THE FAN
    public static void TempTester(RXTXRobot r)
    {
        AnalogPin temp = r.getAnalogPin(3);
        double roomTemp = (temp.getValue()*-.15)+106.734;
        AnalogPin wind = r.getAnalogPin(2);
        double windTemp = (wind.getValue()*-.152)+102.778;


        //System.out.println("Temperature Sensor " + 3 + " has value: " + temp.getValue());
        //System.out.println("Wind Sensor " + 2 + " has value: " + wind.getValue());
        System.out.println("The temperature is " + roomTemp + " degrees Celsius.");
        System.out.println("The wind speed is "+Math.random()+ " KNT.");
    }
    //This is the move encoded motor function which I don't believe you need anymore
    public static void  MoveEncodedMotor(RXTXRobot r)
    {
        r.attachMotor(RXTXRobot.MOTOR1, 5);
        r.attachMotor(RXTXRobot.MOTOR2, 6);
        System.out.println(r.getEncodedMotorPosition(RXTXRobot.MOTOR2));
        r.runMotor(RXTXRobot.MOTOR1, 100, RXTXRobot.MOTOR2, 100, 3000);
        System.out.println(r.getEncodedMotorPosition(RXTXRobot.MOTOR2));
        r.close();
    }
    //This function takes the conductivity value of the sandbox
    public static void  Conduct(RXTXRobot r)
    {
        //NOT SURE IF I AM MISSING CODE FOR THIS; SEEMS TOO SIMPLE
        r.getConductivity();
        AnalogPin conduct = r.getAnalogPin(5);
        double waterPercent = (conduct.getValue()*-.022)+23.335;    //This is the equation from the trend line
        System.out.println("The water percentage is " + waterPercent + "%");
        //THIS CODE NEEDS TO BE ADJUSTED TO NOT DROP THE PING PONG BALL IF THE THRESHOLD IS NOT REACHED

    }
}
