package main;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class FitnessPlot {

    public static void plot(Stage stage) {


        stage.setTitle("Fitness");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generations");
        yAxis.setLabel("Normalized values");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Fitness");
        lineChart.setCreateSymbols(false);
        Double[] fitnessAverage = GraphData.fitnessAverage.toArray(new Double[0]);
        Double[] fitnessBest = GraphData.fitnessBest.toArray(new Double[0]);
        Double[] priAverage = GraphData.priAverage.toArray(new Double[0]);
        Double[] priBest = GraphData.priBest.toArray(new Double[0]);
        Double[] devAverage = GraphData.devAverage.toArray(new Double[0]);
        Double[] devBest = GraphData.devBest.toArray(new Double[0]);
        Double[] concAverage = GraphData.concAverage.toArray(new Double[0]);
        Double[] concBest = GraphData.concBest.toArray(new Double[0]);


        XYChart.Series series1 = new XYChart.Series();
        series1.setName("fitnessAverage");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("fitnessBest");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("priAverage");
        XYChart.Series series4 = new XYChart.Series();
        series4.setName("priBest");
        XYChart.Series series5 = new XYChart.Series();
        series5.setName("devAverage");
        XYChart.Series series6 = new XYChart.Series();
        series6.setName("devBest");
        XYChart.Series series7 = new XYChart.Series();
        series7.setName("concAverage");
        XYChart.Series series8 = new XYChart.Series();
        series8.setName("concBest");

        double lowfit = Double.min(GraphData.lowestAverageFitness,GraphData.lowestBestFitness);
        double highfit = Double.max(GraphData.highestAverageFitness,GraphData.highestBestFitness)-lowfit;
        double lowdev = Double.min(GraphData.lowestAverageDev,GraphData.lowestBestDev);
        double highdev = Double.max(GraphData.highestBestDev,GraphData.highestAverageDev)-lowdev;
        double lowconc = Double.min(GraphData.lowestAverageConc,GraphData.lowestBestConc);
        double highconc = Double.max(GraphData.highestBestConc,GraphData.highestAverageConc)-lowconc;
        for (int i = 0; i < fitnessAverage.length; i++) {
            series1.getData().add(new XYChart.Data(i, ((fitnessAverage[i]-lowfit)/highfit)));
            series2.getData().add(new XYChart.Data(i, ((fitnessBest[i]-lowfit)/highfit)));
            series3.getData().add(new XYChart.Data(i, (priAverage[i])));
            series4.getData().add(new XYChart.Data(i, (priBest[i])));
            series5.getData().add(new XYChart.Data(i, ((devAverage[i]-lowdev)/highdev)));
            series6.getData().add(new XYChart.Data(i, ((devBest[i]-lowdev)/highdev)));
            series7.getData().add(new XYChart.Data(i, ((concAverage[i]-lowconc)/highconc)));
            series8.getData().add(new XYChart.Data(i, ((concBest[i]-lowconc)/highconc)));

            /*
            series1.getData().add(new XYChart.Data(i, (((fitnessAverage[i]-GraphData.lowestFitness)/GraphData.highestFitness)-GraphData.lowestPri)/GraphData.highestPri));
            series2.getData().add(new XYChart.Data(i, (((fitnessBest[i]-GraphData.lowestFitness)/GraphData.highestFitness)-GraphData.lowestPri)/GraphData.highestPri));
            series3.getData().add(new XYChart.Data(i, (priAverage[i]-GraphData.lowestPri)/GraphData.highestPri));
            series4.getData().add(new XYChart.Data(i, (priBest[i]-GraphData.lowestPri)/GraphData.highestPri));
            series5.getData().add(new XYChart.Data(i, (((devAverage[i]-GraphData.lowestDev)/GraphData.highestDev)-GraphData.lowestPri)/GraphData.highestPri));
            series6.getData().add(new XYChart.Data(i, (((devBest[i]-GraphData.lowestDev)/GraphData.highestDev)-GraphData.lowestPri)/GraphData.highestPri));
            series7.getData().add(new XYChart.Data(i, (((concAverage[i]-GraphData.lowestConc)/GraphData.highestConc)-GraphData.lowestPri)/GraphData.highestPri));
            series8.getData().add(new XYChart.Data(i, (((concBest[i]-GraphData.lowestConc)/GraphData.highestConc)-GraphData.lowestPri)/GraphData.highestPri));
             */
        }


        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        lineChart.getData().add(series4);
        lineChart.getData().add(series5);
        lineChart.getData().add(series6);
        lineChart.getData().add(series7);
        lineChart.getData().add(series8);

        System.out.println();
        System.out.println("lowestDev "+GraphData.lowestDev);
        System.out.println("lowestFitness "+GraphData.lowestFitness);
        System.out.println("lowestConc "+GraphData.lowestConc);
        System.out.println("lowestPri "+GraphData.lowestPri);
        System.out.println("highestDev "+GraphData.highestDev);
        System.out.println("highestFitness "+GraphData.highestFitness);
        System.out.println("highestConc "+GraphData.highestConc);
        System.out.println("highestPri "+GraphData.highestPri);
        System.out.println("generation "+GraphData.generation);
        System.out.println("lastBestDev "+devBest[devBest.length-1]);
        System.out.println("lastBestFitness "+fitnessBest[devBest.length-1]);
        System.out.println("lastBestConc "+concBest[devBest.length-1]);
        System.out.println("lastBestPri "+priBest[devBest.length-1]);
        System.out.println("conc weight "+Main.concWeight);
        System.out.println("dev weight "+Main.devWeight);
        System.out.println();


        Scene scene  = new Scene(lineChart,800,600);

        stage.setScene(scene);
        stage.show();
        System.out.println();
    }

}