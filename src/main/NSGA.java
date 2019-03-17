package main;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class NSGA {

    //lagr 1000 bilder med dev og conc, finn PRI for alle og kjør linear regresjon

    // crossover: take a segment from p1 and put it in p2, do reverse for p2 and p1
    //      this may break other segments into multiple parts
    //      find a segment boundary before transplant
    //      create completely new pixelToSegment initialized to -1
    //      propagate from every pixel if pixelToSegment==-1, save Indexes underway to save to segmentToPixel
    // add edge on smallest distance for A, SA or new mutator??????
    // mutator that traverses a segment edge, and checks if adding this pixel to a neighbouring edge would decrease total segment deviation????
    //      would require saving the centroid on fitness calculation and updating it underway during mutation

    private Random r;
    private Pop[] population;
    private Pop[] children;
    private Mutator mutator = new Mutator();
    private Main main;
    private  Bean bean;
    private int currentPopIndex = 0;
    private NSGAthread[] threads = new NSGAthread[Main.threads];

    NSGA(Bean bean, Main main, Pop[] population, Pop[] children){
        this.main = main;
        this.bean = bean;
        r = new Random(System.nanoTime());
        this.population = population;
        this.children = children;
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new NSGAthread();
            threads[i].bean = bean;
            threads[i].main = main;
            threads[i].population = population;
            threads[i].children = children;
            threads[i].m = mutator;
            threads[i].nsga = this;
        }
    }

    ArrayList<ArrayList<Pop>> start() {
        for (int i = 1; i < Main.generations; i++) {
            main.startTimeC = System.currentTimeMillis();
            chooseNextGen(population, children);
            main.elapsedTimeC += System.currentTimeMillis()-main.startTimeC;
            if(i%Mutator.adjustmentRate==0){
                System.out.println("Gen: "+i+" elapsedTime: "+(System.currentTimeMillis()-main.startTime));
                mutator.adjustMutations();
                //for (int j = 0; j < 5; j++) {
                //    System.out.println("dev: "+population[j].dev+"     conc: "+population[j].conc+"    dist: "+population[j].distance);
                //}
            }

            main.startTimeM = System.currentTimeMillis();
            for (int j = 0; j < population.length; j++) {
                if (population[j].child) {
                    population[j].child = false;
                    mutator.mutationSucces[population[j].mutationUsed]++;
                }
            }
            CountDownLatch latch = new CountDownLatch(Main.threads);
            for (int j = 0; j < threads.length; j++) {
                threads[j].latch = latch;
                NSGAthread thread = threads[j];
                main.executor.submit(() -> {
                    try {
                        thread.run();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                });
            }
            try {
                latch.await();
            }catch (Exception e){
                e.printStackTrace();
                System.exit(-1); // \_(0_o)_/
            }
            currentPopIndex = 0;
            main.elapsedTimeM += System.currentTimeMillis()-main.startTimeM;

            if(bean.found70 || System.currentTimeMillis()-main.startTime> Main.runningTime)break;
        }

        System.out.println("Best PRI: "+bean.best+" at time "+(bean.bestTime-main.startTime));

        ArrayList<ArrayList<Pop>> fronts = chooseNextGen(population,children);

        return fronts;
    }

    public synchronized int getNextPopIndex(){
        return currentPopIndex++;
    }

    private ArrayList<ArrayList<Pop>> chooseNextGen(Pop[] population, Pop[] children) {
        ArrayList<Pop> generation = new ArrayList<>();
        generation.addAll(Arrays.asList(population));
        generation.addAll(Arrays.asList(children));

        ArrayList<Pop> front = new ArrayList<>();
        ArrayList<Pop> removed = new ArrayList<>();
        ArrayList<ArrayList<Pop>> fronts = new ArrayList<>();
        do {
            generation.addAll(removed);
            removed.clear();
            front.add(generation.remove(0));
            Iterator<Pop> it = generation.iterator();
            while (it.hasNext()) {
                Pop p = it.next();
                Iterator<Pop> it2 = front.iterator();
                boolean dominated = false;
                while (it2.hasNext()) {
                    Pop q = it2.next();
                    if (p.dominates(q)) {
                        it2.remove();
                        removed.add(q);
                    } else if (q.dominates(p)) {
                        dominated = true;
                    }
                }
                if (!dominated) {
                    front.add(p);
                    it.remove();
                }
            }
            ArrayList<Pop> front2 = (ArrayList<Pop>) front.clone();
            fronts.add(front2);
            front.clear();
        } while (!removed.isEmpty() && !generation.isEmpty());


        Iterator<ArrayList<Pop>> it = fronts.iterator();
        int i = 0;
        int r = 0;
        Comparator<Pop> byDev = (Pop o1, Pop o2) -> (int) (o1.dev - o2.dev);
        Comparator<Pop> byConc = (Pop o1, Pop o2) -> (int) (o1.conc - o2.conc);
        Comparator<Pop> byDist = (Pop o1, Pop o2) -> (int) (o2.distance - o1.distance);
        while (it.hasNext()) {
            r++;
            front = it.next();
            front.sort(byDev);

            Iterator<Pop> it2 = front.iterator();
            Pop p1 = it2.next();
            Pop p2;
            Pop p3;
            p1.distance = Float.MAX_VALUE; // first
            if (it2.hasNext()) {
                p2 = it2.next();
                if (it2.hasNext()) {
                    p3 = it2.next();
                    p2.distance = p3.dev - p1.dev;
                    while (it2.hasNext()) {
                        p1 = p2;
                        p2 = p3;
                        p3 = it2.next();
                        p2.distance = p3.dev - p1.dev;
                    }
                    p3.distance = Float.MAX_VALUE; // last
                } else {
                    p2.distance = Float.MAX_VALUE; // last
                }
            }

            front.sort(byConc);
            it2 = front.iterator();
            p1 = it2.next();
            if (it2.hasNext()) {
                p2 = it2.next();
                if (it2.hasNext()) {
                    p3 = it2.next();
                    p2.distance = p2.distance + p3.conc - p1.conc;
                    while (it2.hasNext()) {
                        p1 = p2;
                        p2 = p3;
                        p3 = it2.next();
                        p2.distance = p2.distance + p3.conc - p1.conc;
                    }
                }
            }
            
            front.sort(byDist);
            it2 = front.iterator();
            while (it2.hasNext()) {
                population[i] = it2.next();
                population[i++].rank = r;
                if(i== Main.popSize) {
                    return fronts; //we done here boiii
                }
            }
        }
        return fronts;
    }



}
