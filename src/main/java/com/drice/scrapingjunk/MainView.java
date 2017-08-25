package com.drice.scrapingjunk;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by DrIce on 8/23/17.
 */
public class MainView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        GridPane grid1 = new GridPane();
        grid1.setAlignment(Pos.CENTER);
        //grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(new Group(), 1250, 800);
        primaryStage.setScene(scene);

        Text sceneTitle = new Text("Scanner");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid1.add(sceneTitle, 0, 0, 4, 4);


        /*
        Pane mostRecentScanBox = createMostRecentScanView();
        mostRecentScanBox.setPadding(new Insets(25, 25, 25, 25));
        */
        BorderPane bp = new BorderPane();
        BorderPane.setAlignment(grid1, Pos.CENTER_LEFT);
        //BorderPane.setAlignment(mostRecentScanBox, Pos.CENTER_RIGHT);
        bp.setLeft(grid1);
        //bp.setRight(mostRecentScanBox);

        //Pane p = createMessageDisplay();
        //p.setPadding(new Insets(25, 25, 25, 25));
        //p.setStyle("-fx-background-color: black;");

        //BorderPane.setAlignment(p, Pos.BOTTOM_CENTER);
        //bp.setBottom(p);

        ((Group)scene.getRoot()).getChildren().addAll(bp);

        primaryStage.show();

    }
}
