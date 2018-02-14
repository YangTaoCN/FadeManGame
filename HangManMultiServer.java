/**
 * HangManNultiServer.java
 * 
 * Version:    1.4
 * 
 * Revisions:
 * 			   1.0
 * 			   1.1 Use Array to show the scene	
 * 			   1.2 Add some hints
 * 			   1.3 Run as Server
 * 			   1.4 Run as Server which can support multiple clients
 * 
 * 
 */
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class implement one version of Hangman game.
 * 
 * 
 * @author    Tao Yang   txy2539@g.rit.edu
 * @author    Su, Hao    hs2236@g.rit.edu
 *
 */

public class HangManMultiServer implements Runnable{
	String route = "";
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	/**
	 * Constructor
	 * @param s
	 * @throws IOException
	 */
	
	HangManMultiServer( Socket s ) throws IOException{
		 socket = s;
		 out = new PrintWriter( socket.getOutputStream(), true );
		 in = new BufferedReader( 
				 new InputStreamReader( 
						 socket.getInputStream() ) );
	}
	
	/**
	 * Override run 
	 */
	
	public void run() {
		try {
			game();
		} catch (Exception e) {
			System.out.println(socket.getPort() + 
					" quit, without finish game!");
			try {
				socket.close();
			} catch (IOException e1) {
				
			}
			out.close();
			try {
				in.close();
			} catch (IOException e1) {
				
			}
			return;
		}
	}
	
	/**
	 * This method start a new game. And estimate whether game is over.
	 * @throws IOException 
	 * 
	 */
	
	public void game() throws IOException {
		
		boolean gameover = false;
		String getInput = new String();
		
		// Use scanner to get the input strings 
		
		
		int wrongTimes = 0;
		int rightTimes = 0;
		
		// If static variable "route" is empty, inquiry the dictionary route
		
		if ( route.equals("") ) {
			
			out.println("Which dictionary do you want to use?\n"
					+ "Press enter to use default dictionary, if lucky...");
			out.println("$over$");
			getInput = in.readLine();
			route = getInput;
		}
		
		// pick a word from dictionary randomly
		
		String word = pick();
		word = word.toLowerCase();
		char[] wordChar = word.toCharArray();
		char[] right = new char[word.length()];
		char[] wrong = new char[9];
		
		// This part repeat when wrongTimes is less or equal to 9
		
		while (wrongTimes <= 9) {
			
			// Print the corresponding faded scene.
			
			fade(wrongTimes);
			
			// Finish records the status of success.
			
	 		boolean finish = true;
			for (int i = 0; i < wordChar.length; i++) {
				if ( isInArray( right, wordChar[ i ] )) {
					out.print( wordChar[ i ] + " " );
				} else {
					out.print( "_ " );
					finish = false;
				}					
			}
			out.print("\n");
			
			// Provide a hint for wrong character used.
			
			if ( wrongTimes >= 1 ) {
				out.print( "Wrong characters: ");
				out.println( wrong );
			}
			
			// To estimate the game process:Win or game over
			
			if (wrongTimes == 9) {
				gameover = true;
				out.println("GAME OVER");
				out.println("The word was: " + word );	
			} else if ( finish == true ) {
				gameover = true;
				out.println("Bingo! Congrats!");
			}
			
			// Let user choose to continue or not until getting correct input.
			
			while ( gameover == true && (!getInput.equals("yes") 
										&&!getInput.equals("no"))) {
				out.println("Do you want to continue (YES/NO)?");
				out.println("$over$");
				getInput = in.readLine();
				getInput = getInput.toLowerCase();
				
				if( getInput.equals("yes")) {
					game();
					return;
				} else if (getInput.equals("no")) {
					System.out.println(socket.getPort()+" client quit!");
					in.close();
					out.close();
					socket.close();
					
					return;
				} else {
					out.println("Don't be naughty!");
				}
			}
			
			out.println( "Which character is in the word?" );
			out.println( "Please type only one character and enter!" );
			
			char[] toChar;
			
			// Check the input, if illegal, re-enter, until legal.
			
			do { 
				out.println("$over$");
				getInput = in.readLine();
				
				// Uniform all the char with lower case.
				
				getInput = getInput.toLowerCase();
				toChar = getInput.toCharArray();
				if (toChar.length != 1 
						|| (toChar[ 0 ] < 'a' || toChar[ 0 ] > 'z')) {
						out.println("One character a-z please!");
				} else if (isInArray( wrong, toChar[ 0 ])
							   || isInArray( right, toChar[ 0 ])) {
						out.println("Used Character! Try again!");
				}
			}while ( toChar.length != 1 
					||(toChar[ 0 ] < 'a' || toChar[ 0 ] > 'z' )
					|| isInArray( wrong, toChar[ 0 ])
					|| isInArray( right, toChar[ 0 ] ));
			
			//Hint after every round and record the chars used
			
			if ( isInArray( wordChar, toChar[0]) ) {
				right[ rightTimes ] = toChar[0];
				rightTimes++;
				out.println("Good! You hit one!");
			} else {
				wrong[ wrongTimes ] = toChar[0];
				wrongTimes++;
				out.println("Bad luck...");
				out.println( 9-wrongTimes + " times left");
			}
			
		}
	}
	
