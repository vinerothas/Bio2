package main;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

public class DrawPareto {

    public static void plot(Stage stage, ArrayList<ArrayList<Pop>> fronts){
        stage.setTitle("Fronts");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);

        lineChart.setTitle("Fronts");
        //defining a series

        Iterator<ArrayList<Pop>> it = fronts.iterator();
        float minDev = Float.MAX_VALUE;
        while(it.hasNext()) {
            Iterator<Pop> it2 = it.next().iterator();
            while(it2.hasNext()) {
                Pop pop = it2.next();
                if(pop.dev<minDev){
                    minDev = pop.dev;
                }
            }
        }


        it = fronts.iterator();
        int i = 1;
        while(it.hasNext()){
            Iterator<Pop> it2 = it.next().iterator();
            XYChart.Series series = new XYChart.Series();
            series.setName("Front "+i);
            i++;
            while(it2.hasNext()){
                Pop pop = it2.next();
                series.getData().add(new XYChart.Data(pop.conc,pop.dev-minDev));
            }
            lineChart.getData().add(series);
        }

        Scene scene  = new Scene(lineChart,800,600);

        stage.setScene(scene);
        stage.show();
    }

}
