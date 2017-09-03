package com.drice.scrapingjunk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by DrIce on 8/23/17.
 */
public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main_view.fxml"));

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("The Filthiest Scraper");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
