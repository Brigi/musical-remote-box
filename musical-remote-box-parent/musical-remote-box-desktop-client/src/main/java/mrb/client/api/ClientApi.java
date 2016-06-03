package mrb.client.api;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import mrb.client.control.PlayerController;
import mrb.client.util.DataHolder;
import mrb.server.IMusicalRemoteBox;
import mrb.server.api.IClientApi;

public class ClientApi extends UnicastRemoteObject implements IClientApi, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ClientApi() throws RemoteException { }
	
	@Override
	public void updatePlayList(IMusicalRemoteBox musicalRemoteBox) throws RemoteException {
		List<File> currentList = musicalRemoteBox.getPlayList();
		PlayerController controller = (PlayerController) DataHolder.getInstance().get("playerController");
		controller.updatePlayList(currentList);
	}
}