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
		position.go(0,30,0);
		theRobot.sleep(3);
		position.go(0,0,90);

		//Close connection
		theRobot.close();
}
