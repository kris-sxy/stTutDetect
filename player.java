
package npass;
import java.net.*;

public class player implements Comparable<player> {
	protected Socket socket;
	protected String name;
	protected int number_of_guess;
	protected int rank;
	protected String state;
	protected int number;
	public player(Socket socket, String name) {
		this.socket = socket;
		this.name = name;
		this.number_of_guess = 1;
		this.state = "wait";
	
		
	}
	public player() {
		
	}
	@Override
	//Sorting rule
	public int compareTo(player o) {
		// TODO Auto-generated method stub
		return this.number_of_guess - o.number_of_guess;
	}
}
