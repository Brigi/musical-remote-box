package mrb.client.view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import mrb.client.util.DataHolder;
import mrb.server.IMusicalRemoteBox;
import mrb.server.model.AudioItem;

public class Player {
	
	private static final String TITLE = "Musical RemoteBox - Player";
	
	public void show() throws IOException {
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/player.fxml"));
		Scene scene = new Scene(root,500,350);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/player.css").toExternalForm());
		Stage stage = (Stage) DataHolder.getInstance().get("currentStage");
		
		// Fill playlist with current songs
		ListView<AudioItem> playList = (ListView<AudioItem>) root.lookup("#playList");
		IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		List<File> currentList = musicalRemoteBox.getPlayList();
		for(File song: currentList)
			playList.getItems().add(new AudioItem(song));
		
		stage.setTitle(TITLE);
		stage.setScene(scene);
		stage.show();
	}
}
