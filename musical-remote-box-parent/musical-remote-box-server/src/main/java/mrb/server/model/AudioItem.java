package mrb.server.model;

import java.io.File;
import java.io.Serializable;

import javafx.scene.control.Control;

public class AudioItem extends Control implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private File audioFile;
	
	public AudioItem(File audioFile) {
		this.audioFile = audioFile;
	}
	
	public File getFile(){
		return this.audioFile;
	}
	
	@Override
	public String toString() {
		return audioFile.getName();
	}
}
