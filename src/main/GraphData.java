package main;

import java.util.ArrayList;

public class GraphData {

    static ArrayList<Double> fitnessAverage = new ArrayList<>();
    static ArrayList<Double> fitnessBest = new ArrayList<>();
    static ArrayList<Double> priAverage = new ArrayList<>();
    static ArrayList<Double> priBest = new ArrayList<>();
    static ArrayList<Double> devAverage = new ArrayList<>();
    static ArrayList<Double> devBest = new ArrayList<>();
    static ArrayList<Double> concAverage = new ArrayList<>();
    static ArrayList<Double> concBest = new ArrayList<>();
    static double lowestDev = Double.MAX_VALUE;
    static double lowestFitness = Double.MAX_VALUE;
    static double lowestConc = Double.MAX_VALUE;
    static double lowestPri = 1;
    static double highestDev = 0;
    static double highestFitness = 0;
    static double highestConc = 0;
    static double highestPri = 0;
    static double lowestAverageDev = Double.MAX_VALUE;
    static double lowestAverageFitness = Double.MAX_VALUE;
    static double lowestAverageConc = Double.MAX_VALUE;
    static double lowestAveragePri = 1;
    static double highestAverageDev = 0;
    static double highestAverageFitness = 0;
    static double highestAverageConc = 0;
    static double highestAveragePri = 0;
    static double lowestBestDev = Double.MAX_VALUE;
    static double lowestBestFitness = Double.MAX_VALUE;
    static double lowestBestConc = Double.MAX_VALUE;
    static double lowestBestPri = 1;
    static double highestBestDev = 0;
    static double highestBestFitness = 0;
    static double highestBestConc = 0;
    static double highestBestPri = 0;
    static double generation = 0;

    static void saveGraphData(Pop[] population){
        if(generation++<5)return;
        long totalDev = 0;
        double totalConc = 0;
        double totalPri = 0;
        double totalFitness = 0;
        double bestDev = Double.MAX_VALUE;
        double bestConc = Double.MAX_VALUE;
        double bestPri = 0;
        double bestFitness = Double.MAX_VALUE;

        for (int i = 0; i < population.length; i++) {
            totalDev+=population[i].dev;
            totalConc+=population[i].conc;
            totalPri+=population[i].pri;
            totalFitness+=population[i].fitness;
            if(population[i].dev < bestDev) bestDev = population[i].dev;
            if(population[i].conc < bestConc) bestConc = population[i].conc;
            if(population[i].fitness < bestFitness) bestFitness = population[i].fitness;
            if(population[i].pri > bestPri) bestPri = population[i].pri;
            if(population[i].dev < lowestDev) lowestDev = population[i].dev;
            if(population[i].conc < lowestConc) lowestConc = population[i].conc;
            if(population[i].fitness < lowestFitness) lowestFitness = population[i].fitness;
            if(population[i].pri < lowestPri) lowestPri = population[i].pri;
            if(population[i].dev > highestDev) highestDev = population[i].dev;
            if(population[i].conc > highestConc) highestConc = population[i].conc;
            if(population[i].fitness > highestFitness) highestFitness = population[i].fitness;
            if(population[i].pri > highestPri) highestPri = population[i].pri;
        }
        double fitnessAvg = totalFitness/(double)population.length;
        double priAvg = totalPri/(double)population.length;
        double devAvg = totalDev/(double)population.length;
        double concAvg = totalConc/(double)population.length;
        fitnessAverage.add(fitnessAvg);
        priAverage.add(priAvg);
        devAverage.add(devAvg);
        concAverage.add(concAvg);
        fitnessBest.add(bestFitness);
        priBest.add(bestPri);
        devBest.add(bestDev);
        concBest.add(bestConc);
        if(devAvg < lowestAverageDev) lowestAverageDev = devAvg;
        if(concAvg < lowestAverageConc) lowestAverageConc = concAvg;
        if(fitnessAvg < lowestAverageFitness) lowestAverageFitness = fitnessAvg;
        if(priAvg < lowestAveragePri) lowestAveragePri = priAvg;
        if(devAvg > highestAverageDev) highestAverageDev = devAvg;
        if(concAvg > highestAverageConc) highestAverageConc = concAvg;
        if(fitnessAvg > highestAverageFitness) highestAverageFitness = fitnessAvg;
        if(priAvg > highestAveragePri) highestAveragePri = priAvg;

        if(bestDev < lowestBestDev) lowestBestDev = bestDev;
        if(bestFitness < lowestBestFitness) lowestBestFitness = bestFitness;
        if(bestConc < lowestBestConc) lowestBestConc = bestConc;
        if(bestPri < lowestBestPri) lowestBestPri = bestPri;
        if(bestDev > highestBestDev) highestBestDev = bestDev;
        if(bestFitness > highestBestFitness) highestBestFitness = bestFitness;
        if(bestConc > highestBestConc) highestBestConc = bestConc;
        if(bestPri > highestBestPri) highestBestPri = bestPri;
    }
}
