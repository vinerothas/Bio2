package main;

import javafx.util.Pair;

import java.util.Arrays;

public class Pop {

    boolean[][] verticalEdges;
    boolean[][] horizontalEdges;

    float conc = Float.MAX_VALUE;
    float[] devSeg;
    float dev = Float.MAX_VALUE;
    float distance;
    short[][] pixelToSegment;
    Index[][] segmentToPixel;
    int rank;
    int mutationUsed;
    boolean child;
    double fitness;
    double pri;

    Pop(Bean bean){
        child = false;
        verticalEdges = new boolean[bean.height-1][bean.width];
        horizontalEdges = new boolean[bean.height][bean.width-1];
        MST.produceRandomMst(bean,verticalEdges,horizontalEdges);

        pixelToSegment = new short[bean.height][bean.width];
        segmentToPixel = new Index[1][bean.size];
        int k = 0;
        for (short i = 0; i < pixelToSegment.length; i++) {
            for (short j = 0; j < pixelToSegment[i].length; j++) {
                pixelToSegment[i][j] = 0;
                segmentToPixel[0][k++] = new Index(i,j);
            }
        }
    }

    Pop(Pop pop){
        verticalEdges = new boolean[pop.verticalEdges.length][];
        for (int i = 0; i < verticalEdges.length; i++) {
            verticalEdges[i] = pop.verticalEdges[i].clone();
        }
        horizontalEdges = new boolean[pop.horizontalEdges.length][];
        for (int i = 0; i < horizontalEdges.length; i++) {
            horizontalEdges[i] = pop.horizontalEdges[i].clone();
        }
        pixelToSegment = new short[pop.pixelToSegment.length][];
        for (int i = 0; i < pixelToSegment.length; i++) {
            pixelToSegment[i] = pop.pixelToSegment[i].clone();
        }
        segmentToPixel = new Index[pop.segmentToPixel.length][];
        for (int i = 0; i < segmentToPixel.length; i++) {
            segmentToPixel[i] = new Index[pop.segmentToPixel[i].length];
            for (int j = 0; j < segmentToPixel[i].length; j++) {
                segmentToPixel[i][j] = new Index(pop.segmentToPixel[i][j].i,pop.segmentToPixel[i][j].j);
            }
        }
        if(pop.devSeg!=null) {
            devSeg = new float[segmentToPixel.length];
            for (int i = 0; i < devSeg.length; i++) {
                devSeg[i] = pop.devSeg[i];
            }
        }
    }

    //TODO can be optimized by finding the new deviation only when segments are created/removed
    void calculateObjectives(Bean bean){
        dev = 0;
        int i = 0;
        devSeg = new float[segmentToPixel.length];
        for (Index[] index:segmentToPixel) {
            float thisdev = Util.segmentDeviation(index, bean);
            dev += thisdev;
            devSeg[i++] = thisdev;
        }
        conc = Util.calculateConc(bean,pixelToSegment);
        PRI.pri(bean, this);
    }

    void calculateFitness(){
        if(!Main.priAsFitness)fitness = dev*Main.devWeight+conc*Main.concWeight;
        if(Main.priAsFitness)fitness = 1/pri;
    }



    //not dominated if a pop is better in at least one objective at not worse in others than another pop
    boolean dominates(Pop pop){
        if(pop.conc>conc && pop.dev>dev)return true;
        return false;
    }

    public String toString() {
        //String s = "dev: "+dev+"   conc: "+conc+"  fitness: "+String.format("%.0f",fitness)+"  PRI: "+pri;
        String s = "dev: "+dev+"   conc: "+conc+"  fitness: "+fitness+"  PRI: "+pri+"  child: "+child;
        return s;
    }


    void printConnections(){
        System.out.println(this);
        char[] seg = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','R','S','T','U','W','X','Y','Z'};

        if(horizontalEdges.length<50) {
            for (int k = 0; k < horizontalEdges.length; k++) {
                StringBuilder line = new StringBuilder();
                for (int l = 0; l < verticalEdges[0].length; l++) {
                    for (int m = 0; m < segmentToPixel.length; m++) {
                        for (int n = 0; n < segmentToPixel[m].length; n++) {
                            if (segmentToPixel[m][n].i == k && segmentToPixel[m][n].j == l) {
                                char c = seg[m % (seg.length - 1)];
                                line.append(c);
                                break;
                            }
                        }
                    }
                }
                System.out.println(line);
            }
            System.out.println();
        }


        StringBuilder line = new StringBuilder();
        line.append("    ");
        for (int i = 0; i < 10; i++) {
            line.append(i+"    ");
        }
        System.out.println(line);

        int i;
        for (i = 0; i < horizontalEdges.length-1; i++) {
            line = new StringBuilder();
            line.append(i+"   ");
            StringBuilder line2 = new StringBuilder();
            line2.append("    ");
            for (int j = 0; j < horizontalEdges[i].length; j++) {
                char c = seg[pixelToSegment[i][j]%(seg.length-1)];
                line.append(c);
                if (horizontalEdges[i][j]){
                    line.append("----");
                }else{
                    line.append("    ");
                }
            }
            char c = seg[pixelToSegment[i][pixelToSegment[i].length-1]%(seg.length-1)];
            line.append(c);
            for (int j = 0; j < verticalEdges[i].length; j++) {
                if (verticalEdges[i][j]){
                    line2.append("|    ");
                }else{
                    line2.append("     ");
                }
            }
            System.out.println(line);
            System.out.println(line2);
        }
        line = new StringBuilder();
        line.append(i+"   ");
        for (int j = 0; j < horizontalEdges[i].length; j++) {
            char c = seg[pixelToSegment[i][j]%(seg.length-1)];
            line.append(c);
            if (horizontalEdges[i][j]){
                line.append("----");
            }else{
                line.append("    ");
            }
        }
        char c = seg[pixelToSegment[i][pixelToSegment[i].length-1]%(seg.length-1)];
        line.append(c);
        System.out.println(line);
        System.out.println();

    }

}
