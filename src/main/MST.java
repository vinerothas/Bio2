package main;

import javafx.util.Pair;

import java.util.TreeSet;
import java.util.*;


class MST {

    public static void produceRandomMst(Bean bean, boolean[][] verticalEdges, boolean[][] horizontalEdges){
        Random r = new Random(System.nanoTime());
        int indexHeight = r.nextInt(bean.height-1);
        int indexWidth = r.nextInt(bean.width-1);
        boolean[][] inMst = new boolean[bean.height][bean.width];

        TreeSet<TreeBean> ts = new TreeSet<>(new TreeBeanComparator());

        for (int i = 1; i < bean.size; i++) {

            Float dist = bean.rightDist[indexHeight][indexWidth];
            if(!dist.isNaN()) ts.add(new TreeBean(indexHeight,indexWidth,dist,false));
            dist = bean.downDist[indexHeight][indexWidth];
            if(!dist.isNaN()) ts.add(new TreeBean(indexHeight,indexWidth,dist,true));
            dist = bean.leftDist[indexHeight][indexWidth];
            if(!dist.isNaN()) ts.add(new TreeBean(indexHeight,indexWidth-1,dist,false));
            dist = bean.upDist[indexHeight][indexWidth];
            if(!dist.isNaN()) ts.add(new TreeBean(indexHeight-1,indexWidth,dist,true));

            TreeBean popped = ts.pollFirst();
            while(inMst[popped.key1][popped.key2]
                    && ((popped.vertical&&inMst[popped.key1+1][popped.key2])||(!popped.vertical&&inMst[popped.key1][popped.key2+1]))
            ){
                popped = ts.pollFirst();
            }

            indexHeight = popped.key1;
            indexWidth = popped.key2;
            if(popped.vertical){
                verticalEdges[indexHeight][indexWidth] = true;
                inMst[indexHeight][indexWidth] = true;
                inMst[indexHeight+1][indexWidth] = true;
                dist = bean.rightDist[indexHeight+1][indexWidth];
                if(!dist.isNaN()) ts.add(new TreeBean(indexHeight+1,indexWidth,dist,false));
                dist = bean.downDist[indexHeight+1][indexWidth];
                if(!dist.isNaN()) ts.add(new TreeBean(indexHeight+1,indexWidth,dist,true));
            }else{
                horizontalEdges[indexHeight][indexWidth] = true;
                inMst[indexHeight][indexWidth] = true;
                inMst[indexHeight][indexWidth+1] = true;
                dist = bean.rightDist[indexHeight][indexWidth+1];
                if(!dist.isNaN()) ts.add(new TreeBean(indexHeight,indexWidth+1,dist,false));
                dist = bean.downDist[indexHeight][indexWidth+1];
                if(!dist.isNaN()) ts.add(new TreeBean(indexHeight,indexWidth+1,dist,true));
            }
        }
    }


    public static void main(String[] args)    {
        Comparator<Pair<Integer, Integer>> byValue = (Pair<Integer, Integer> o1, Pair<Integer, Integer> o2)->o1.getValue()-o2.getValue();
        TreeSet<Pair<Integer, Integer>> ts = new TreeSet<>(new PairComparator());
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < 50; i++) {
            Pair<Integer, Integer> pair = new Pair<>(i, r.nextInt(10));
            ts.add(pair);
        }

        System.out.println(ts);
    }

    public static class PairComparator implements Comparator<Pair<Integer, Integer>> {
        @Override
        public int compare(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
            if (pair1.getValue() < pair2.getValue()) return -1;
            if (pair1.getValue() > pair2.getValue()) return 1;
            if (pair1.getKey() < pair2.getKey()) return -1;
            if (pair1.getKey() > pair2.getKey()) return 1;
            return 0;
        }
    }

    public static class TreeBeanComparator implements Comparator<TreeBean> {
        @Override
        public int compare(TreeBean treeBean1, TreeBean treeBean2) {
            if (treeBean1.value < treeBean2.value) return -1;
            return 1;
        }
    }

    /*
    static int getAbsoluteIndex(int height, int width, int imageHeight){
        return height*imageHeight+width;
    }

    static Pair<Integer,Integer> get2DIndex(int absoluteIndex, int imageHeight){
        int width = absoluteIndex%imageHeight;
        int height = (absoluteIndex-width)/imageHeight;
        return new Pair<>(height,width);
    }*/
}