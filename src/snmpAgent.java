// This class defines a snmp agent
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class snmpAgent {


	public static void main(String[] args) {


		// Variables used for the timer
		long temps = 60000;                    // time before repeating the task : 60000 = 60 secondes
		long startTime = 0;                    // time before starting the task (0 : immediate start)
		Timer timer = new Timer();             // timer creation
		TimerTask tache = new TimerTask() {    // timer task creation and specification of what will be done
			@Override

			public void run() {

				////////////////////////////////////////
				// VARIABLES
				////////////////////////////////////////

				int valueInteger1 = 0; // variable used to store the value 1 found as an Integer

				int valueInteger2 = 0; // variable used to store the value 2 found as an Integer

				// command that will be executed
				//String commandline = "snmpget -v 2c -c ttm4128 127.0.0.1 ipInReceives.0"; 
				String commandline = "snmpget -v 2c -c ttm4128 127.0.0.1 ipInReceives.0"; //138.68.88.225
				
				// First data : total number of input datagrams (IPv4) received from interfaces, including those received in error
				String[] cmd = {"bash","-c",commandline}; // String array used by the exec method

				////////////////////////////////////////
				// START
				////////////////////////////////////////

				System.out.println("Gathering Informations\n"); // printing a text in the terminal

				// get the value of total number of input datagrams (IPv4) received from interfaces, including those received in error
				valueInteger1 = get(cmd);
				
				// Write value in text file by calling the method write
				write(valueInteger1 + "\n");
				
				System.out.println("value 1 : " + valueInteger1);

				// definition of the threshold                            
				int threshold = 449789;

				// if the value is bigger than the threshold then the trap must be sent with the two informations
				if(valueInteger1 > threshold){
					
					// get the second value needed : total number of input datagrams successfully delivered to IPv4 user-protocols (including ICMP)
					cmd[2] = "snmpget -v 2c -c ttm4128 127.0.0.1 ipInDelivers.0";                   
					valueInteger2 = get(cmd);
					
					System.out.println("value 2 : " + valueInteger2);
					
					// send the trap with the first value
					commandline = "snmptrap -v 2c -c ttm4128 127.0.0.1 \"\" NTNU-NOTIFICATION-MIB::anotif SNMPv2-MIB::ipInReceives.0 s \"" + valueInteger1 + "\"";
										
					cmd[2] = commandline;
					
					try{
						Process r = Runtime.getRuntime().exec(cmd); // execute the cmd
					}
					catch (Exception e){
						e.printStackTrace();
					}

					// snmptrap -v 2c -c ttm4128 127.0.0.1 "" NTNU-NOTIFICATION-MIB::anotif anotif s "Notification" ipInReceives.0 counter32 "87654" ipInDelivers.0 counter32 "1234"
					// send the trap with the second values
					commandline = "snmptrap -v 2c -c ttm4128 127.0.0.1 \"\" NTNU-NOTIFICATION-MIB::anotif anotif s \"Notification\" ipInReceives.0 counter32 \"" + valueInteger1 + "\" ipInDelivers.0 counter32 \"" + valueInteger2  + "\"";
										
					cmd[2] = commandline;
					
					try{
						Process r = Runtime.getRuntime().exec(cmd); // execute the cmd
					}
					catch (Exception e){
						e.printStackTrace();
					}

				}

			}


		};
		timer.scheduleAtFixedRate(tache,startTime,temps);  // beginning of the mechanism

	}


	// this method is used to create and write the data into a text file.
	public static void write(String text) {
		try {

			File file = new File("./values.txt");

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true); // true --> will append the new data at the end of the file
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text); // write the value
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// this method is used to get and return the value wanted as an integer
	public static int get(String[] command) {

		String line; // variable used to store the result text
		int value = 0;
		
		// let try to get the value
		try { 
			Process p = Runtime.getRuntime().exec(command); // execute the cmd
			//System.out.println(command[2] + "\n \n");
			
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream())); // definition of a new buffer reader
			
			while ((line = input.readLine()) != null) {// Read data as long as there is data to read
								
				//System.out.println(line); // printing the gathered information

				value = Integer.valueOf(line.substring(line.lastIndexOf(" ")+1)); // extract the value from the string

			}
			input.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}


}
