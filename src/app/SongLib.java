//Sanjay Kao (sjk231)
//Virginia Cheng (vc365)

package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.SongLibController;

public class SongLib extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation(
				getClass().getResource("/view/songlib.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		
		SongLibController controller = 
				loader.getController();
		controller.start(stage);

        Scene scene = new Scene(root);

        stage.setTitle("Library");
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
