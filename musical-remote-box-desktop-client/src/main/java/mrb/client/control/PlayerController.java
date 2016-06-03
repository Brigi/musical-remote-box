package mrb.client.control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import mrb.client.api.ClientApi;
import mrb.client.util.DataHolder;
import mrb.client.view.MessageBox;
import mrb.server.IMusicalRemoteBox;
import mrb.server.model.AudioItem;
import mrb.server.model.AudioPacket;

public class PlayerController implements Initializable {

	private static final String FILE_DIALOG_TITLE = "Choose a song";
	
	private static final String[] AUDIO_TYPES = new String[] {"*.mp3"};
	
	private ClientApi clientApi;
	
	@FXML
	private ListView<AudioItem> playList;
	
	@FXML
	private Slider volumeSlider;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Set the text displayed in the playList for the custom Object "AudioItem"
		try {
			this.clientApi = new ClientApi();
		} catch (RemoteException e1) {
			callNoServerMessageBox();
		}
		final Callback<ListView<AudioItem>, ListCell<AudioItem>> callback =  (ListView<AudioItem> audioList) -> {return updateCell();};
		this.playList.setCellFactory(callback);
		final IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		DataHolder.getInstance().put("playerController", this);
		try {
			musicalRemoteBox.subscribe(this.clientApi);
		} catch (RemoteException e) {
			callNoServerMessageBox();
		}
		Stage stage = (Stage) DataHolder.getInstance().get("currentStage");
		stage.setOnCloseRequest((WindowEvent windowEvent) -> {
			try {
				musicalRemoteBox.unsubscribe(clientApi);
				UnicastRemoteObject.unexportObject(clientApi, true);
			} catch (NoSuchObjectException e) {
				// Nothing to do here if an exception occurs.
				// TODO Log error
			} catch (RemoteException e) {
				// Nothing to do here if an exception occurs.
				// TODO Log error
			}
		});
	}
	
	/**
	 * Updates the cell.
	 * @return the list of audio items
	 */
	private ListCell<AudioItem> updateCell() {
		ListCell<AudioItem> cell = new ListCell<AudioItem>() {
			
			@Override
			protected void updateItem(AudioItem t, boolean bln) {
                super.updateItem(t, bln);
                if (t != null) {
                    setText(t.toString());
                }
            }
		};
		return cell;
	}

	/**
	 * Plays the currently selected audio item.
	 * @param event ignored
	 */
	@FXML
	public void play(ActionEvent event) {
		IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		try {
			musicalRemoteBox.play();
		} catch (RemoteException e) {
			callNoServerMessageBox();
		}
	}

	/**
	 * Halts the currently selected audio item.
	 * @param event ignored
	 */
	@FXML
	public void stop(ActionEvent event) {
		IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		try {
			musicalRemoteBox.stop();
		} catch (RemoteException e) {
			callNoServerMessageBox();
		}
	}

	/**
	 * Calls a file dialog to choose a song to be added to the playlist.
	 * @param event ignored
	 */
	@FXML
	public void addSong(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.setTitle(FILE_DIALOG_TITLE);
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Audio files", AUDIO_TYPES);
		fileChooser.getExtensionFilters().add(filter);
		
		File file = fileChooser.showOpenDialog(null);
		addToPlaylist(file);		
	}
	
	/**
	 * Adds a song to the playlist.
	 * @param file the song to be added
	 */
	private void addToPlaylist(File file) {
		synchronized (this.playList) {
			if(file != null) {
				IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
				try {
					musicalRemoteBox.addSong(new AudioPacket(file));
				} catch (RemoteException e) {
					callNoServerMessageBox();
				} catch (IOException e) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							(new MessageBox()).show("The song could not be found. Mayb it was deleted while uploading it.");							
						}
					}).start();
				}
			}
		}
	}

	/**
	 * Skips the currently song and selects the next one in the playlist.
	 * @param event ignored
	 */
	@FXML
	public void forward(ActionEvent event) {
		IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		try {
			musicalRemoteBox.forward();
		} catch (RemoteException e) {
			callNoServerMessageBox();
		}
	}
	
	/**
	 * Skips the currently song and selects the previous one in the playlist.
	 * @param event ignored
	 */
	@FXML
	public void backward(ActionEvent event) {
		IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		try {
			musicalRemoteBox.backward();
		} catch (RemoteException e) {
			callNoServerMessageBox();
		}
	}
	
	/**
	 * Event called by clicking the volume bar.
	 * @param event ignored
	 */
	@FXML
	public void volumeSlideReleased(MouseEvent event) {
		adjustVolume();
	}
	
	/**
	 * Event called by scrolling over the volume bar.
	 * @param event ignored
	 */
	@FXML
	public void volumeSlideScroll(ScrollEvent event) {
		if(event.getDeltaY() > 0)
			this.volumeSlider.increment();
		if(event.getDeltaY() < 0)
			this.volumeSlider.decrement();
		adjustVolume();
	}
	
	/**
	 * Adjusts the volume to the currently selected value.
	 */
	private void adjustVolume() {
		IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
		try {
			musicalRemoteBox.adjustVolume(this.volumeSlider.getValue());
		} catch (RemoteException e) {
			callNoServerMessageBox();
		}
	}
	
	/**
	 * Listens on the mouse events. If the secondary key is clicked a menu for manipulating the songs is shown.
	 * @param event
	 */
	@FXML
	public void playListClickHandler(MouseEvent event) {
		if(event.getButton().equals(MouseButton.SECONDARY)) {
			final AudioItem audioItem = playList.getSelectionModel().getSelectedItem();
			if(audioItem != null) {
				ContextMenu menu = new ContextMenu();
				MenuItem item = new MenuItem("Remove");
				item.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						synchronized (playList) {
							IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) DataHolder.getInstance().get("musicalRemoteBox");
							int index = 0;
							for(AudioItem oneAudioItem: playList.getItems()) {
								if(oneAudioItem.equals(audioItem)) 
									break;
								index++;
							}
							try {
								musicalRemoteBox.removeSong(index);
							} catch (RemoteException e) {
								callNoServerMessageBox();
							}
						}
					}
				});
				menu.getItems().add(item);
				this.playList.setContextMenu(menu);
			}
		}
	}
	
	/**
	 * Event called, when something is dragged over the playlist
	 * @param event
	 */
	@FXML
	public void onDragOver(DragEvent event) {
		Dragboard board = event.getDragboard();
		if(board.hasFiles())
			event.acceptTransferModes(TransferMode.ANY);
		else
			event.consume();
	}
	
	/**
	 * Event called, when something is dropped onto the playlist. If it is an audio file it will be
	 * added to the playlist.
	 * @param event
	 */
	@FXML
	public void onDragDropped(DragEvent event) {
		Dragboard board = event.getDragboard();
		boolean success = false;
		
		for(File file: board.getFiles()){
			String[] splittedFileName = file.getName().split("\\.");
			String fileExtension = splittedFileName[splittedFileName.length - 1];
			for(String allowedExtension: AUDIO_TYPES) {
				if(fileExtension.equals(allowedExtension.split("\\.")[1])) {
					addToPlaylist(file);
					success = true;
				}
			}
		}
		event.setDropCompleted(success);
		event.consume();
	}

	/**
	 * Updates the playlist with the given list. Mostly called by new instanced of the client.
	 * @param currentList current list of audio items in the playlist.
	 * @throws RemoteException if, the client is not available
	 */
	public void updatePlayList(final List<File> currentList) throws RemoteException {
		Platform.runLater(() -> {
			playList.getItems();
			playList.getItems().clear();
			for(File song: currentList)
				playList.getItems().add(new AudioItem(song));
		});
	}
	
	/**
	 * Lets a message box pop up with an error message. Called, if something went wrong.
	 */
	private void callNoServerMessageBox() {
		(new MessageBox()).show("An Error occured while connecting to the server.");
		try {
			UnicastRemoteObject.unexportObject(clientApi, true);
		} catch (NoSuchObjectException e) {
			(new MessageBox()).show("The client could not be shut down properly.\nYou have to kill the process by yourself, I guess. ;)");
		}
	}
}
