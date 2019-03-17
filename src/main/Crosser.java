package main;

import java.util.ArrayList;
import java.util.Iterator;

public class Crosser {

    static Pop cross(Pop receiver,Pop giver, Bean bean, Mutator mutator) {

        int bestSegment = -1;
        double bestValue = Double.MAX_VALUE;
        for (int i = 0; i < giver.segmentToPixel.length; i++) {
            if(giver.segmentToPixel.length>Main.minCrossSegment) {
                double value = giver.devSeg[i] / (double) giver.segmentToPixel.length;
                if (value < bestValue) {
                    bestSegment = i;
                    bestValue = value;
                }
            }
        }

        if(bestSegment==-1){
            return mutator.mutate(receiver,bean);
        }

        receiver = new Pop(receiver);

        Index[] segment = giver.segmentToPixel[bestSegment];
        //System.out.println(segment.length);
        for (Index index: segment) {
            int i = index.i;
            int j = index.j;
            if(j<receiver.horizontalEdges.length){
                receiver.horizontalEdges[i][j] = false;
                if(i!=0)receiver.horizontalEdges[i-1][j] = false;
            }
            if(i<receiver.verticalEdges.length){
                receiver.verticalEdges[i][j] = false;
                if(j!=0)receiver.verticalEdges[i][j-1] = false;
            }
        }

        for (Index index: segment) {
            int i = index.i;
            int j = index.j;
            if(j<receiver.horizontalEdges.length)receiver.horizontalEdges[i][j] = giver.horizontalEdges[i][j];
            if(i<receiver.verticalEdges.length)receiver.verticalEdges[i][j] = giver.verticalEdges[i][j];
        }

        for (int i = 0; i < receiver.pixelToSegment.length; i++) {
            for (int j = 0; j < receiver.pixelToSegment[i].length; j++) {
                receiver.pixelToSegment[i][j] = -1;
            }
        }

        ArrayList<ArrayList<Index>> newSegments = new ArrayList<>();
        short newSegment = 0;
        for (int i = 0; i < receiver.pixelToSegment.length; i++) {
            for (int j = 0; j < receiver.pixelToSegment[i].length; j++) {
                if(receiver.pixelToSegment[i][j] == -1){
                    ArrayList<Index> list = new ArrayList<>();
                    propagateSegment(receiver,i,j,newSegment++,list,-1);
                    newSegments.add(list);
                }
            }
        }

        receiver.segmentToPixel = new Index[newSegments.size()][];
        Iterator<ArrayList<Index>> it = newSegments.iterator();
        int i = 0;
        while(it.hasNext()){
            receiver.segmentToPixel[i++] = it.next().toArray(new Index[0]);
        }

        receiver.mutationUsed = Mutator.xID;
        mutator.mutationUsed[Mutator.xID]++;
        return receiver;
    }

    static void propagateSegment(Pop pop, int h, int w, short newSegment, ArrayList<Index> newSegmentList, int dir){
        if(pop.pixelToSegment[h][w] == -1){
            pop.pixelToSegment[h][w]=newSegment;
            newSegmentList.add(new Index((short)h,(short)w));
        } else{
            return; //already marked
        }
        if(dir != 2 && h<pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]){
            propagateSegment(pop, h+1, w,newSegment,newSegmentList,1);
        }
        if(dir != 1 && h>0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h-1][w]){
            propagateSegment(pop, h-1, w,newSegment,newSegmentList,2);
        }
        if(dir != 4 && h<pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]){
            propagateSegment(pop, h, w+1,newSegment,newSegmentList,3);
        }
        if(dir != 3 && w>0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w-1]){
            propagateSegment(pop, h, w-1,newSegment,newSegmentList,4);
        }
    }
}
