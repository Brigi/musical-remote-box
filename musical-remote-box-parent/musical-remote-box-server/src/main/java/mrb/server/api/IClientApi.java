package mrb.server.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

import mrb.server.IMusicalRemoteBox;

public interface IClientApi extends Remote{
	
	/**
	 * Updates the playlist e.g. when somebody else added a song to the list.
	 * @param musicalRemoteBox the remote box to be updated
	 * @throws RemoteException if no connection to the remote box could be established
	 */
	void updatePlayList(IMusicalRemoteBox musicalRemoteBox) throws RemoteException;
}
