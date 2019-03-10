package main;

import java.util.Random;

public class Mutator {

    static void mutate(Pop pop, Bean bean){
        Pop pop2 = new Pop(pop);
        Random r = new Random(System.currentTimeMillis());
        int h = r.nextInt(bean.height);
        int w = r.nextInt(bean.width);
        while(pop2.genotype[h][w] == Pop.dir.self){
            h = r.nextInt(bean.height);
            w = r.nextInt(bean.width);
        }
        int s = pop2.pixelToSegment[h][w];

        boolean m = r.nextBoolean();

        int h2 = h;
        int w2 = w;
        if(pop2.genotype[h][w] == Pop.dir.left){
            w2 = w-1;
        }else if(pop2.genotype[h][w] == Pop.dir.right){
            w2 = w+1;
        }else if(pop2.genotype[h][w] == Pop.dir.up){
            h2 = h-1;
        }else if(pop2.genotype[h][w] == Pop.dir.down){
            h2 = h+1;
        }
        int s2 = pop2.pixelToSegment[h2][w2];

        pop2.genotype[h][w] = Pop.dir.self;

        /*if(pop2.genotype[h][w] == Pop.dir.left){
            int h2 = h;
            int w2 = w-1;
            int s2 = pop2.pixelToSegment[h2][w2];
            int h3;
            int w3 = w;
            if(m){
                pop2.genotype[h][w] = Pop.dir.up;
                h3 = h-1;
            }else{
                pop2.genotype[h][w] = Pop.dir.down;
                h3 = h+1;
            }
            int s3 = pop2.pixelToSegment[h2][w2];
            if()
        }else if(pop2.genotype[h][w] == Pop.dir.right){
            int h2 = h;
            int w2 = w+1;
        }*/
    }
}
