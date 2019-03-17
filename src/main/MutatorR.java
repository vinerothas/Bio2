package main;

import java.util.Random;

public class MutatorR {

    //need to have calculated fitness before calling it
    static Pop mutateSR(Pop pop, Bean bean, Mutator mutator){
        //find the edge (in the segment with the biggest deviation per number of pixels) with the highest distance value
        if(pop.segmentToPixel.length>= Main.maxSegments){
            return MutatorA.mutateA(pop,bean, mutator);
        }

        int worstSegment = 0;
        float worstValue = pop.devSeg[0]/(float)pop.segmentToPixel[0].length;
        for (int i = 1; i < pop.devSeg.length ; i++) {
            float nextValue = pop.devSeg[i]/(float)pop.segmentToPixel[i].length;
            if(nextValue>worstValue){
                worstValue = nextValue;
                worstSegment = i;
            }
        }

        float biggestDistance = -1;
        int h = -1;
        int w = -1;
        boolean vertical = false;
        for (int i = 0; i < pop.segmentToPixel[worstSegment].length; i++) {
            Index index = pop.segmentToPixel[worstSegment][i];
            int j = index.i;
            int k = index.j;
            if(j != pop.verticalEdges.length && pop.verticalEdges[j][k]){
                if(pop.pixelToSegment[j+1][k]==worstSegment){
                    if(bean.downDist[j][k]>biggestDistance){
                        biggestDistance = bean.downDist[j][k];
                        h = j;
                        w = k;
                        vertical = true;
                    }
                }
            }
            if(j!=0 && pop.verticalEdges[j-1][k]){
                if(pop.pixelToSegment[j-1][k]==worstSegment){
                    if(bean.upDist[j][k]>biggestDistance){
                        biggestDistance = bean.upDist[j][k];
                        h = j-1;
                        w = k;
                        vertical = true;
                    }
                }
            }
            if(k != pop.horizontalEdges[j].length && pop.horizontalEdges[j][k]){
                if(pop.pixelToSegment[j][k+1]==worstSegment){
                    if(bean.rightDist[j][k]>biggestDistance){
                        biggestDistance = bean.rightDist[j][k];
                        h = j;
                        w = k;
                        vertical = false;
                    }
                }
            }
            if(k != 0 && pop.horizontalEdges[j][k-1]){
                if(pop.pixelToSegment[j][k-1]==worstSegment){
                    if(bean.leftDist[j][k]>biggestDistance){
                        biggestDistance = bean.leftDist[j][k];
                        h = j;
                        w = k-1;
                        vertical = false;
                    }
                }
            }
        }

        Pop pop1 = removeEdge(pop,bean,h,w,vertical);
        pop1.mutationUsed = Mutator.srID;
        mutator.mutationUsed[mutator.srID]++;
        return pop1;
    }

    private static Pop removeEdge(Pop pop, Bean bean, int h, int w, boolean vertical){
        Pop pop2 = new Pop(pop);
        pop = null;

        int h2 = h;
        int w2 = w;
        int dir;

        if(vertical){
            pop2.verticalEdges[h][w] = false;
            h2++;
            dir = 1;
        }else{
            pop2.horizontalEdges[h][w] = false;
            w2++;
            dir = 3;
        }

        //System.out.println("MutateR vertical: "+vertical+"    h: "+h+"     w: "+w);
        Index[] newSegment = new Index[ pop2.segmentToPixel[pop2.pixelToSegment[h][w]].length ];
        IntHolder indexHolder = new IntHolder();
        int oldSegment = pop2.pixelToSegment[h][w];

        propagateNewSegment(pop2, h, w,oldSegment,(short)pop2.segmentToPixel.length, newSegment, indexHolder);
        int index = indexHolder.i;
        Index[] newSegment2 = new Index[index];
        System.arraycopy(newSegment,0,newSegment2,0,index);

        Index[] oldSegment2 = new Index[pop2.segmentToPixel[oldSegment].length-newSegment2.length];
        findOldSegment(pop2,h2,w2,oldSegment,oldSegment2,new IntHolder(),dir);

        Index[][] segmentToPixel = new Index[pop2.segmentToPixel.length+1][];
        for (int i = 0; i < pop2.segmentToPixel.length; i++) {
            if(i != oldSegment){
                segmentToPixel[i] = pop2.segmentToPixel[i];
            }else{
                segmentToPixel[i] = oldSegment2;
            }
        }
        segmentToPixel[pop2.segmentToPixel.length] = newSegment2;
        pop2.segmentToPixel = segmentToPixel;
        return pop2;
    }

    static Pop mutateR(Pop pop, Bean bean, Mutator mutator){
        if(pop.segmentToPixel.length>= Main.maxSegments){
            return MutatorA.mutateA(pop,bean, mutator);
        }

        Random r = new Random(System.nanoTime());
        boolean vertical = r.nextBoolean();
        int h = r.nextInt(bean.height-1);
        int w = r.nextInt(bean.width-1);
        while(vertical&&!pop.verticalEdges[h][w]){
            h = r.nextInt(bean.height-1);
            w = r.nextInt(bean.width);
        }
        while(!vertical&&!pop.horizontalEdges[h][w]){
            h = r.nextInt(bean.height);
            w = r.nextInt(bean.width-1);
        }

        Pop pop1 = removeEdge(pop,bean,h,w,vertical);
        pop1.mutationUsed = Mutator.rID;
        mutator.mutationUsed[Mutator.rID]++;
        return pop1;
    }

    static void propagateNewSegment(Pop pop, int h, int w, int oldSegment, short newSegment, Index[] newSegmentArray, IntHolder index){
        if(pop.pixelToSegment[h][w] == oldSegment){
            pop.pixelToSegment[h][w]=newSegment;
            newSegmentArray[index.i++] = new Index((short)h,(short)w);
        } else{
            return; //already marked
        }
        if(h<pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]){
            propagateNewSegment(pop, h+1, w,oldSegment,newSegment,newSegmentArray, index);
        }
        if(h>0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h-1][w]){
            propagateNewSegment(pop, h-1, w,oldSegment,newSegment,newSegmentArray, index);
        }
        if(h<pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]){
            propagateNewSegment(pop, h, w+1,oldSegment,newSegment,newSegmentArray, index);
        }
        if(w>0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w-1]){
            propagateNewSegment(pop, h, w-1,oldSegment,newSegment,newSegmentArray, index);
        }
    }

    static void findOldSegment(Pop pop, int h, int w, int oldSegment,Index[] oldSegmentArray, IntHolder index, int dir){
        //if(index.i == oldSegmentArray.length){
        //    System.out.println();
        //}
        if(pop.pixelToSegment[h][w] == oldSegment){
            oldSegmentArray[index.i++] = new Index((short)h,(short)w);
        }else{
            return;
        }
        if(dir!= 2 && h<pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]){ //down
            findOldSegment(pop, h+1, w,oldSegment,oldSegmentArray, index,1);
        }
        if(dir!=1 && h>0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h-1][w]){ // up
            findOldSegment(pop, h-1, w,oldSegment,oldSegmentArray, index,2);
        }
        if(dir !=4 && h<pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]){ //right
            findOldSegment(pop, h, w+1,oldSegment,oldSegmentArray, index,3);
        }
        if(dir != 3 && w>0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w-1]){ // left
            findOldSegment(pop, h, w-1,oldSegment,oldSegmentArray, index,4);
        }
    }
}
