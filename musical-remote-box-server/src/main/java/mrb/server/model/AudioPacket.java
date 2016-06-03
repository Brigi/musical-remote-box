package mrb.server.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class AudioPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] data;
	
	private String songTitle;
	
	/**
	 * Constructor, which stores the given file as a byte array.
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public AudioPacket(File file) throws FileNotFoundException, IOException {
		this.data = new byte[(int) file.length()];
		this.songTitle = file.getName();
		FileInputStream inStream = new FileInputStream(file);
		inStream.read(this.data);
		inStream.close();
	}
	
	/**
	 * Gets the title of the song.
	 * @return
	 */
	public String getTitle() {
		return this.songTitle;
	}
	
	/**
	 * Unpacks the stored data to the given stream.
	 * @param outStream the stream determining, where the data should be extracted to.
	 * @throws IOException
	 */
	public void unpack(OutputStream outStream) throws IOException{
		outStream.write(this.data);
	}
}
