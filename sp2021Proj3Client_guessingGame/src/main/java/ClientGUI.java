import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {

	Stage primaryStage;
	Map<String, Scene> sceneMap;
	
	Client c;
	
	String currCategory;
	
	// Startup FX components
	VBox v1;
	Label ipLabel,
		  portLabel;
	TextField ipTextField,
			  portTextField;
	Button startButton;
	
	
	// Category selection scene FX components
	Label catLabel;
	Button cat1Button,
		   cat2Button,
		   cat3Button;
	Label rightWordsLabel,
		   wrongWordsLabel;
	VBox v2;
	HBox h1;
	BorderPane b1;
	
	
	// ClientGUI scene FX components
	Label hintLabel,
		  errorLabel,
		  numGuesses,
		  numLettersLabel,
		  correctLettersLabel;
	TextField charInputTF;
	Button submitButton;
	
	
	// EndGUI scene FX components
	VBox v3;
	HBox h2;
	Label winLabel;
	Button quitButton,
		   playAgainButton;
		   
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.primaryStage = primaryStage;
		// TODO Auto-generated method stub
		this.primaryStage.setTitle("Welcome to JavaFX");
		
		sceneMap = buildScenes();
		
		Scene startupScene = sceneMap.get("startupLogin");
		this.primaryStage.setScene(startupScene);
		this.primaryStage.show();
		
		
	}
	
	private Map<String, Scene> buildScenes() {
		Map<String, Scene> m = new HashMap<String, Scene>();
		m.put("startupLogin", buildStartupLogin());
		m.put("catSelect",	  buildCatSelect());
		m.put("clientGUI",    buildClientGUI());
		m.put("winScene",	  buildEndGUI());
		
		return m;
	}
	
	private Scene buildStartupLogin() {
		
		ipLabel   = new Label("Enter IP (127.0.0.1)");
		portLabel = new Label("And Port (5555)");
		
		ipTextField = new TextField("127.0.0.1");
		portTextField = new TextField("5555");
		
		startButton = new Button("Start");
		
		v1 = new VBox(ipLabel, portLabel, ipTextField, portTextField, startButton);
		
		startButton.setOnAction(e-> {
			startButton.setDisable(true);
			c = new Client(data -> {}, ipTextField.getText(), portTextField.getText()
			);
			c.setDaemon(true);
			c.start();
			
			// Wait until there are 3 categories fetched from the server
			while(c.getCategories().size() < 3) {}
			
			Vector<String> categories = c.getCategories();
			
			cat1Button.setText(categories.elementAt(0));
			cat2Button.setText(categories.elementAt(1));
			cat3Button.setText(categories.elementAt(2));
			
			primaryStage.setScene(sceneMap.get("catSelect"));
		});
		
		Scene s = new Scene(v1, 400, 400);
		
		return s;
	}
	
	private Scene buildCatSelect() {
		
		catLabel = new Label("Categories");
		
		cat1Button = new Button();
		cat2Button = new Button();
		cat3Button = new Button();
		
		rightWordsLabel = new Label("  Correctly Guessed Words: 0");
		wrongWordsLabel = new Label("Incorrectly Guessed Words: 0");
		
		h1 = new HBox(cat1Button, cat2Button, cat3Button);
		v2 = new VBox(h1, rightWordsLabel, wrongWordsLabel);
		b1 = new BorderPane(v2, catLabel, null, null, null);
		
		
		cat1Button.setOnAction(e-> {
			
			c.reset();
			System.out.println();
			currCategory = cat1Button.getText();
			c.setCurrCategory(cat1Button.getText());
			correctLettersLabel.setText(c.getWord(currCategory));
			hintLabel.setText("Hint: " + c.getHint(currCategory));
			numLettersLabel.setText("Word Length: " + correctLettersLabel.getText().length());
			numGuesses.setText(String.valueOf("Guesses Left: " + c.getGuesses()));
			errorLabel.setText("");
			
			primaryStage.setScene(sceneMap.get("clientGUI"));
		});
		cat2Button.setOnAction(e-> {
			
			c.reset();
			currCategory = cat2Button.getText();
			c.setCurrCategory(cat2Button.getText());
			hintLabel.setText("Hint: " + c.getHint(currCategory));
			correctLettersLabel.setText(c.getWord(currCategory));
			numLettersLabel.setText("Word Length: " + correctLettersLabel.getText().length());
			numGuesses.setText(String.valueOf("Guesses Left: " + c.getGuesses()));
			errorLabel.setText("");
			
			primaryStage.setScene(sceneMap.get("clientGUI"));
		});
		cat3Button.setOnAction(e-> {
			
			c.reset();
			currCategory = cat3Button.getText();
			c.setCurrCategory(cat3Button.getText());
			hintLabel.setText("Hint: " + c.getHint(currCategory));
			correctLettersLabel.setText(c.getWord(currCategory));
			numLettersLabel.setText("Word Length: " + correctLettersLabel.getText().length());
			numGuesses.setText(String.valueOf("Guesses Left: " + c.getGuesses()));
			errorLabel.setText("");
			
			primaryStage.setScene(sceneMap.get("clientGUI"));
		});
		
		
		Scene s = new Scene(b1, 700, 700);
		
		return s;
	}
	
 	private Scene buildClientGUI() {
		
 		hintLabel = new Label();
 		numLettersLabel = new Label();
 		correctLettersLabel = new Label();
 		charInputTF = new TextField();
 		submitButton = new Button("Submit");
 		numGuesses = new Label("Guesses Left: 6");
 		errorLabel = new Label();
 		Label instructionLabel = new Label(
 				"\nHow to play:\n"
 				+ "- To win, you must correctly guess 1 word in each category.\n"
 				+ "- For each word to be guesssed, you are allowed 6 mistakes.\n"
 				+ "- Three total incorrectly guessed words results in a loss.\n"
 				+ "- Note that for each word begins with an UPPERCASE letter.\n"
 				+ "\n"
 				+ "Good luck! :)\n"
 				+ "P.S: Please allow for 10 sec if server is slow to respond");
 		
 		submitButton.setOnAction(e->{
 			Platform.runLater(() ->{
 				submitButton.setDisable(true);
	 			errorLabel.setText("");
	 			if (charInputTF.getText().length() != 1) {
	 				charInputTF.setText("");
	 				errorLabel.setText("Please enter only 1 character!");
	 				submitButton.setDisable(false);
	 				return;
	 			}
	 			String newWord = c.getNewWord(currCategory, charInputTF.getText());
	 			rightWordsLabel.setText("  Correctly Guessed Words: " + c.getCorrectWords());
	 			wrongWordsLabel.setText("Incorrectly Guessed Words: " + c.getIncorrectWords());
	 			
	 			if (c.getMajorWinState().compareTo("Won") == 0) {
	 				System.out.println("GAME WON");
	 				winLabel.setText("Congradulation! You Won!");
	 				primaryStage.setScene(sceneMap.get("winScene"));
	 				return;
	 			} else if (c.getMajorWinState().compareTo("Lost") == 0) {
	 				System.out.println("GAME LOST");
	 				winLabel.setText("Off, too bad, you lose :(");
	 				primaryStage.setScene(sceneMap.get("winScene"));
	 				return;
	 			}
	 			
	 			if (c.getWinCond().compareTo("Won") == 0) {
	 				// Disable button for that category
 					if (cat1Button.getText().compareTo(currCategory) == 0) {
 						cat1Button.setDisable(true);
 					} else if (cat2Button.getText().compareTo(currCategory) == 0) {
 						cat2Button.setDisable(true);
 					} else if (cat3Button.getText().compareTo(currCategory) == 0) {
 						cat3Button.setDisable(true);
 					}
	 				
	 				// Switch scene
	 				primaryStage.setScene(sceneMap.get("catSelect"));
	 			} else if (c.getWinCond().compareTo("Lost") == 0) {
	 				primaryStage.setScene(sceneMap.get("catSelect"));
	 			}
	 			correctLettersLabel.setText(newWord);
 				errorLabel.setText(c.getSuccessfulSubmit());
 				numGuesses.setText("Guesses Left: " + c.getGuesses());
	 			
	 			charInputTF.setText("");
	 			submitButton.setDisable(false);
 			});
 		});
 		
		Scene s = new Scene(new VBox(hintLabel, numLettersLabel, correctLettersLabel, charInputTF, submitButton, numGuesses, errorLabel, instructionLabel), 700, 700);
		
		return s;
	}

 	private Scene buildEndGUI() {
 		winLabel = new Label();
 		quitButton = new Button("Quit");
 		playAgainButton = new Button("Play Again");
 		h2 = new HBox(playAgainButton, quitButton);
 		v3 = new VBox(winLabel, h2);
 		
 		quitButton.setOnAction(e -> {
 			System.exit(0);
 		});
 		
 		playAgainButton.setOnAction(e -> {
			c = new Client(t -> {}, ipTextField.getText(), portTextField.getText());
			c.setDaemon(true);
			c.start();
			
			// Wait until there are 3 categories fetched from the server
			while(c.getCategories().size() < 3) {}
			Vector<String> categories = c.getCategories();
			
			sceneMap = buildScenes();
			
			cat1Button.setText(categories.elementAt(0));
			cat2Button.setText(categories.elementAt(1));
			cat3Button.setText(categories.elementAt(2));
			
			
			primaryStage.setScene(sceneMap.get("catSelect"));
			
 		});
 		
 		Scene s = new Scene(v3, 500, 500);
 		
 		return s;
 	}
}
