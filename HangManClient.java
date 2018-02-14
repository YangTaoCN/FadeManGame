/**
 * HangManMultiClient.java
 * 
 * Version:    1.1
 * 
 * Revisions:
 * 			   1.0
 * 			   1.1 Handle more exceptions
 * 
 * 
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * This class communicate with server start hang-man game
 * 
 * @author    Tao Yang   txy2539@g.rit.edu
 * @author    Su, Hao    hs2236@g.rit.edu
 *
 */


public class HangManClient {
	
	/**
	 * Main method
	 * @param args
	 * @throws IOException
	 */
	
	public static void main ( String[] args) throws IOException {
		Socket s;
		try {
			s = new Socket("localhost",8888);
			//s = new Socket("129.21.22.196",8888);
			PrintWriter out = new PrintWriter( s.getOutputStream(), true );
			BufferedReader in = new BufferedReader( 
					new InputStreamReader( 
							s.getInputStream() ));
			Scanner input = new Scanner( System.in ); 
			System.out.println("using server port :"+ s.getPort()+ 
							"\nClient port " + s.getLocalPort());
			String show;
			
			// receive and send message
			
			while(true) {
				try {
					while ( !(show = in.readLine()).equals("$over$")) {
						System.out.println(show);
					}
					String w = input.nextLine();
					out.println(w);	
				}catch( SocketException e) {
					System.out.println("server problem,bye-bye");	
					out.close();
					in.close();
					s.close();
					System.exit(0);
				}catch( Exception e) {
					System.out.println("BYB-BYE!");
					out.close();
					in.close();
					s.close();
					System.exit(0);
				}
			}
		} catch (Exception e1) {
			System.out.print("Server is not ready, come back later!");
			System.exit(0);
		}
	}
}// HangManMultiClient
