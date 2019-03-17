package main;

import java.awt.image.WritableRaster;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class PRI {

    static int pixelRangeCheck = 4;

    static void pri(Bean bean, Pop pop){

        int outline = bean.height*2+bean.width*2-4;


        boolean[][] edges = new boolean[bean.height][bean.width];

        for (int i = 0; i < bean.height; i++) {
            edges[i][0] = true;
            edges[i][bean.width-1] = true;
        }
        for (int i = 0; i < bean.width; i++) {
            edges[0][i] = true;
            edges[bean.height-1][i] = true;
        }

        for (int i = 1; i < bean.height - 1; i++) {
            for (int j = 1; j < bean.width - 1; j++) {
                if (pop.pixelToSegment[i][j] != pop.pixelToSegment[i + 1][j] ||
                        pop.pixelToSegment[i][j] != pop.pixelToSegment[i][j + 1] ||
                        pop.pixelToSegment[i][j] != pop.pixelToSegment[i + 1][j + 1]) {
                    edges[i][j] = true;
                }
            }
        }
        if(Main.recompute)edges = Util.recompute(edges);

        //Writer.writeEdges(bean,1,edges);
       // System.out.println("new");
        double best = 0;
        for (int k = 0; k<bean.gtEdge.length; k++) {
            double score = compareEdges(bean.gtEdge[k],     edges, outline);
            double score2 = compareEdges(edges,     bean.gtEdge[k],outline);
            score = Double.min(score,score2);
            if(score>best) best = score;
        }
        pop.pri = best;
        if(best>bean.best) {
            System.out.println("Best PRI: "+best);
            bean.best = best;
            bean.bestTime = System.currentTimeMillis();

            if(best>=0.6){
                if(Main.printNewBest) {
                    Writer.writeSolution(pop, bean, true, best * 100,true);
                    Writer.writeSolution(pop, bean, false, best * 100,true);
                }
                if(best>=0.65){
                    if(best>=Main.stoppingPri){
                        if(!bean.found70){
                            System.out.println("70 found!");
                            bean.found60 = true;
                            bean.found65 = true;
                            bean.found70 = true;
                            Writer writer = new Writer();
                            //writer.writeSolution(pop,bean,true, 70);
                            //writer.writeSolution(pop,bean,false, 70);
                        }
                    }else if(!bean.found65){
                        System.out.println("65 found!");
                        bean.found60 = true;
                        bean.found65 = true;
                        Writer writer = new Writer();
                        //writer.writeSolution(pop,bean,true, 65);
                        //writer.writeSolution(pop,bean,false, 65);
                    }
                }else{
                    if(!bean.found60){
                        System.out.println("60 found!");
                        bean.found60 = true;
                        Writer writer = new Writer();
                        //writer.writeSolution(pop,bean,true, 60);
                        //writer.writeSolution(pop,bean,false, 60);
                    }
                }
            }
        }
        //System.out.println(best);


    }

    private static double compareEdges(boolean[][] p1, boolean[][] p2,int outline){
        /*
        int counter = outline;
        int numberOfBlackPixels = outline;
        for (int i = 1; i < p1.length - 1; i++) {
            for (int j = 1; j < p1[0].length - 1; j++) {
         */
        int counter = 0;
        int numberOfBlackPixels = 0;
        for (int i = 0; i < p1.length; i++) {
            for (int j = 0; j < p1[0].length; j++) {
                if (p1[i][j]) {
                    //black color
                    numberOfBlackPixels+=1;
                    if (p2[i][j]){
                        counter++;
                    }else{
                        boolean correctFound = false;
                        for (int l = max(0,i-pixelRangeCheck); l < min(p1.length,i+pixelRangeCheck); l++) {
                            if(correctFound) break;
                            int a = max(0,j-pixelRangeCheck);
                            int b = min(p1[0].length,j+pixelRangeCheck);
                            for (int m = a; m < b; m++) {
                                if(p2[l][m]){
                                    counter++;
                                    correctFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(counter+" "+numberOfBlackPixels);
        return counter/(double)numberOfBlackPixels;

    }

}
