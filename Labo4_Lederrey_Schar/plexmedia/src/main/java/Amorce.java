import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import ch.heigvd.iict.ser.rmi.IServerMediaApi;
import controller.mainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Amorce extends Application {
    public static Stage stage;

    public static void main(String [] args) throws RemoteException, NotBoundException, MalformedURLException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Labo4");
        Scene rootScene = new Scene(root);
        primaryStage.setScene(rootScene);


        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();;
        });

        primaryStage.show();
    }
}
