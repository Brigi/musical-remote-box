package mrb.client.view;

import mrb.client.util.DataHolder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MessageBox {
	
	private static final String TITLE = "Error";
	
	public void show(String message) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text text = new Text(message);
		grid.add(text, 0, 0);
		
		HBox hBox = new HBox();
		Button button = new Button("OK");
		button.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				Platform.exit();	
			}
		});
		hBox.setAlignment(Pos.BOTTOM_CENTER);
		hBox.getChildren().add(button);
		grid.add(hBox, 0, 1);
		
		Scene scene = new Scene(grid);
		Stage stage = (Stage) DataHolder.getInstance().get("currentStage");
		stage.setScene(scene);
		stage.setTitle(TITLE);
		stage.show();
	}
}
