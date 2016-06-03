package mrb.client.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mrb.client.util.DataHolder;

public class Connector {

	private static final String TITLE = "Musical RemoteBox - Connector";
	
	public void show() throws IOException {
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/connector.fxml"));
		Scene scene = new Scene(root,500,350);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/connector.css").toExternalForm());
		Stage stage = (Stage) DataHolder.getInstance().get("currentStage");
		
		stage.setTitle(TITLE);
		stage.setScene(scene);
		stage.show();
	}
}
