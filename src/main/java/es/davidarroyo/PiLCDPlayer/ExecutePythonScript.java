package es.davidarroyo.PiLCDPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecutePythonScript {
	
	public static void executeCommand(String videoTitle, String lcdEnabled, String address, String port) {
		String command = "python /usr/local/bin/pilcd.py -e " + lcdEnabled + " -a " + address + " -p " + port
				+ " -t " + videoTitle;

		String output = executeCommand(command);

		System.out.println(output);
	}
	
	private static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return output.toString();

	}

}
