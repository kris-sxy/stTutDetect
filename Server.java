package npass;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.Date;
import java.util.logging.*;

public class Server {
	protected static CopyOnWriteArrayList<player> playerjoinlist;
	protected static CopyOnWriteArrayList<player> playerplaylist;
	protected static ArrayList<player> playerresultlist;
	protected BufferedReader input;
	protected PrintWriter output;
	protected static int number;
	protected static int round = 0;
	static Logger logger;
	
	public static void main(String[] args) throws IOException {
		serverLogger();
		System.out.println("Server running");
		logger.info("Server running");
		//Define the port
		ServerSocket listener = new ServerSocket(61321);
		//Store players waiting for the game
		playerjoinlist = new CopyOnWriteArrayList<>();
		//Store players who have already started the game
		playerplaylist= new CopyOnWriteArrayList<>();
		//Store player rankings
		playerresultlist= new ArrayList<>();
		while(true) {
		//Cycle to monitor
		Socket socket = listener.accept();
		//Starting threads
		joingamethread joingamethread = new joingamethread(socket,logger);
		Thread join = new Thread(joingamethread);
		checkthread checkthread = new checkthread();
		Thread check = new Thread(checkthread);
		check.start();
		join.start();
		}		
	}
	//The method of receiving a message
	public String acceptmessage(Socket socket, BufferedReader input) throws IOException {
		String message = input.readLine();
		return message;
	}
    //The method of sending a message
	public void sendmessage(Socket socket, PrintWriter output, String message) throws IOException {
		output.println(message);
	}
	 //Configure log information
	 private static void serverLogger() throws IOException{
	        logger = Logger.getLogger("Server");
	        logger.setLevel(Level.INFO);
	        FileHandler fileHandler = new FileHandler(new SimpleDateFormat
	                ("dd-MM-yyyy").format(new Date())+"-Server.log", true);

	        fileHandler.setLevel(Level.ALL);
	        fileHandler.setFormatter(new LoggerFormatter());
	        logger.addHandler(fileHandler);
	        logger.setUseParentHandlers(false);
	    }// end of gameLogger
	

}
//Create log form
class LoggerFormatter extends Formatter {
    @Override
    public String format(LogRecord logRecord) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[" + new Date() + "]" + " [" + logRecord.getLevel() + "] ");
        stringBuilder.append("| " + logRecord.getLoggerName() + " | "+ ": " + logRecord.getMessage() + "\n");
        return stringBuilder.toString();
    }//end of format
}// end of LoggerFormatter
