package main;

import java.util.Random;

public class MutatorA {

    //add a connecting edge to the segment with the smallest number of pixels
    //must call calculate fitness before using
    static Pop mutateSA(Pop pop, Bean bean, Mutator mutator) {
        if (pop.segmentToPixel.length == Main.minSegments) {
            return MutatorR.mutateR(pop, bean, mutator);
        }

        int smallestSegment = -1;
        int smallestSize = Integer.MAX_VALUE;
        for (int i = 0; i < pop.segmentToPixel.length; i++) {
            if (pop.segmentToPixel[i].length < smallestSize) {
                smallestSize = pop.segmentToPixel.length;
                smallestSegment = i;
            }
        }

        int h = -1;
        int w = -1;
        boolean vertical = false;
        for (int i = 0; i < pop.segmentToPixel[smallestSegment].length; i++) {
            Index index = pop.segmentToPixel[smallestSegment][i];
            int j = index.i;
            int k = index.j;
            if (j != pop.verticalEdges.length && !pop.verticalEdges[j][k]) {
                if (pop.pixelToSegment[j + 1][k] != smallestSegment) {
                    h = j;
                    w = k;
                    vertical = true;
                    break;
                }
            }
            if (j != 0 && !pop.verticalEdges[j - 1][k]) {
                if (pop.pixelToSegment[j - 1][k] != smallestSegment) {
                    h = j - 1;
                    w = k;
                    vertical = true;
                    break;
                }
            }
            if (k != pop.horizontalEdges[j].length && !pop.horizontalEdges[j][k]) {
                if (pop.pixelToSegment[j][k + 1] != smallestSegment) {
                    h = j;
                    w = k;
                    vertical = false;
                    break;
                }
            }
            if (k != 0 && !pop.horizontalEdges[j][k - 1]) {
                if (pop.pixelToSegment[j][k - 1] != smallestSegment) {
                    h = j;
                    w = k - 1;
                    vertical = false;
                    break;
                }
            }
        }

        Pop pop1 = addEdge(pop, bean, h, w, vertical);
        pop1.mutationUsed = Mutator.saID;
        mutator.mutationUsed[mutator.saID]++;
        return pop1;
    }

    static Pop mutateA(Pop pop, Bean bean, Mutator mutator) {
        if (pop.segmentToPixel.length <= Main.minSegments) {
            return MutatorR.mutateR(pop, bean, mutator);
        }
        Random r = new Random(System.nanoTime());
        boolean vertical = r.nextBoolean();
        boolean[][] edges;
        int hd = 0;
        int wd = 0;
        if (vertical) { //horizontal less width on array, more width on next pixel
            edges = pop.verticalEdges;
            hd = 1;
        } else {
            edges = pop.horizontalEdges;
            wd = 1;
        }

        int h = r.nextInt(bean.height - hd);
        int w = r.nextInt(bean.width - wd);
        int h2 = h + hd;
        int w2 = w + wd;
        int maxTries = 300;
        int tries = 0;
        while (edges[h][w] || pop.pixelToSegment[h][w] == pop.pixelToSegment[h2][w2]) {
            if (tries++ == maxTries) return MutatorR.mutateR(pop, bean, mutator);
            h = r.nextInt(bean.height - hd);
            w = r.nextInt(bean.width - wd);
            h2 = h + hd;
            w2 = w + wd;
        }

        Pop pop1 = addEdge(pop, bean, h, w, vertical);
        pop1.mutationUsed = Mutator.aID;
        mutator.mutationUsed[mutator.aID]++;
        return pop1;
    }

    private static Pop addEdge(Pop pop, Bean bean, int h, int w, boolean vertical) {
        Pop pop2 = new Pop(pop);
        int h2 = h;
        int w2 = w;
        int dir;
        if (vertical) {
            dir = 1;
            h2++;
            pop2.verticalEdges[h][w] = true;
        } else {
            dir = 3;
            w2++;
            pop2.horizontalEdges[h][w] = true;
        }

        short oldSegment = pop2.pixelToSegment[h2][w2];
        short newSegment = pop2.pixelToSegment[h][w];

        if (newSegment < oldSegment) {
            propagateSegment(pop2, h2, w2, oldSegment, newSegment, dir);
        } else {
            short a = oldSegment;
            oldSegment = newSegment;
            newSegment = a;
            propagateSegment(pop2, h, w, oldSegment, newSegment, dir + 1);
        }
        //System.out.println("MutateA vertical: "+vertical+"    h: "+h+"     w: "+w);


        Index[][] segmentToPixel = new Index[pop2.segmentToPixel.length - 1][];
        for (int i = 0; i < pop2.segmentToPixel.length - 1; i++) {
            if (i == oldSegment) {//move last segment here and update pixelToSegment
                segmentToPixel[i] = pop2.segmentToPixel[pop2.segmentToPixel.length - 1];
                for (int k = 0; k < segmentToPixel[i].length; k++) {
                    pop2.pixelToSegment[segmentToPixel[i][k].i][segmentToPixel[i][k].j] = (short) i;
                }
            } else if (i == newSegment) { //concatenate segments
                int l1 = pop2.segmentToPixel[oldSegment].length;
                int l2 = pop2.segmentToPixel[newSegment].length;
                segmentToPixel[i] = new Index[l1 + l2];
                for (int j = 0; j < l1; j++) {
                    segmentToPixel[i][j] = pop2.segmentToPixel[oldSegment][j];
                }
                for (int j = l1; j < l2 + l1; j++) {
                    segmentToPixel[i][j] = pop2.segmentToPixel[newSegment][j - l1];
                }
            } else {
                segmentToPixel[i] = pop2.segmentToPixel[i];
            }
        }
        pop2.segmentToPixel = segmentToPixel;

        return pop2;
    }


    static void propagateSegment(Pop pop, int h, int w, int oldSegment, short newSegment, int dir) {
        if (pop.pixelToSegment[h][w] == oldSegment) {
            pop.pixelToSegment[h][w] = newSegment;
        } else {
            return; //already marked
        }
        if (dir != 2 && h < pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]) { //down
            propagateSegment(pop, h + 1, w, oldSegment, newSegment, 1);
        }
        if (dir != 1 && h > 0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h - 1][w]) { // up
            propagateSegment(pop, h - 1, w, oldSegment, newSegment, 2);
        }
        if (dir != 4 && h < pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]) { //right
            propagateSegment(pop, h, w + 1, oldSegment, newSegment, 3);
        }
        if (dir != 3 && w > 0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w - 1]) { // left
            propagateSegment(pop, h, w - 1, oldSegment, newSegment, 4);
        }
    }

}
