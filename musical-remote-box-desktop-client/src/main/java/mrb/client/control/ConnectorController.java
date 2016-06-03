package mrb.client.control;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import mrb.client.util.DataHolder;
import mrb.client.view.MessageBox;
import mrb.client.view.Player;
import mrb.server.IMusicalRemoteBox;

public class ConnectorController {

	@FXML
	private TextField input;
	
	@FXML
	private Text noHostFoundMessage;
	
	/**
	 * Event called by clicking the connect button. A connection attempt to the given IP is done.
	 * @param event
	 */
	@FXML
	public void connect(ActionEvent event) {
		boolean serverFound = false;
		try {
			String host = input.getText();
			IMusicalRemoteBox musicalRemoteBox = (IMusicalRemoteBox) LocateRegistry.getRegistry(host, Registry.REGISTRY_PORT).lookup("MusicalRemoteBox");
			DataHolder.getInstance().put("musicalRemoteBox", musicalRemoteBox);
			serverFound  = true;
		} catch (UnknownHostException e) {
			displayError();
		} catch (IOException e) {
			displayError();
		} catch (NotBoundException e) {
			(new MessageBox()).show("Internal Error. Your client might not be up to date.");
		} 
		if(serverFound) {
			try {
				(new Player()).show();
			} catch (IOException e) {
				(new MessageBox()).show("An Error occurred while loading the new formula.");
			}
		}
	}
	
	/**
	 * Displays an error message. Called, if the connection attempt to the entered IP failed.
	 */
	private void displayError() {
		new Thread(() ->{
			noHostFoundMessage.setVisible(true);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				noHostFoundMessage.setVisible(false);
			}
			noHostFoundMessage.setVisible(false);
		}).start();
	}
}
