package main;

import javafx.util.Pair;

import java.util.*;

public class NSGA {

    static int popSize = 30;
    Random r;
    static int generations = 100;

    public ArrayList<ArrayList<Pop>> start(Bean bean) {
        Pop[] population = new Pop[popSize];
        r = new Random(System.nanoTime());
        Pop[] children = new Pop[popSize];

        for (int i = 0; i < population.length; i++) {
            population[i] = new Pop(bean);
            population[i] = mutate(population[i], bean);
            population[i].calculateFitness(bean);
            //population[0].printConnections();
            children[i] = mutate(population[i], bean);
            children[i].calculateFitness(bean);
        }

        for (int i = 0; i < generations; i++) {
            chooseNextGen(population, children);
            System.out.println("Gen: "+i);
            for (int j = 0; j < 10; j++) {
                System.out.println("dev: "+population[j].dev+"     conc: "+population[j].conc+"    dist: "+population[j].distance);
            }
            //population[0].printConnections();
            for (int j = 0; j < population.length; j++) {
                children[j] = mutate(population[j], bean);
                children[j].calculateFitness(bean);
            }
        }
        return chooseNextGen(population,children);

    }

    ArrayList<ArrayList<Pop>> chooseNextGen(Pop[] population, Pop[] children) {
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
        } while (!removed.isEmpty());


        Iterator<ArrayList<Pop>> it = fronts.iterator();
        int i = 0;
        Comparator<Pop> byDev = (Pop o1, Pop o2) -> (int) (o1.dev - o2.dev);
        Comparator<Pop> byConc = (Pop o1, Pop o2) -> (int) (o1.conc - o2.conc);
        Comparator<Pop> byDist = (Pop o1, Pop o2) -> (int) (o1.distance - o2.distance);
        while (it.hasNext()) {
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
                population[i++] = it2.next();
                if(i==popSize) return fronts; //we done here boiii
            }
        }

        return fronts;
    }

    Pop mutate(Pop pop, Bean bean) {
        if (r.nextBoolean()) {
            return Mutator.mutateR(pop, bean);
        } else {
            return Mutator.mutateA(pop, bean);
        }
    }

}
