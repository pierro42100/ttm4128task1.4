// This class defines a snmp agent
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class snmpAgent {

	public static void main(String[] args) {
	      
        long temps = 2000;                      // délai avant de répéter la tache : 2000 = 2 seconde
        long startTime = 0;                    // délai avant la mise en route (0 demarre immediatement)
        Timer timer = new Timer();             // création du timer
        TimerTask tache = new TimerTask() {     // création et spécification de la tache à effectuer
            @Override
                public void run() {
                    String line;
                    String value;
                    int valueInteger = 0;
                    //String value = 0;
                    //String[] cmd = {"bash","-c","ls"};
                    //String[] cmd = {"bash","-c","snmpget -v 2c -c ttm4128 127.0.0.1 sysContact.0"};
                    // Total RAM used: .1.3.6.1.4.1.2021.4.6.0 snmpget -v 2c -c ttm4128 127.0.0.1 UCD-SNMP-MIB::memAvailReal.0
                    String[] cmd = {"bash","-c","snmpget -v 2c -c ttm4128 127.0.0.1 ipInReceives.0"};
                    System.out.println("Hello, world!\n");
                    try {
                        Process p = Runtime.getRuntime().exec(cmd);
                        BufferedReader input =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
                        while ((line = input.readLine()) != null) {
                   
                            System.out.println(line);
                            //695428 kB, try to get only 695428
                            value = line.substring(line.lastIndexOf(" ")+1);
                            //lastWord = line.substring(line.lastIndexOf(" ")-1);
                            //System.out.println(value);
                            valueInteger = Integer.valueOf(value);
                            System.out.println(valueInteger);
                        }
                        input.close();
                    } catch (Exception e) {
                    	
                    }
                }
        };
        timer.scheduleAtFixedRate(tache,startTime,temps);  // ici on lance la mecanique
 
        }

}