	/**
	 * Draw the scene using scanner by printing out # the line one by one.
	 *
	 * @param times
	 * 
	 */
	void fade(int wrongTimes){
		int startLine = wrongTimes;
		String[] scene = {
		    "        ###",
			"        ###",
			"         #",
			"       #####",
			"      # ### #",
			"        ###",
			"       #  #",
			"      #    #",
			"     ##    ##",
			"########################",
			"##                    ##",
			"##                    ##",
		};
			      
			
		for ( int i = 0; i <11; i++ ) {
			if ( i >= startLine ) {
				//System.out.println( scene[i] );
				out.println( scene[i] );
			} else {
				//System.out.println( "" );
				out.println( "" );
			}
		}
	}
		
	/**
	 * This method use scanner to count and pick words in object file.
	 * Given a default file route by direct enter. 
	 * 
	 * @return return random word
	 * @throws IOException 
	 */
	
	public String pick() throws IOException{
		String selecWord = new String();
		if (route.equals("")) {
			route = "dic.txt";
		}
		
		File dic = new File(route);
		
		// Two scanners, one is for counting, the other is for picking.
		
		Scanner countWord = null;
		Scanner pickWord = null;
		try {
			countWord = new Scanner( dic );
			pickWord = new Scanner (dic);
			out.println( "Let's started with " + dic );
		
		// If file not found, execute hint and reload game();
			
		} catch (Exception e) {
			//System.out.println( "File not found..." );
			out.println( "File not found..." );
			route = "";
			out.println( "A demo will be provided..." );
			return "hello";
			
		}
		
		// Get the sum of words
		
		int wordSum = 0;
		while(countWord.hasNextLine()) {
			wordSum++;
			countWord.nextLine();
		}
		
		// Get a random number within words' sum. +1 in case of 0.
		
		Random getRand = new Random();
		int randomNum = getRand.nextInt( wordSum ) + 1;
		int count = 0;
		
		// Pink the word at random position
		
		while(pickWord.hasNextLine()) {
			count++;
			if (count == randomNum) {
				selecWord = pickWord.nextLine();
			} else {
				pickWord.nextLine();
			}	
		}
		
		// close the scanner to save memory.
		
		countWord.close();
		pickWord.close();
		return selecWord;
	}
	
	/**
	 * This method tell whether input is in array set.
	 * 
	 * @param set	 Object char array
	 * @param input  any char input
	 * @return		 return true or false
	 */
	
	boolean isInArray(char[] set, char input) {
		boolean inOrNot = false;
		for ( int i = 0; i < set.length; i++ ) {
			if ( set[ i ] == input ) {
				inOrNot = true;
			} 
		}
		return inOrNot;
	}
	
	/**
	 * The main program. Print the opening remarks.
	 * Call method game to start the game.
	 * 
	 * @param argus command line arguments (ignored)
	 * @throws IOException 
	 */
	
	public static void main(String argus[]) throws IOException {
		
		ServerSocket s = new ServerSocket(8888);
		System.out.println(InetAddress.getLocalHost()+"," + s.getLocalPort());
		System.out.println("waiting for client");
		int count = 0;
		while(true) {
			Socket server = s.accept();
			new PrintWriter(server.getOutputStream(), true).println(
					"Welcome to hangman game!\n"
					+ "Let's get started!");
			System.out.println("client "+ server.getInetAddress()+" online");
			Thread newGame  = new Thread( new HangManMultiServer(server));
			newGame.start();
			System.out.println( ++count +" game start");
		}
		
	}
	
}//HangManMultiServer
