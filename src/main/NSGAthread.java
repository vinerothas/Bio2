package main;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class NSGAthread implements Runnable {

    Pop[] population;
    Random r;
    Mutator m;
    Main main;
    Pop[] children;
    Bean bean;
    CountDownLatch latch;
    NSGA nsga;

    NSGAthread(){
        r = new Random(System.nanoTime());
    }

    public void run(){
        for (int j = 0; j < population.length; j++) {
            int i = nsga.getNextPopIndex();
            if (i >= population.length) {
                latch.countDown();
                return;
            }
            float f = r.nextFloat();
            Pop pop = main.tournamentSelection(population);

            if (f < m.tresholds[Mutator.xID] && i + 1 != population.length) {
                int i2 = nsga.getNextPopIndex();
                if (i2 < population.length) {
                    Pop pop2 = main.tournamentSelection(population);
                    while (pop == pop2) {
                        pop2 = main.tournamentSelection(population);
                    }
                    children[i] = Crosser.cross(pop, pop2, bean, m);
                    children[i2] = Crosser.cross(pop2, pop, bean, m);
                    children[i2].calculateObjectives(bean);
                    children[i2].child = true;
                } else {
                    children[i] = m.mutate(pop, bean);
                }
            } else if (f < m.tresholds[Mutator.rID]) {
                children[i] = MutatorR.mutateR(pop, bean, m);
            } else if (f < m.tresholds[Mutator.aID]) {
                children[i] = MutatorA.mutateA(pop, bean, m);
            } else if (f < m.tresholds[Mutator.srID]) {
                children[i] = MutatorR.mutateSR(pop, bean, m);
            } else {
                children[i] = MutatorA.mutateSA(pop, bean, m);
            }
            children[i].calculateObjectives(bean);
            children[i].child = true;
        }
        latch.countDown();
    }

}
