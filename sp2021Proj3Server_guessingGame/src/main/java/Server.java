import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;
import java.util.function.Consumer;

import javafx.application.Platform;

public class Server {
	
	int numClients;
	ServerThread server;
	Vector<ClientThread> clients;
	Consumer<Serializable> callback;
	
	Server(Consumer<Serializable> call, String ip, int port){
		callback = call;
		server = new ServerThread();
		server.setDaemon(true);
		server.start();
		clients = new Vector<ClientThread>();
	}
	
	class ServerThread extends Thread {
		public void run() {
			try(ServerSocket mysocket = new ServerSocket(5555);) {
				
			    System.out.println("Server is waiting for a client!");
			  
				
			    while(true) {
			
					ClientThread c = new ClientThread(mysocket.accept(), numClients);
					callback.accept("client has connected to server: " + "client #" + numClients);
					clients.add(c);
					c.start();
					
					numClients++;
				
			    }
			    
			} catch(Exception e) {
				e.printStackTrace();
				callback.accept("Server socket did not launch");
			}
		}
	}
	
	// Client connection thread from server-side
	class ClientThread extends Thread {
		String inputFile = "src/main/resources/input1.txt";
		WordGame wg;
		
		// Connection to the client
		Socket connection;
		
		// Id num for thread
		int clientNum;
		
		// I/O Streams
		ObjectOutputStream outStream;
		ObjectInputStream inStream;
		
		ClientThread(Socket s, int numClients) {
			connection = s;
			clientNum = numClients;
			wg = new WordGame(inputFile);
			if (!wg.getFileSuccess()) {
				callback.accept("Could Not Open File: " + inputFile);
				System.exit(0);
			}
			
			Vector<String> categories = wg.getCategories(),
							   words1 = wg.getWords(categories.elementAt(0)),
							   words2 = wg.getWords(categories.elementAt(1)),
							   words3 = wg.getWords(categories.elementAt(2));
		}
		
		public void run() {
			// Set up connection with client
			try {
				inStream = new ObjectInputStream(connection.getInputStream());
				outStream = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);	
			}
			catch(Exception e) {
				callback.accept("Streams not open");
			}
			
			try {
				Passable p = new Passable();
				p.function = "_cat";
				p.currCategory = wg.getCategories().elementAt(0);
				Passable p2 = new Passable();
				p2.function = "_cat";
				p2.currCategory = wg.getCategories().elementAt(1);
				Passable p3 = new Passable();
				p3.function = "_cat";
				p3.currCategory = wg.getCategories().elementAt(2);
				outStream.writeObject(p);
				outStream.writeObject(p2);
				outStream.writeObject(p3);
			} catch (IOException e1) {
				callback.accept("Could not send categories!");
				e1.printStackTrace();
			}
			
			while(true) {
				try {
			    	Passable p = (Passable) inStream.readObject(); // .readObject() is blocking call
			    	callback.accept("client: " + clientNum + " sent: " + p.function);
			    	
			    	
			    	decodeAndExecuteData(p);
			    	
			    } catch(Exception e) {
			    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + clientNum + "....closing down!");
			    	e.printStackTrace();
			    	clients.remove(this); 
			    	break;
			    }
			}
		}
		
		// Decode incoming data
		private void decodeAndExecuteData(Passable p_in) {
			if (p_in.function.compareTo("getWord") == 0) {
	    		try {
	    			Passable p = new Passable();
	    			p.function = "_word";
	    			p.currWord = wg.getCloakedCurrWord();
					outStream.writeObject(p);
					callback.accept("Sending cloaked-word: " + p.currWord);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		
	    	} else if (p_in.function.compareTo("getNewWord") == 0) {
	    		Platform.runLater( ()-> {
		    		try {
		    			Passable p = new Passable();
		    			p.function = "_newWord";
		    			p.successfulSubmit = wg.submitLetter( p_in.inputText.charAt(0) );
		    			p.currWord = wg.getCloakedCurrWord();
		    			p.incorrectGuesses = wg.getGuesses();
		    			p.minorWinState = wg.getMinorWinState();
		    			p.majorWinState = wg.getMajorWinState();
		    			callback.accept("Sending successfulSubmit: " + p.successfulSubmit);
		    			callback.accept("Sending new cloaked-word: " + p.currWord);
		    			callback.accept("Sending incorrectGuesses: " + p.incorrectGuesses);
		    			callback.accept("Sending minorWinState: " + p.minorWinState);
		    			callback.accept("Sending majorWinState: " + p.majorWinState);
		    			
		    			
		    			outStream.writeObject(p);
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
	    		});
	    		
	    	} else if (p_in.function.compareTo("setCategory") == 0) {
	    		wg.setCurrCategory(p_in.currCategory);
	    		wg.setCurrWord();
	    		callback.accept("Setting new cagetory and word");
	    		
	    	} else if (p_in.function.compareTo("getHint") == 0) {
	    		try {
	    			Passable p = new Passable();
	    			p.function = "_hint";
	    			p.currHint = wg.getHint(p_in.currCategory);
					outStream.writeObject(p);
					callback.accept("Sending hint: " + p.currHint);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		
	    	} else if (p_in.function.compareTo("getGuesses") == 0) {
	    		try {
	    			Passable p = new Passable();
	    			p.function = "_guesses";
	    			p.incorrectGuesses = wg.getGuesses();
	    			callback.accept("Sending num incorrect guesses: " + p.incorrectGuesses);
	    			
					outStream.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	} else if (p_in.function.compareTo("reset") == 0) {
	    		callback.accept("Reseting!");
	    		wg.reset();
	    	} else if (p_in.function.compareTo("getCorrectWords") == 0) {
	    		Passable p = new Passable();
	    		p.function = "_correctWords";
	    		p.correctWords = wg.getCorrectWords();
	    		callback.accept("Sending correctWords: " + p.correctWords);
	    		try {
					outStream.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	} else if (p_in.function.compareTo("getIncorrectWords") == 0) {
	    		Passable p = new Passable();
	    		p.function = "_incorrectWords";
	    		p.incorrectWords = wg.getIncorrectWords();
	    		callback.accept("Sending incorrectWords: " + p.incorrectWords);
	    		try {
					outStream.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
		}
	}
}
