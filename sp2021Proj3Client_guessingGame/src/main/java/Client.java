import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;
import java.util.function.Consumer;

import javafx.application.Platform;

public class Client extends Thread {
	
	Socket connection;
	
	// I/O Streams
	ObjectOutputStream outStream;
	ObjectInputStream inStream;
	
	Vector<String> categories;
	String successfulSubmit,
		   minorWinState,
		   majorWinState,
		   currWord,
		   currHint;
		   
	
	Integer correctWords = 0,
			incorrectWords = 0,
			guesses,
		    port;
	String ip;
	
	
	// Funct. interface that 
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call, String ip, String port){
		callback = call;
		this.ip = ip;
		this.port = Integer.parseInt(port);
		categories = new Vector<String>();
	}
	
	public void run() {
		
		try {
			connection = new Socket(ip, port);
		    outStream = new ObjectOutputStream(connection.getOutputStream());
		    inStream = new ObjectInputStream(connection.getInputStream());
		    connection.setTcpNoDelay(true);
		}
		catch(Exception e) {
			System.out.println("Could Not Find Server, Quitting :(");
			e.printStackTrace();
		}
		int i = 0;
		while(true) {
			 
			try {
				String s = "String";
				Passable p =  (Passable) inStream.readObject();
				if (p != null) {
					i++;
					decodeAndExecude(p);
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
    }
	
	private void decodeAndExecude(Passable p) {
		if (p.function.compareTo("_cat") == 0) {
			addCategory(p);
		}
		if (p.function.compareTo("_word") == 0) {
			addWord(p);
		}
		if (p.function.compareTo("_hint") == 0) {
			addHint(p);
		}
		if (p.function.compareTo("_newWord") == 0) {
			addNewWord(p);
		}
		if (p.function.compareTo("_guesses") == 0) {
			addGuesses(p);
		}
		if (p.function.compareTo("_correctWords") == 0) {
			addCorrectWords(p);
		}
		if (p.function.compareTo("_incorrectWords") == 0) {
			addIncorrectWords(p);
		}
	}	
	
	
	public void send(Passable p) {
		
		try {
			outStream.writeObject(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addCategory(Passable p) {
		categories.add(p.currCategory);
	}
	
	private void addWord(Passable p) {
		currWord = p.currWord;
	}
	
	private void addNewWord(Passable p) {
		
		currWord = p.currWord;
		successfulSubmit = p.successfulSubmit;
		guesses = p.incorrectGuesses;
		System.out.println("addNewWord Guesses: " + guesses);
		minorWinState = p.minorWinState;
		majorWinState = p.majorWinState;
		
		System.out.println("Adding Word");
		System.out.println("currWord:" + currWord + " successSubmit: " + successfulSubmit + " Guesses: " + guesses + " winCond: " + minorWinState);
	}
	
	private void addHint(Passable p) {
		currHint = p.currHint;
	}
	
	private void addGuesses(Passable p) {
		guesses = p.incorrectGuesses;
	}
	
	private void addCorrectWords(Passable p) {
		correctWords = p.correctWords;
	}
	
	private void addIncorrectWords(Passable p) {
		incorrectWords = p.incorrectWords;
	}
	
	public Vector<String> getCategories() {
		return categories;
	}
	
	public String getWord(String category) {
		currWord = null;
		//currHint = null;
		Passable p = new Passable();
		p.function = "getWord";
		p.currCategory = category;
		send(p);
		Passable p2 = new Passable();
		p2.function = "getHint";
		p2.currCategory = category;
		send(p2);
		
		int i = 0;
		while (currWord == null || currHint == null) { i++; if (i == 100000) {System.out.println("getWord Timed Out!");}}
		return currWord;
	}
	
	public String getNewWord(String category, String inputText) {
		guesses = -1;
		currWord = null;
		successfulSubmit = null;
		minorWinState = null;
		
		Passable p = new Passable();
		p.function = "getNewWord";
		p.currCategory = category;
		p.inputText = inputText;
		send(p);
		
		int i = 0;
		while (currWord == null || successfulSubmit == null || guesses == -1 || minorWinState == null) {
			i++;
			if (i == 1000000000) {
				System.out.println("getNewWord TimedOut");
				System.out.println("currWord: " + currWord + " successfulSubmit: " + successfulSubmit + " guesses: " + guesses);
			}
		}
		return currWord;
	}
	
	public String getHint(String category) {
		currHint = null;
		
		Passable p = new Passable();
		p.function = "getHint";
		p.currCategory = category;
		send(p);
		
		int i = 0;
		while (currHint == null) {i++; if (i == 100000) {System.out.println("getHint Timed Out");}}
		
		return currHint;
	}
	
	public String getSuccessfulSubmit() {
		return successfulSubmit;
	}
	
	public int getGuesses() {
		return guesses;
	}
	
	public String getWinCond() {
		return minorWinState;
	}
	
	public int getCorrectWords() {
		correctWords = -1;
		Passable p = new Passable();
		p.function = "getCorrectWords";
		send(p);
		int i = 0;
		while (correctWords == -1) {i++; if (i == 1000000) {System.out.println("getCorrectWords Timecd Out");}}
		return correctWords;
	}
	
	public int getIncorrectWords() {
		incorrectWords = -1;
		Passable p = new Passable();
		p.function = "getIncorrectWords";
		send(p);
		int i = 0;
		while (incorrectWords == -1) {i++; if (i == 1000000) {System.out.println("getIncorrectWords Timecd Out");}}
		return incorrectWords;
	}
	
	public String getMajorWinState() {
		return majorWinState;
	}
	
	public void setCurrCategory(String category) {
		Passable p = new Passable();
		p.function = "setCategory";
		p.currCategory = category;
		send(p);
	}
	
	public void reset() {
		Passable p = new Passable();
		p.function = "reset";
		send(p);
		
		Passable p2 = new Passable();
		p2.function = "getGuesses";
		send(p2);
		int i = 0;
		while (guesses == null) { i++; if (i == 100000) {System.out.println("GUESSES NULL");}}
	}
	
	
	//TODO Is this replaced by getNewWord?
	public void submitLetter(String input) {
		Passable p = new Passable();
		p.function = "submitLetter";
		p.inputText = input;
		send(p);
		
	}
}
