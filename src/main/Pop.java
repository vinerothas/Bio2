package main;

import javafx.util.Pair;

public class Pop {

    boolean[][] verticalEdges;
    boolean[][] horizontalEdges;

    float conc = Float.MAX_VALUE;
    float dev = Float.MAX_VALUE;
    short[][] pixelToSegment;
    Index[][] segmentToPixel;

    Pop(Bean bean){
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
        verticalEdges = pop.verticalEdges.clone();
        horizontalEdges = pop.horizontalEdges.clone();
        pixelToSegment = pop.pixelToSegment.clone();
        segmentToPixel = new Index[pop.segmentToPixel.length][];
        for (int i = 0; i < segmentToPixel.length; i++) {
            segmentToPixel[i] = new Index[pop.segmentToPixel[i].length];
            for (int j = 0; j < segmentToPixel[i].length; j++) {
                segmentToPixel[i][j] = new Index(pop.segmentToPixel[i][j].i,pop.segmentToPixel[i][j].j);
            }
        }
    }

    void calculateFitness(Bean bean){
        dev = 0;
        for (Index[] index:segmentToPixel) {
            dev += Util.segmentDeviation(index, bean);
        }
        conc = Util.calculateConc(bean,pixelToSegment);
    }


    void printConnections(){
        char[] seg = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','R','S','T','U','W','X','Y','Z'};

//        for (int k = 0; k < horizontalEdges.length; k++) {
//            StringBuilder line = new StringBuilder();
//            for (int l = 0; l < verticalEdges[0].length; l++) {
//                for (int m = 0; m < segmentToPixel.length; m++) {
//                    for (int n = 0; n < segmentToPixel[m].length; n++) {
//                        if(segmentToPixel[m][n].i==k && segmentToPixel[m][n].j==l){
//                            char c = seg[m%(seg.length-1)];
//                            line.append(c);
//                            break;
//                        }
//                    }
//                }
//            }
//            System.out.println(line);
//        }
//        System.out.println();

        int i;
        for (i = 0; i < horizontalEdges.length-1; i++) {
            StringBuilder line = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
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
        StringBuilder line = new StringBuilder();
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
