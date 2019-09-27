package npass;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	

	public static void main(String[] args) throws IOException, InterruptedException {
		//Enter the server address to connect to the server
		System.out.println("Please input the server address:");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String serverAddress = reader.readLine();
		Socket socket = new Socket(serverAddress, 61321);
		Client gamec = new Client();
		while(true) {
		//To start the threads
		receivethread receive = new receivethread(socket,gamec);
		Thread receivethread = new Thread(receive);
		sendthread send = new sendthread(socket,gamec);
		Thread sendthread = new Thread(send);
		sendthread.start();
		receivethread.start();
		//Make sure the last thread ends before starting a new one
		sendthread.join();
		receivethread.join();
		}
		

	}
	//The method of receiving information
	public String acceptmessage(Socket socket,BufferedReader input) throws IOException {
		String message = input.readLine();
		return message;
		}
	//The method of sending information
	public void sendmessage(Socket socket,PrintWriter output,String message) throws IOException {
		output.println(message);
		}
	

}

//The thread that receives the message
class receivethread implements Runnable {
	private Socket socket;
	private Client gamec;
	public receivethread(Socket socket,Client gamec) {
		this.socket = socket;
		this.gamec = gamec;
		
		
	}

	@Override
	public void run() {
		//The loop receives the message from the server and prints it
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
			String receive = input.readLine();
			System.out.println(receive);}
			
		
	
		}catch(IOException e1) {
			
		}
		
		
	}}
//The thread that sent the message
class sendthread implements Runnable {
	private Socket socket;
	private Client gamec;
	public sendthread(Socket socket,Client gamec) {
		this.socket = socket;
		this.gamec = gamec;
		
		
	}

	@Override
	public void run() {
		try {
		//Send a message to the server 
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
		 while (true) {
             String s = input.readLine();
             output.println(s);
             //when "q" is sent, the client exits the program
             if (s.equals("q")) {
            	 System.exit(0);
             };
         }
		}catch(IOException e1) {
			
		}
		
		
	}
	
}


