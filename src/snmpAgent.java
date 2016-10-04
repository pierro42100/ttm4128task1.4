// This class defines a snmp agent
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;




public class snmpAgent {

	
	public static void main(String[] args) {
	     
		////////////////////////////////////////////////////////////////////////////////////////
		
        long temps = 2000;                     // time before repeating the task : 2000 = 2 secondes
        long startTime = 0;                    // time before starting the task (0 : immediat start)
        Timer timer = new Timer();             // timer creation
        TimerTask tache = new TimerTask() {    // timer task creation and specification of what will be done
            @Override
            
                public void run() {
                    String line; 
                    String value;
                    int valueInteger = 0;
                    // String value = 0;
                    // String[] cmd = {"bash","-c","ls"};
                    // String[] cmd = {"bash","-c","snmpget -v 2c -c ttm4128 127.0.0.1 sysContact.0"};
                    // Total RAM used: .1.3.6.1.4.1.2021.4.6.0 snmpget -v 2c -c ttm4128 127.0.0.1 UCD-SNMP-MIB::memAvailReal.0
                    
                    String commandline = "snmpget -v 2c -c ttm4128 127.0.0.1 ipInReceives.0";
                    // First data :
                    String[] cmd = {"bash","-c",commandline};
                    
                    // Second data :
                    // String[] cmd = {"bash","-c","snmpget -v 2c -c ttm4128 127.0.0.1 ipInDelivers.0"};
                    
                    System.out.println("Hello, world!\n"); //test
                    try { 
                        Process p = Runtime.getRuntime().exec(cmd);
                        BufferedReader input =
                            new BufferedReader(new InputStreamReader(p.getInputStream())); 
                        while ((line = input.readLine()) != null) { // Read data
                   
                            System.out.println(line);
                            value = line.substring(line.lastIndexOf(" ")+1); // Get the value
                            //lastWord = line.substring(line.lastIndexOf(" ")-1);
                            //System.out.println(value);
                            valueInteger = Integer.valueOf(value); // convert string into integer
                            System.out.println(valueInteger); // print the integer value
                            
                            // Write value in text file
                            write(value + "\n"); //EOL
                            
                            // Just a test
                            if(valueInteger > 14712500){
                            	commandline = "snmptrap -v 2c -c ttm4128 127.0.0.1 \"\" NTNU-NOTIFICATION-MIB::anotif anotif s \"here\"";
                            	cmd[2] = commandline;
                                
                            	Process r = Runtime.getRuntime().exec(cmd);
                            }
                            
                            // if > treshold (treshold for 5-10 traps in 15 minutes)
                            // --> snmptrap	
                            // else
                            // nothing
                            
                        }
                        input.close();
                    } catch (Exception e) {
                    	
                    }
                }
        };
        timer.scheduleAtFixedRate(tache,startTime,temps);  // beginning of the mechanism
 
        }
	
	
	
		public static void write(String text) {
		try {
			
			File file = new File("./values.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true); //the true will append the new data
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
