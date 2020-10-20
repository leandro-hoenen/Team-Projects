package geographyProject;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GeoView {
	private Stage stage;
	private GeoModel model;
	
	// top controls

	public GeoView(Stage primaryStage, GeoModel model) {
		this.stage = primaryStage;
		this.model = model;
		
		stage.setTitle("Geo Database");
		
		// Create Masterpane for the different UI areas
		BorderPane root = new BorderPane();
		root.setTop(createDataControls());
		root.setLeft(createLeftControls());
		
		// TO DO: set up scene
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("geographyProject.css").toExternalForm());
		stage.setScene(scene);

	}
	
	public void start() {
		stage.show();
	}
	
	public HBox createDataControls() {
		HBox topBtnPane = new HBox();
		

		Button btnCreate = new Button ("Create");
		Button btnEdit = new Button ("Edit");
		Button btnDelete = new Button ("Delete");
		Button btnSave = new Button ("Save");
		topBtnPane = new HBox(btnCreate, btnEdit, btnDelete, btnSave);
		
		return topBtnPane;
	}
	
	public VBox createLeftControls() {
		VBox leftControls = new VBox();
		
		return leftControls;
	}

}
