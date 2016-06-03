package mrb.server;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import mrb.server.api.IClientApi;
import mrb.server.model.AudioPacket;

public interface IMusicalRemoteBox extends Remote{

	/**
	 * Adds a client to the list of clients, so that it can be updated if changes to the play list are made.
	 * @param client the client to be added
	 * @throws RemoteException
	 */
	void subscribe(IClientApi client) throws RemoteException;
	
	/**
	 * Removes the client from the list of clients.
	 * @param client the client to be removed
	 * @throws RemoteException
	 */
	void unsubscribe(IClientApi client) throws RemoteException;
	
	/**
	 * Adds the given song to the play list.
	 * @param song the song the be added
	 * @throws RemoteException
	 */
	void addSong(AudioPacket song) throws RemoteException;
	
	/**
	 * Removes the given song.
	 * @param index the index of the song to be removed.
	 * @throws RemoteException
	 */
	void removeSong(int index) throws RemoteException;
	
	/**
	 * Lets the player begin to play the play list.
	 * @throws RemoteException
	 */
	void play() throws RemoteException;
	
	/**
	 * Lets the player stop to play.
	 * @throws RemoteException
	 */
	void stop() throws RemoteException;
	
	/**
	 * Plays the next song.
	 * @throws RemoteException
	 */
	void forward() throws RemoteException;
	
	/**
	 * Plays the previous song.
	 * @throws RemoteException
	 */
	void backward() throws RemoteException;
	
	/**
	 * Adjusts the volume.
	 * @param rate the new volume rate
	 * @throws RemoteException
	 */
	void adjustVolume(double rate) throws RemoteException;
	
	/**
	 * Gets the current play list.
	 * @return the current play list
	 * @throws RemoteException
	 */
	List<File> getPlayList() throws RemoteException;
}
