package main;

import java.util.Random;

public class Mutator {

    static Pop mutateR(Pop pop, Bean bean){
        if(pop.segmentToPixel.length==bean.size){
            return mutateA(pop,bean);
        }
        Pop pop2 = new Pop(pop);
        pop = null;
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
        //System.out.println("MutateR vertical: "+vertical+"    h: "+h+"     w: "+w);

        //if(pop2.pixelToSegment[h][w] >= pop2.segmentToPixel.length){
        //    System.out.println();
        //}
        Index[] newSegment = new Index[ pop2.segmentToPixel[pop2.pixelToSegment[h][w]].length ];
        IntHolder indexHolder = new IntHolder();
        int oldSegment = pop2.pixelToSegment[h][w];

        propagateNewSegment(pop2, h, w,oldSegment,(short)pop2.segmentToPixel.length, newSegment, indexHolder);
        int index = indexHolder.i;
        Index[] newSegment2 = new Index[index];
        System.arraycopy(newSegment,0,newSegment2,0,index);

        //startSegTime = System.currentTimeMillis();
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

        /*for (int i = 0; i < pop2.segmentToPixel.length; i++) {
            for (int j = 0; j < pop2.segmentToPixel[i].length; j++) {
                Index index2 = pop2.segmentToPixel[i][j];
                if(index2==null){
                    System.out.println();
                }
                if(pop2.pixelToSegment[index2.i][index2.j]!=i){
                    System.out.println();
                }
            }
        }*/


        //pop2.printConnections();
        //System.out.println();

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
        pop = null;
        Random r = new Random(System.nanoTime());
        boolean vertical = r.nextBoolean();
        boolean[][] edges;
        int hd = 0;
        int wd = 0;
        if(vertical){ //horizontal less width on array, more width on next pixel
            edges = pop2.verticalEdges;
            hd = 1;
        }else{
            edges = pop2.horizontalEdges;
            wd = 1;
        }

        int h = r.nextInt(bean.height-hd);
        int w = r.nextInt(bean.width-wd);
        int h2 = h+hd;
        int w2 = w+wd;
        int maxTries = 100;
        int tries = 0;
        while(edges[h][w] || pop2.pixelToSegment[h][w]==pop2.pixelToSegment[h2][w2]){
            if (tries++ == maxTries) return pop2;
            h = r.nextInt(bean.height-hd);
            w = r.nextInt(bean.width-wd);
            h2 = h+hd;
            w2 = w+wd;
        }
        //long startTime = System.currentTimeMillis();
        //long startSegTime;

        edges[h][w] = true;
        int dir;
        if(vertical) dir = 1;
        else dir = 3;

        //System.out.println("MutateA vertical: "+vertical+"    h: "+h+"     w: "+w);
        short oldSegment = pop2.pixelToSegment[h2][w2];
        short newSegment = pop2.pixelToSegment[h][w];
        //System.out.println("newSegment<oldSegment "+(newSegment<oldSegment));

        if(newSegment<oldSegment){
            propagateSegment(pop2, h2, w2,oldSegment,newSegment,dir);
        }else {
            short a = oldSegment;
            oldSegment = newSegment;
            newSegment = a;
            propagateSegment(pop2, h, w,oldSegment,newSegment,dir+1);
        }
        //System.out.println("newSegment:"+newSegment+"  oldSegment:"+oldSegment);

        //startSegTime = System.currentTimeMillis();

        Index[][] segmentToPixel = new Index[pop2.segmentToPixel.length-1][];
        for (int i = 0; i < pop2.segmentToPixel.length-1; i++) {
            if(i == oldSegment){
                //move last segment here and update pixelToSegment
                segmentToPixel[i] = pop2.segmentToPixel[pop2.segmentToPixel.length-1];
                for (int k = 0; k < segmentToPixel[i].length; k++) {
                    pop2.pixelToSegment[segmentToPixel[i][k].i][segmentToPixel[i][k].j]=(short)i;
                }
            }else if(i == newSegment){
                //concatenate segments
                int l1 = pop2.segmentToPixel[oldSegment].length;
                int l2 = pop2.segmentToPixel[newSegment].length;
                segmentToPixel[i] = new Index[l1+l2];
                for (int j = 0; j < l1; j++) {
                    segmentToPixel[i][j] = pop2.segmentToPixel[oldSegment][j];
                }
                for (int j = l1; j < l2+l1; j++) {
                    segmentToPixel[i][j] = pop2.segmentToPixel[newSegment][j-l1];
                }
            }else{
                segmentToPixel[i] = pop2.segmentToPixel[i];
            }
        }
        pop2.segmentToPixel = segmentToPixel;

        /*for (int i = 0; i < pop2.segmentToPixel.length; i++) {
            for (int j = 0; j < pop2.segmentToPixel[i].length; j++) {
                Index index = pop2.segmentToPixel[i][j];
                if(index==null){
                    System.out.println();
                }
                if(pop2.pixelToSegment[index.i][index.j]!=i){
                    System.out.println();
                }
            }
        }*/

        //pop2.printConnections();
        //System.out.println();

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

    static void propagateSegment(Pop pop, int h, int w, int oldSegment, short newSegment, int dir){
        if(pop.pixelToSegment[h][w] == oldSegment){
            pop.pixelToSegment[h][w]=newSegment;
        } else{
            return; //already marked
        }
        if(dir!= 2 && h<pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]){ //down
            propagateSegment(pop, h+1, w,oldSegment,newSegment,1);
        }
        if(dir!=1 && h>0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h-1][w]){ // up
            propagateSegment(pop, h-1, w,oldSegment,newSegment,2);
        }
        if(dir !=4 && h<pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]){ //right
            propagateSegment(pop, h, w+1,oldSegment,newSegment,3);
        }
        if(dir != 3 && w>0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w-1]){ // left
            propagateSegment(pop, h, w-1,oldSegment,newSegment,4);
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
