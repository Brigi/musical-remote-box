package mrb.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.application.Application;
import javafx.stage.Stage;


public class ServerStarter extends Application{

	public static void main(String... args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		System.out.print("Setting up server...");
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (RemoteException e) {
			System.out.println("Registry could not be created.");
			e.printStackTrace();
		}
		try {
			LocateRegistry.getRegistry(Registry.REGISTRY_PORT).rebind("MusicalRemoteBox", new MusicalRemoteBox());
		} catch (Exception e) {
			System.out.println("Registry could not rebind server.");
			e.printStackTrace();
		}
		System.out.println(" done.");
	}
}
