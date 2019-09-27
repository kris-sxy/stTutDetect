package npass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;
/*The main purpose of this thread is to receive the player input name and register, after the player successfully 
 * registered to  join the playerjoinlist, wait for 10 seconds, wait for the thread checkThread to select the 
 * player, after the player selected to start the game
 */
public class joingamethread implements Runnable {
	private Socket socket;
	private Logger logger;
	public joingamethread(Socket socket,Logger logger) {
		this.socket = socket;
		this.logger = logger;			
	}

	@Override
	public void run() {
		try {
		//Create input/output stream
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		//Player input name
		output.println("Welcom to the game,please enter your first name to register");
		String name = input.readLine();
		//Initialize players by name and join the playjoinlist
		player player = new player(socket, name);
		Server.playerjoinlist.add(player);
		player.state = "wait";
		//Send a successful registration message to the console
		output.println("Registered successfully, the system is counting the number of players. Please wait patiently. The game will start soon");
		logger.info("Player "+name+" registered successfully");
		System.out.println("Player "+name+" registered successfully");
		//Determine if you can play the game and process the game
		while(true) {
		//Wait 10 seconds for another thread to select the player	
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//If the player object is in the playerplaylist then the game can be played, otherwise wait the next round
		if(Server.playerplaylist.contains(player)) {
			//Print all players who can play this round
			for(int i =0;i<Server.playerplaylist.size();i++) {
				output.println("Player "+Server.playerplaylist.get(i).name+" has joined the "+Server.round+" round game");	
				logger.info("Player "+Server.playerplaylist.get(i).name+" has joined the "+Server.round+" round game");
				System.out.println("Player "+Server.playerplaylist.get(i).name+" has joined the "+Server.round+" round game");
			}
			output.println("game will start after 3 seconds");
			//The game will start after 3 seconds
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Print game rules
			output.println("The game has started, please enter number between 0 and 9 to guess the number, or press e to give up");
			String guessnumber = null;
			boolean guess = false;
			int count = player.number_of_guess;
			//The game process
			for(count = 1;count<5;count++) {
				guessnumber = input.readLine();
				logger.info("Player "+name+" guessed "+ guessnumber+" in the "+count+" time");
				System.out.println("Player "+name+" guessed "+ guessnumber+" in the "+count+" time");
				//Player input rule,player can only type 0-9 and e.
				while(!(guessnumber.equals("e"))&&!(guessnumber.equals("0"))&&!(guessnumber.equals("1"))&&!(guessnumber.equals("2"))&&!(guessnumber.equals("3"))&&!(guessnumber.equals("4"))&&!(guessnumber.equals("5"))&&!(guessnumber.equals("6"))&&!(guessnumber.equals("7"))&&!(guessnumber.equals("8"))&&!(guessnumber.equals("9"))) {
					output.println("Please enter 0-9 or e");
					guessnumber = input.readLine();
					
				}
				//player choose "e" to escaped,count the number of times the player has guessed and assign a value to the state attribute
				if(guessnumber.equals("e")) {
					output.println("You have escaped");
					guess = true;
					player.state = "escaped";
					player.number_of_guess = count;
					//Add player objects to playerresultlist
					Server.playerresultlist.add(player);
					break;
					
				}
				//game rule,give players hints
				if(Integer.parseInt(guessnumber)>Server.number) {
					output.println("The number entered is larger than the correct number");
				}
				//game rule,give players hints
				if(Integer.parseInt(guessnumber)<Server.number) {
					output.println("The number entered is smaller than the correct number");
					
					
				}
				//game rule,give players hints,count the number of times the player has guessed and assign a value to the attributes
				if(Integer.parseInt(guessnumber)==Server.number) {
					output.println("Well done. You guessed the number");
					guess = true;
					player.number_of_guess = count;
					player.state = "successful";
					////Add player objects to playerresultlist
					Server.playerresultlist.add(player);
					break;
	
					
				}
				
			}
			
			
			if(!guess) {
				output.println("Sorry, the maximum number of guesses has been reached. The correct number is "+Server.number);
				player.state = "unsuccessful";
				player.number_of_guess = 4;
				//Add player objects to playerresultlist
				Server.playerresultlist.add(player);
				
			}
			//The process after a round of play
			while(true) {
			//If the two lists differ in length, the player is prompted to wait for other players to complete the game	
			if(!(Server.playerresultlist.size()==Server.playerplaylist.size())) {
				output.println("Please wait for other players to finish the game. The ranking will be announced after all players finish the game");
				//Prompt the client every 5 seconds to keep alive
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
				
			}
			//If the two lists are the same length, print out all the player information and rank them according to the number of guesses
			if(Server.playerresultlist.size()==Server.playerplaylist.size()) {
				output.println("All players have completed the game, ranked as follows");
				logger.info("All players have completed the "+Server.round+" round game");
				//Using sort method
				Collections.sort(Server.playerresultlist);
				//Looping print player info from playerresultlist
				for(int i=0;i<Server.playerresultlist.size();i++) {
					output.println("Player "+Server.playerresultlist.get(i).name+" guess "+Server.playerresultlist.get(i).number_of_guess+" time "+Server.playerresultlist.get(i).state);
					logger.info("Player "+Server.playerresultlist.get(i).name+" guess "+Server.playerresultlist.get(i).number_of_guess+" time "+Server.playerresultlist.get(i).state);
				}
				//Clear the list after 6 seconds for the next round
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Server.playerresultlist.clear();
				Server.playerplaylist.clear();
				//Prompt players
				output.println("Enter p to play again or enter q to quit");
				String qorp = input.readLine();
				//Add players to the playerjoinlist for the next round of play
				if(qorp.equals("p")) {
					Server.playerjoinlist.add(player);
					player.state = "wait";
					output.println("You've already started lining up again");
					logger.info("Player "+player.name+ "already started lining up again");
					break;
				}
				//game over
				if(qorp.equals("q")) {
					output.println("Game Over");
					logger.info("Player "+player.name+ " choose game over");
					break;
					
					
				}
				
			}}}
		else {
			//If the player is not selected, give a hint every 10 seconds
			output.println("One round is in progress, please wait for the next round");
		}}	
			}
	
		catch(IOException e1) {
			
		}
		
	}

}
/*The purpose of this thread is to select players who meet the rules of the game and add them from A to B*/
class checkthread implements Runnable {
	@Override
	public void run() {
		while(true) {
		//choose players every five seconds	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Select the player's rules, playerplaylist is empty, playerjoinlist is not empty
		if(Server.playerplaylist.size()==0&&!(Server.playerjoinlist.size()==0)) {
			Server.round++;
			//Create a random number to guess(0-9)
			Server.number = new Random().nextInt(10);
			//Select the player's rules
			if(Server.playerjoinlist.size()==1) {
				Server.playerplaylist.add(Server.playerjoinlist.get(0));
				Server.playerjoinlist.remove(0);
			}
			//Select the player's rules
			if(Server.playerjoinlist.size()==2) {
				Server.playerplaylist.add(Server.playerjoinlist.get(0));
				Server.playerplaylist.add(Server.playerjoinlist.get(1));
				Server.playerjoinlist.remove(0);
				Server.playerjoinlist.remove(0);
			}
			//Select the player's rules
			if(Server.playerjoinlist.size()>=3) {
				Server.playerplaylist.add(Server.playerjoinlist.get(0));
				Server.playerplaylist.add(Server.playerjoinlist.get(1));
				Server.playerplaylist.add(Server.playerjoinlist.get(2));
				Server.playerjoinlist.remove(0);
				Server.playerjoinlist.remove(0);
				Server.playerjoinlist.remove(0);
			}
			}
	}}
	
}

