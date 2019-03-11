package main;

import java.util.Random;

public class Mutator {

    static Pop mutateV(Pop pop, Bean bean){
        Pop pop2 = new Pop(pop);
        Random r = new Random(System.currentTimeMillis());
        int h = r.nextInt(bean.height-1);
        int w = r.nextInt(bean.width);
        while(!pop2.verticalEdges[h][w]){
            h = r.nextInt(bean.height-1);
            w = r.nextInt(bean.width);
        }

        boolean remove = pop2.pixelToSegment[h][w]==pop2.pixelToSegment[h+1][w];
        //TODO uncomment this and remove from inside if: pop2.verticalEdges[h][w] = !remove;
        if(remove){
            //split a segment
            pop2.verticalEdges[h][w] = !remove;
            Index[] newSegment = new Index[pop2.segmentToPixel[pop2.pixelToSegment[h][w]].length];
            IntHolder indexHolder = new IntHolder();
            int oldSegment = pop2.pixelToSegment[h][w];
            propagateSegment(pop2, h, w,oldSegment,(short)pop2.segmentToPixel.length, newSegment, indexHolder);
            int index = indexHolder.i;
            Index[] newSegment2 = new Index[index];
            System.arraycopy(newSegment,0,newSegment2,0,index);
            Index[][] segmentToPixel = new Index[pop2.segmentToPixel.length+1][];
            for (int i = 0; i < pop2.segmentToPixel.length; i++) {
                if(i != oldSegment){
                    segmentToPixel[i] = pop2.segmentToPixel[i];
                }else{
                    segmentToPixel[i] = new Index[pop2.segmentToPixel[i].length-newSegment2.length];
                    int k = 0;
                    for (int j = 0; j < pop2.segmentToPixel[i].length; j++) {
                        boolean found = false;
                        for (int l = 0; l < newSegment2.length; l++) {
                            if(pop2.segmentToPixel[i][j].equals(newSegment2[l])){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            if(k==segmentToPixel[i].length){
                                System.out.println();
                            }
                            segmentToPixel[i][k++] = pop2.segmentToPixel[i][j];
                        }
                    }
                }
            }
            segmentToPixel[pop2.segmentToPixel.length] = newSegment2;
            pop2.segmentToPixel = segmentToPixel;
        }else{
            // merge two segments
        }
        return pop2;
    }

    static void propagateSegment(Pop pop, int h, int w, int oldSegment, short newSegment, Index[] newSegmentArray, IntHolder index){
        if(pop.pixelToSegment[h][w] == oldSegment){
            pop.pixelToSegment[h][w]=newSegment;
            newSegmentArray[index.i++] = new Index((short)h,(short)w);
        } else{
            return; //already marked
        }
        if(h<pop.verticalEdges.length && w < pop.verticalEdges[0].length && pop.verticalEdges[h][w]){
            propagateSegment(pop, h+1, w,oldSegment,newSegment,newSegmentArray, index);
        }
        if(h>0 && w < pop.verticalEdges[0].length && pop.verticalEdges[h-1][w]){
            propagateSegment(pop, h-1, w,oldSegment,newSegment,newSegmentArray, index);
        }
        if(h<pop.horizontalEdges.length && w < pop.horizontalEdges[0].length && pop.horizontalEdges[h][w]){
            propagateSegment(pop, h, w+1,oldSegment,newSegment,newSegmentArray, index);
        }
        if(w>0 && h < pop.horizontalEdges.length && pop.horizontalEdges[h][w-1]){
            propagateSegment(pop, h, w-1,oldSegment,newSegment,newSegmentArray, index);
        }
    }
}
