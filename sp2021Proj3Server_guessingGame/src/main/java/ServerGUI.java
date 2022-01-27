import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application {
	public WordGame game;
	String inputFile;
	int counter, counter2;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		ListView<String> output = new ListView<String>();
		Scene serverGUI = new Scene(output, 500, 500);
		
		// TODO Auto-generated method stub
		primaryStage.setTitle("Game Server");
		
		Label ipLabel   = new Label("Enter IP (127.0.0.1)");
		Label portLabel = new Label("And Port (5555)");
		
		TextField ipTextField = new TextField("127.0.0.1");
		TextField portTextField = new TextField("5555");
		
		Button startButton = new Button("Start");
		
		VBox v1 = new VBox(ipLabel, portLabel, ipTextField, portTextField, startButton);
		
		startButton.setOnAction(e-> {
			Server server = new Server(data -> {
				Platform.runLater(() -> {
						output.getItems().add(data.toString());
				});
			}, ipLabel.getText(), Integer.parseInt(portTextField.getText()));
			primaryStage.setScene(serverGUI);
		});
		
		Scene s = new Scene(v1, 400, 400);
		primaryStage.setScene(s);
		primaryStage.show();
		
		
		
		
		
		
		
		
		
		
		
		
		primaryStage.show();
		
	}

}
