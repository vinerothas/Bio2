package main;

import javafx.util.Pair;

public class Pop {

    dir[][] genotype;
    enum dir {
        up,
        down,
        left,
        right,
        self
    }

    float conc = Float.MAX_VALUE;
    float dev = Float.MAX_VALUE;
    short[][] pixelToSegment;
    Index[][] segmentToPixel;

    Pop(Bean bean){
        genotype = new dir[bean.height][bean.width];
        MST.produceRandomMst(bean,genotype);

        pixelToSegment = new short[genotype.length][genotype[0].length];
        segmentToPixel = new Index[1][bean.size];
        int k = 0;
        for (short i = 0; i < pixelToSegment.length; i++) {
            for (short j = 0; j < pixelToSegment[i].length; j++) {
                pixelToSegment[i][j] = 1;
                segmentToPixel[0][k++] = new Index(i,j);
            }
        }
    }

    Pop(Pop pop){
        genotype = new dir[pop.genotype.length][pop.genotype[0].length];
        pixelToSegment = new short[genotype.length][genotype[0].length];
        segmentToPixel = new Index[pop.segmentToPixel.length][];
        for (int i = 0; i < genotype.length; i++) {
            for (int j = 0; j < genotype[0].length; j++) {
                genotype[i][j] = pop.genotype[i][j];
                pixelToSegment[i][j] = pop.pixelToSegment[i][j];
            }
        }
        for (int i = 0; i < segmentToPixel.length; i++) {
            segmentToPixel[i] = new Index[pop.segmentToPixel[i].length];
            for (int j = 0; j < segmentToPixel[i].length; j++) {
                segmentToPixel[i][j] = new Index(segmentToPixel[i][j].i,segmentToPixel[i][j].j);
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
        for (int i = 0; i < genotype.length ; i++) {
            StringBuilder line = new StringBuilder();
            StringBuilder line2 = new StringBuilder();
            for (int j = 0; j < genotype[i].length; j++) {
                if(genotype[i][j] == dir.self){
                    line.append("X");
                    if(j != genotype[i].length-1 && genotype[i][j+1] == dir.left){
                        line.append(" <- ");
                    }else{
                        line.append("    ");
                    }
                    if(i != genotype.length-1){
                        if(genotype[i+1][j] != dir.up) {
                            line2.append("     ");
                        }else{
                            line2.append("^    ");
                        }
                    }
                }else {
                    line.append("O");
                    if(genotype[i][j] == dir.left){
                        if(j != genotype[i].length-1 && genotype[i][j+1] == dir.left){
                            line.append(" <- ");
                        }else{
                            line.append("    ");
                        }
                        if(i != genotype.length-1){
                            if(genotype[i+1][j] != dir.up) {
                                line2.append("     ");
                            }else{
                                line2.append("^    ");
                            }
                        }
                    }else if(genotype[i][j] == dir.right){
                        if(genotype[i][j+1] == dir.left){
                            line.append("<-->");
                        }else{
                            line.append(" -> ");
                        }
                        if(i != genotype.length-1){
                            if(genotype[i+1][j] != dir.up) {
                                line2.append("     ");
                            }else{
                                line2.append("^    ");
                            }
                        }
                    }else if(genotype[i][j] == dir.down){
                        if(genotype[i+1][j] == dir.up){
                            line2.append("|    ");
                        }else{
                            line2.append("v    ");
                        }
                        if(j != genotype[i].length-1){
                            if(genotype[i][j+1] != dir.left) {
                                line.append("    ");
                            }else{
                                line.append(" <- ");
                            }
                        }
                    }else if(genotype[i][j] == dir.up){
                        if(i != genotype.length-1 && genotype[i+1][j] == dir.up){
                            line2.append("^    ");
                        }else{
                            line2.append("     ");
                        }
                        if(j != genotype[i].length-1){
                            if(genotype[i][j+1] != dir.left) {
                                line.append("    ");
                            }else{
                                line.append(" <- ");
                            }
                        }
                    }
                }

            }
            System.out.println(line);
            System.out.println(line2);
        }
    }

}
