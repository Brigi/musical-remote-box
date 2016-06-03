package mrb.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import mrb.server.api.IClientApi;
import mrb.server.model.AudioPacket;

public class MusicalRemoteBox extends UnicastRemoteObject implements IMusicalRemoteBox{

	private static final String AUDIO_DIR = new File("").getAbsolutePath() + "/audio";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int currentSongIndex = 0;
	
	private List<File> playList = new ArrayList<File>();
	
	private List<IClientApi> clients = new ArrayList<IClientApi>();
	
	private MediaPlayer player;
	
	private double volume = 100;
	
	protected MusicalRemoteBox() throws RemoteException {
		super();
		File audioDir = new File(AUDIO_DIR);
		if(!audioDir.exists())
			audioDir.mkdirs();
		else {
			File[] allSongFiles = audioDir.listFiles();
			for(File song :allSongFiles) 
				song.delete();
		}
	}

	@Override
	public void subscribe(IClientApi client) throws RemoteException {
		synchronized (this.clients) {
			this.clients.add(client);
		}
	}
	
	@Override
	public void unsubscribe(IClientApi client) throws RemoteException {
		synchronized (this.clients) {
			this.clients.remove(client);
		}
	}

	@Override
	public void addSong(AudioPacket song) throws RemoteException{
		OutputStream outStream;
		try {
			File songFile = new File(AUDIO_DIR + "/" + song.getTitle());
			outStream = new FileOutputStream(songFile);
			song.unpack(outStream);
			outStream.flush();
			outStream.close();
			
			synchronized (this.playList) {
				this.playList.add(songFile);
				updateClients();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void removeSong(int index) throws RemoteException{
		Status status = null;
		synchronized (this.playList) {
			if(index == this.currentSongIndex && this.player != null) {
				status = this.player.getStatus();
				this.player.stop();
				this.player = null;
			}
		}
		if(!this.playList.isEmpty()) {
			File toDelete = this.playList.get(index);
			this.playList.remove(index);
			boolean test = toDelete.delete();
			if(status != null && status == Status.PLAYING)
				play();
			updateClients();
		}
	}

	@Override
	public void play() throws RemoteException{
			if(this.player == null && this.playList.size() > 0) {
				this.player = new MediaPlayer(new Media(this.playList.get(currentSongIndex).toURI().toString()));
				this.player.setVolume(this.volume);
				System.out.println("Playing " + this.playList.get(currentSongIndex).getAbsolutePath());
				this.player.setOnError(new Runnable() {
					
					@Override
					public void run() {
						System.out.println("error");
						synchronized (player) {
							player = null;							
						}
						playList.remove(currentSongIndex);
						if(currentSongIndex < (playList.size()-1))
							currentSongIndex++;
						else 
							currentSongIndex = 0;
						try {
							play();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				this.player.setOnEndOfMedia(new Runnable() {
					
					@Override
					public void run() {
						if(currentSongIndex < (playList.size()-1))
							currentSongIndex++;
						else 
							currentSongIndex = 0;
						player = null;
						try {
							play();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				this.player.stop();
			}
			if(this.player != null)
				this.player.play();
	}

	@Override
	public void stop() throws RemoteException{
		if(this.player != null) {
			synchronized (this.player) {
				this.player.pause();
			}
		}	
	}

	@Override
	public List<File> getPlayList() throws RemoteException{
		return this.playList;
	}
	
	private void updateClients() {
		List<IClientApi> clientsToBeRemoved = new ArrayList<IClientApi>();
		for(IClientApi client: this.clients) {
			try {
				client.updatePlayList(this);
			} catch (RemoteException e) {
				System.out.println("Connection to a client lost. Has been removed from the list of clients.");
				clientsToBeRemoved.add(client);
			}
		}
		for(IClientApi client: clientsToBeRemoved)
			this.clients.remove(client);
	}

	@Override
	public void forward() throws RemoteException {
		if(currentSongIndex < (playList.size()-1))
			currentSongIndex++;
		else 
			currentSongIndex = 0;
		if(this.player != null){
			this.player.stop();
			this.player = null;
		}
		play();
	}

	@Override
	public void backward() throws RemoteException {
		if(currentSongIndex > 0)
			currentSongIndex--;
		else 
			currentSongIndex = this.playList.size() - 1;
		if(this.player != null){
			this.player.stop();
			this.player = null;
		}
		play();
	}
	
	@Override
	public void adjustVolume(double rate) throws RemoteException {
		if(this.player != null) {
			synchronized (this.player) {
				this.volume = rate / 100;
				this.player.setVolume(this.volume);
			}
		}
	}
}
