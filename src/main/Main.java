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
        reader.readImage(1, bean);
        Pop pop = new Pop(bean);
        pop.printConnections();
        for (int i = 0; i < 10; i++) {
            pop = Mutator.mutateV(pop,bean);
        }
        pop.printConnections();
        //pop.calculateFitness(bean);
        //System.out.println(pop.dev+" "+pop.conc);
        //Writer writer = new Writer();
        //writer.write(bean);
    }


    public static void main(String[] args) {
        launch(args);
    }
}