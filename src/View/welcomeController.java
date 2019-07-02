package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

/**
 * Created by Ilana on 24/06/2017.
 */

public class welcomeController {

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }
    MyModel model = new MyModel();
    public Button playButton;


    public void start(ActionEvent actionEvent) throws Exception {
        Stage primaryStage = new Stage();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("My Application!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());

        primaryStage.setMinWidth(635);
        primaryStage.setMinHeight(390);
        primaryStage.setScene(scene);
        //--------------
        View view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        view.setZoom(scene);
        view.playMusic();
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        view.disBTN();
        //--------------
        SetStageCloseEvent(primaryStage);
        primaryStage.show();


        Stage stage2 = (Stage) playButton.getScene().getWindow();
        stage2.close();

    }


    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // ... user chose OK
                    model.stop();
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });

    }
}
