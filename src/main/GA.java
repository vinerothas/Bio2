package main;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class GA {

    private Random r;
    private Pop[] population;
    private Pop[] children;
    private Mutator mutator = new Mutator();
    private Main main;
    private Bean bean;

    GA(Bean bean, Main main, Pop[] population, Pop[] children){
        this.main = main;
        this.bean = bean;
        r = new Random(System.nanoTime());
        this.population = population;
        this.children = children;
    }

    void start() {
        for (Pop p: population) {
            p.calculateFitness();
        }
        Arrays.sort(population, new SortPop());

        for (int i = 1; i < Main.generations; i++) {
            main.startTimeC = System.currentTimeMillis();
            chooseNextGen();
            if(Main.graph) GraphData.saveGraphData(population);
            main.elapsedTimeC += System.currentTimeMillis()-main.startTimeC;
            if(i%Mutator.adjustmentRate==0){
                System.out.println("Gen: "+i+" elapsedTime: "+(System.currentTimeMillis()-main.startTime));
                mutator.adjustMutations();
                for (int j = 0; j < Integer.min(15, Main.popSize); j++) {
                    if(!Main.priAsFitness)System.out.println("dev: "+population[j].dev+"   conc: "+population[j].conc+"  fitness: "+String.format("%.0f",population[j].fitness)+"  PRI: "+population[j].pri);
                    if(Main.priAsFitness)System.out.println("dev: "+population[j].dev+"   conc: "+population[j].conc+"  fitness: "+String.format("%.5f",population[j].fitness)+"  PRI: "+population[j].pri);
                }
            }
            mutator.generateChildren(population,children,bean,main);

            if(bean.found70 || System.currentTimeMillis()-main.startTime> Main.runningTime)break;
        }

        System.out.println("Best PRI: "+bean.best+" at time "+(bean.bestTime-main.startTime));
    }

    private void chooseNextGen(){
        for (Pop c: children) {
            c.calculateFitness();
        }
        Arrays.sort(children, new SortPop());
        Pop[] nextGen = new Pop[Main.popSize];

        int ci = 0;
        int pi = 0;

        if(children[0].fitness<population[0].fitness && population[0].fitness-children[0].fitness > Main.fitnessComparison){
            nextGen[0] = children[ci++];
        }else{
            nextGen[0] = population[pi++];
        }

        for (int i = 1; i < Main.popSize ; i++) {
            if(children[ci].fitness<population[pi].fitness && children[ci].fitness-nextGen[i-1].fitness > Main.fitnessComparison){
                nextGen[i] = children[ci++];
            }else{
                nextGen[i] = population[pi++];
            }
        }
        population = nextGen;
    }

    class SortPop implements Comparator<Pop> {
        public int compare(Pop a, Pop b) {
            if ( a.fitness > b.fitness ) return 1;
            else if ( a.fitness == b.fitness ) return 0;
            else return -1;
        }
    }

}
