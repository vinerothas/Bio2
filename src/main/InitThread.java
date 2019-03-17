package main;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class InitThread implements Runnable{

    Random r;
    Bean bean;
    Pop[] population;
    Pop[] children;
    Mutator mutator;
    Main main;
    CountDownLatch latch;

    InitThread(Bean bean, Pop[] population, Pop[] children, Mutator mutator, Main main, CountDownLatch latch){
        this.bean = bean;
        this.population = population;
        this.children = children;
        this.mutator = mutator;
        r = new Random(System.nanoTime());
        this.main = main;
        this.latch = latch;
    }

    public void run() {
        try {
            for (int j = 0; j < population.length; j++) {
                int i = main.getCurrentPopIndex();
                if (i >= Main.popSize) {
                    latch.countDown();
                    return;
                }
                population[i] = new Pop(bean);
                int segmentations = r.nextInt(Main.maxSegments - Main.minSegments) + Main.minSegments - 1;
                for (int k = 0; k < segmentations; k++) {
                    population[i] = MutatorR.mutateR(population[i], bean, mutator);
                }
                population[i].calculateObjectives(bean);
                children[i] = mutator.mutate(population[i], bean);
                children[i].calculateObjectives(bean);
            }
        }catch(Exception e){
            e.printStackTrace();
            latch.countDown();
            return;
        }
        latch.countDown();
    }

}
