package main;

import java.util.Random;

public class Mutator {

    final static int mutations = 5; //number of different mutations
    double[] mutateChances = new double[]{0.1,0.35,0.1,0.1,0.25};
    double[] tresholds = new double[mutations];
    final static int xID = 0;
    final static int rID = 1;
    final static int aID = 2;
    final static int srID = 3;
    final static int saID = 4;
    final static int adjustmentRate = Main.nsga ? 1000/ Main.popSize : 1000/Main.popSize*3;
    int[] mutationUsed = new int[mutations];
    int[] mutationSucces = new int[mutations];
    static String[] mutationNames = new String[]{"X","R","A","SR","SA"};
    private Random r;
    private static double pity = 0.05;
    private static double pityTreshold = 0.01;
    private static double momentum = 0.7;//how much of the previous chance to propagate

    Mutator(){
        r = new Random(System.nanoTime());
        tresholds[0] = mutateChances[0];
        for (int i = 1; i < tresholds.length; i++) {
            tresholds[i] = tresholds[i-1]+mutateChances[i];
        }
    }

    void generateChildren(Pop[] population, Pop[] children,Bean bean, Main main){
        main.startTimeM = System.currentTimeMillis();
        for (int j = 0; j < population.length; j++) {
            if(population[j].child){
                population[j].child = false;
                mutationSucces[population[j].mutationUsed]++;
            }
            float f = r.nextFloat();

            Pop pop = main.tournamentSelection(population);
            if(f<tresholds[xID] && j+1!=population.length){
                Pop pop2 = main.tournamentSelection(population);
                while(pop==pop2){
                    pop2 = main.tournamentSelection(population);
                }
                children[j] = Crosser.cross(pop,pop2,bean,this);
                children[j+1] = Crosser.cross(pop2,pop,bean,this);
                children[j].calculateObjectives(bean);
                children[j].child = true;
                j++;
            }else if(f<tresholds[rID]) {
                children[j] =  MutatorR.mutateR(pop, bean, this);
            } else if (f<tresholds[aID]) {
                children[j] =  MutatorA.mutateA(pop, bean, this);
            }else if (f<tresholds[srID]) {
                children[j] =  MutatorR.mutateSR(pop, bean, this);
            }else{
                children[j] =  MutatorA.mutateSA(pop, bean, this);
            }
            children[j].calculateObjectives(bean);
            children[j].child = true;
        }
        main.elapsedTimeM += System.currentTimeMillis()-main.startTimeM;
    }

    Pop mutate(Pop pop, Bean bean) {
        float f = r.nextFloat();
        while(f<tresholds[xID]){
            f = r.nextFloat();
        }
        if (f<tresholds[rID]) {
            return MutatorR.mutateR(pop, bean, this);
        } else if (f<tresholds[aID]) {
            return MutatorA.mutateA(pop, bean, this);
        }else if (f<tresholds[srID]) {
            return MutatorR.mutateSR(pop, bean, this);
        }else{
            return MutatorA.mutateSA(pop, bean, this);
        }
    }

    void adjustMutations(){
        String s = "";
        for (int i = 0; i < mutations; i++) {
            s += mutationNames[i]+"_MU:"+mutationUsed[i]+"    ";
        }
        System.out.println(s);
        s = "";
        for (int i = 0; i < mutations; i++) {
            s += mutationNames[i]+"_MS:"+mutationSucces[i]+"    ";
        }
        System.out.println(s);
        double[] mr = new double[mutations];
        for (int i = 0; i < mr.length; i++) {
            if(mutationUsed[i]!=0) mr[i] = mutationSucces[i]/(float)(mutationUsed[i]);
            else if(mutationSucces[i]!=0) mr[i] = 1/(double)mutations;
            else mr[i] = 0;
        }
        mr[xID]/=4;
        mr[srID]/=4;
        //s = "";
        //for (int i = 0; i < mutateChances.length; i++) {
        //    s += mutationNames[i]+"_C:"+String.format("%.3f",mutateChances[i])+"    ";
        //}
        //System.out.println(s);

       // s = "";
        //for (int i = 0; i < mr.length; i++) {
         //   s += mutationNames[i]+"_R:"+String.format("%.3f",mr[i])+"    ";
        //}
        //System.out.println(s);

        double sum = 0;
        for (int i = 0; i < mutateChances.length; i++) {
            mutateChances[i] = mr[i]+momentum*mutateChances[i];
            if(mutateChances[i]<pityTreshold)mutateChances[i] = pity;
            sum+= mutateChances[i];
        }

        for (int i = 0; i < mutateChances.length; i++) {
            mutateChances[i] = mutateChances[i]/sum;
        }

        s = "";
        for (int i = 0; i < mutateChances.length; i++) {
            s += mutationNames[i]+"_C:"+String.format("%.3f",mutateChances[i])+"    ";
        }
        System.out.println(s);

        //s = mutationNames[0]+"_T:"+String.format("%.3f",tresholds[0])+"    ";
        tresholds[0] = mutateChances[0];
        for (int i = 1; i < tresholds.length; i++) {
            tresholds[i] = tresholds[i-1]+mutateChances[i];
            //s += mutationNames[i]+"_T:"+String.format("%.3f",tresholds[i])+"    ";
        }
        //System.out.println(s);

        for (int j = 0; j < mutationUsed.length; j++) {
            mutationUsed[j] = 0;
            mutationSucces[j] = 0;
        }
    }
}
