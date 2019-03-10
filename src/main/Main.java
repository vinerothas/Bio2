package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        Bean bean = new Bean();
        Reader reader = new Reader();
        reader.readImage(0, bean);
        Pop pop = new Pop(bean);
        pop.printConnections();
        System.out.println();
        //Writer writer = new Writer();
        //writer.write(bean);
    }


    public static void main(String[] args) {
        launch(args);
    }
}