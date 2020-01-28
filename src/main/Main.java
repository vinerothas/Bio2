package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    long startTimeI = 0;
    long elapsedTimeI = 0;
    long startTimeC = 0;
    long elapsedTimeC=0;
    long startTimeM = 0;
    long elapsedTimeM=0;
    long startTimeP = 0;
    long elapsedTimeP=0;
    long startTime;


    final static int pixelRangeCheck = 4; // = 4 in supplied code
    final static int testNumber = 3;
    final static boolean nsga = false;
    final static int popSize = 50;
    final static int tournamentSize = 5;   //popSize/3;
    final static int generations = 1000000;
    final static double minutesRunning = 50;
    final static double runningTime = 1000*60*minutesRunning;
    final static int maxSegments = 30;
    final static int minSegments = 2;
    final static double devWeight = 0.002;
    final static double concWeight = 0.2;
    final static double fitnessComparison = 0.000001;
    final static int minCrossSegment = 10;
    final static boolean recompute = false;
    final static boolean graph = false;
    final static boolean priAsFitness = true; // MUST BE false DURING DEMO
    final static double stoppingPri = 0.9999;    // SHOULD BE 0.7 DURING DEMO
    final static boolean printNewBest = true;// SHOULD BE true DURING DEMO
    final static boolean printGreen = true;
    final static int threads = 5;

    Random r;
    ThreadPoolExecutor executor;
    private int currentPopIndex = 0;


    @Override
    public void start(Stage primaryStage){
        startTime = System.currentTimeMillis();
        System.out.println("Start. Pop: "+ popSize+"   Minutes to run: "+ minutesRunning+"   NSGA: "+ nsga);

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.setMaximumPoolSize(threads);
        executor.setCorePoolSize(threads);
        executor.setKeepAliveTime(60, TimeUnit.SECONDS);

        r = new Random(System.nanoTime());
        Bean bean = new Bean();
        Reader reader = new Reader();
        reader.readImage(testNumber, bean);
        reader.readGT(testNumber,bean);

        Pop[] population = new Pop[popSize];
        Pop[] children = new Pop[popSize];
        initPop(population,children, bean);

        if(nsga) {
            NSGA nsga = new NSGA(bean,this,population,children);
            ArrayList<ArrayList<Pop>> fronts = nsga.start();
            DrawPareto.plot(primaryStage,fronts);
        }else{
            GA ga = new GA(bean,this,population,children);
            ga.start();
            if(graph)FitnessPlot.plot(primaryStage);
        }

        //System.out.println("Drawing of all pareto solutions is off");
        startTimeP = System.currentTimeMillis();
        for (int i = 0; i < Main.popSize; i++) {
            if(!nsga || population[i].rank==1){
                Writer.writeSolution(population[i],bean,true,population[i].pri*100,false);
                Writer.writeSolution(population[i],bean,false,population[i].pri*100,false);
            }
        }
        elapsedTimeP = System.currentTimeMillis()-startTimeP;

        printTime();
    }

    synchronized int getCurrentPopIndex(){
        return currentPopIndex++;
    }

    private void initPop(Pop[] population,Pop[] children, Bean bean){
        Mutator mutator = new Mutator();

        startTimeI = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(Main.threads);
        for (int j = 0; j < threads; j++) {
            InitThread thread = new InitThread(bean,population,children,mutator,this,latch);
            executor.submit(() -> {
                thread.run();
                return null;
            });
        }
        try {
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1); // \_(0_o)_/
        }
        elapsedTimeI = System.currentTimeMillis()-startTimeI;
    }

    Pop tournamentSelection(Pop[] population){
        int bestIndex = r.nextInt(population.length);
        if(nsga){
            for (int i = 0; i < Main.tournamentSize - 1; i++) {
                int i1 = r.nextInt(population.length);
                if (population[i1].rank <= population[bestIndex].rank) {
                    if (population[i1].distance > population[bestIndex].rank) {
                        bestIndex = i1;
                    }
                }
            }
        }else{
            for (int i = 0; i < Main.tournamentSize - 1; i++) {
                int i1 = r.nextInt(population.length);
                if (population[i1].fitness < population[bestIndex].fitness) {
                    bestIndex = i1;
                }
            }
        }
        return population[bestIndex];
    }

    /*Pair<Pop,Pop> doubleTournamentSelection(Pop[] population){
        int i1 = r.nextInt(population.length);
        int i2 = r.nextInt(population.length);
        int bestIndex;
        int secondBestIndex;

        if (population[i1].fitness > population[i2].fitness) {
            bestIndex = i1;
            secondBestIndex = i2;
        } else {
            bestIndex = i2;
            secondBestIndex = i1;
        }

        for (int i = 0; i < Param.tournamentSize - 2; i++) {
            i1 = r.nextInt(population.length);
            if (population[i1].fitness > population[secondBestIndex].fitness) {
                if (population[i1].fitness > population[bestIndex].fitness) {
                    secondBestIndex = bestIndex;
                    //IT SHOULD HAVE BEEN i1 INSTEAD OF i BUT IT WORKED WELL SO I DUNNO
                    bestIndex = i;
                } else {
                    //IT SHOULD HAVE BEEN i1 INSTEAD OF i BUT IT WORKED WELL SO I DUNNO
                    secondBestIndex = i;
                }
            }
        }

        if(nsga){
            for (int i = 0; i < Main.tournamentSize - 1; i++) {
                int i1 = r.nextInt(population.length);
                if (population[i1].rank <= population[bestIndex].rank) {
                    if (population[i1].distance > population[bestIndex].rank) {
                        bestIndex = i1;
                    }
                }
            }
        }else{
            for (int i = 0; i < Main.tournamentSize - 1; i++) {
                int i1 = r.nextInt(population.length);
                if (population[i1].fitness < population[bestIndex].fitness) {
                    bestIndex = i1;
                }
            }
        }
        return population[bestIndex];
    }*/

    private void printTime(){
        long sum = elapsedTimeM+elapsedTimeI+elapsedTimeC+elapsedTimeP;
        System.out.println("elapsedTimeI: "+elapsedTimeI);
        System.out.println("elapsedTimeC: "+elapsedTimeC);
        System.out.println("elapsedTimeM: "+elapsedTimeM);
        System.out.println("elapsedTimeP: "+elapsedTimeP);
        System.out.println("sum: "+sum);
        float ratioI = elapsedTimeI/(float)sum;
        float ratioC = elapsedTimeC/(float)sum;
        float ratioM = elapsedTimeM/(float)sum;
        float ratioP = elapsedTimeP/(float)sum;
        System.out.println("ratioI: "+ratioI);
        System.out.println("ratioC: "+ratioC);
        System.out.println("ratioM: "+ratioM);
        System.out.println("ratioP: "+ratioP);
    }

    public static void main(String[] args) {
        launch(args);
    }
}