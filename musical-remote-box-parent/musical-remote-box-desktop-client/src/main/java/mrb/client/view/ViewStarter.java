package mrb.client.view;

import mrb.client.util.DataHolder;
import javafx.application.Application;
import javafx.stage.Stage;

public class ViewStarter extends Application{
	
	public void start(String... args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		DataHolder.getInstance().put("currentStage", stage);
		(new Connector()).show();
	}
}
