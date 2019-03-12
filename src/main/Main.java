package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        Random r = new Random(System.nanoTime());
        Bean bean = new Bean();
        Reader reader = new Reader();
        reader.readImage(1, bean);

        NSGA nsga = new NSGA();
        //nsga.start(bean);
        ArrayList<ArrayList<Pop>> fronts = nsga.start(bean);
        DrawPareto.plot(primaryStage,fronts);

        /*
        Pop pop = new Pop(bean);
        //pop.printConnections();
        for (int i = 0; i < 100; i++) {
            if(r.nextBoolean()) {
                pop = Mutator.mutateR(pop, bean);
            }else{
                pop = Mutator.mutateA(pop,bean);
            }
            //pop.printConnections();
            pop.calculateFitness(bean);
            System.out.println(pop.dev+" "+pop.conc);
        }*/
        //Writer writer = new Writer();
        //writer.write(bean);
    }

    public static void main(String[] args) {
        launch(args);
    }
}