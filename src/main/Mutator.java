package main;

import java.util.Random;

public class Mutator {

    static Pop mutateR(Pop pop, Bean bean){
        if(pop.segmentToPixel.length==bean.size){
            return mutateA(pop,bean);
        }
        Pop pop2 = new Pop(pop);
        Random r = new Random(System.nanoTime());
        boolean vertical = r.nextBoolean();
        int h = r.nextInt(bean.height-1);
        int w = r.nextInt(bean.width-1);
        while(vertical&&!pop2.verticalEdges[h][w]){
            h = r.nextInt(bean.height-1);
            w = r.nextInt(bean.width);
        }
        while(!vertical&&!pop2.horizontalEdges[h][w]){
            h = r.nextInt(bean.height);
            w = r.nextInt(bean.width-1);
        }

        //long startTime = System.currentTimeMillis();
        //long startSegTime;

        int h2 = h;
        int w2 = w;
        int dir;

        if(vertical){
            pop2.verticalEdges[h][w] = false;
            h2++;
            dir = 1;
        }
        else{
            pop2.horizontalEdges[h][w] = false;
            w2++;
            dir = 3;
        }
        System.out.println("MutateR vertical: "+vertical+"    h: "+h+"     w: "+w);

        Index[] newSegment = new Index[pop2.segmentToPixel[pop2.pixelToSegment[h][w]].length];
        IntHolder indexHolder = new IntHolder();
        int oldSegment = pop2.pixelToSegment[h][w];

        propagateNewSegment(pop2, h, w,oldSegment,(short)pop2.segmentToPixel.length, newSegment, indexHolder);
        int index = indexHolder.i;
        Index[] newSegment2 = new Index[index];
        System.arraycopy(newSegment,0,newSegment2,0,index);

        //startSegTime = System.currentTimeMillis();
        Index[] oldSegment2 = new Index[pop2.segmentToPixel[oldSegment].length-newSegment2.length];
        findOldSegment(pop,h2,w2,oldSegment,oldSegment2,new IntHolder(),dir);

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
        //startTime = System.currentTimeMillis()-startTime;
        //startSegTime = System.currentTimeMillis()-startSegTime;
        //System.out.println("Start: "+startTime+"   Seg: "+startSegTime);

        return pop2;
    }

    static Pop mutateA(Pop pop, Bean bean){
        if(pop.segmentToPixel.length==1){
            return mutateR(pop,bean);
        }
        Pop pop2 = new Pop(pop);
        Random r = new Random(System.nanoTime());
        boolean vertical = r.nextBoolean();
        int h = r.nextInt(bean.height-1);
        int w = r.nextInt(bean.width-1);
        while(vertical&&pop2.verticalEdges[h][w]){
            h = r.nextInt(bean.height-1);
            w = r.nextInt(bean.width);
        }
        while(!vertical&&pop2.horizontalEdges[h][w]){
            h = r.nextInt(bean.height);
            w = r.nextInt(bean.width-1);
        }

        //long startTime = System.currentTimeMillis();
        //long startSegTime;

        int h2 = h;
        int w2 = w;
        int dir;

        if(vertical){
            pop2.verticalEdges[h][w] = true;
            h2++;
            dir = 1;
        }
        else{
            pop2.horizontalEdges[h][w] = true;
            w2++;
            dir = 3;
        }
        System.out.println("MutateA vertical: "+vertical+"    h: "+h+"     w: "+w);

        short oldSegment = pop2.pixelToSegment[h2][w2];
        short newSegment = pop2.pixelToSegment[h][w];

        propagateSegment(pop2, h, w,oldSegment,newSegment);

        //startSegTime = System.currentTimeMillis();

        Index[][] segmentToPixel = new Index[pop2.segmentToPixel.length-1][];
        int j = 0;
        for (int i = 0; i < pop2.segmentToPixel.length; i++) {
            //has to look at pixelToSegment, because pixelToSegment and segmentToPixel might diverge
            int seg = pop2.pixelToSegment[pop2.segmentToPixel[i][0].i][pop2.segmentToPixel[i][0].j];
            if(seg != oldSegment && seg != newSegment){
                segmentToPixel[j++] = pop2.segmentToPixel[i];
            }else{
                //TODO segmentToPixel[j++] = oldSegment2;
            }
        }
        //TODO segmentToPixel[pop2.segmentToPixel.length] = newSegment2;
        pop2.segmentToPixel = segmentToPixel;
        //startTime = System.currentTimeMillis()-startTime;
        //startSegTime = System.currentTimeMillis()-startSegTime;
        //System.out.println("Start: "+startTime+"   Seg: "+startSegTime);

        return pop2;
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

    static void propagateSegment(Pop pop, int h, int w, int oldSegment, short newSegment){
        if(pop.pixelToSegment[h][w] == oldSegment){
            pop.pixelToSegment[h][w]=newSegment;
        } else{
            return; //already marked
        }
        if(h<pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]){
            propagateSegment(pop, h+1, w,oldSegment,newSegment);
        }
        if(h>0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h-1][w]){
            propagateSegment(pop, h-1, w,oldSegment,newSegment);
        }
        if(h<pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]){
            propagateSegment(pop, h, w+1,oldSegment,newSegment);
        }
        if(w>0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w-1]){
            propagateSegment(pop, h, w-1,oldSegment,newSegment);
        }
    }

    static void findOldSegment(Pop pop, int h, int w, int oldSegment,Index[] oldSegmentArray, IntHolder index, int dir){
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
