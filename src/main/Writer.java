package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Writer {

    public static void writeSolution(Pop pop, Bean bean, boolean blackandwhite, double score, boolean best){
        BufferedImage image = new BufferedImage(bean.width, bean.height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();

        StringBuilder s = new StringBuilder("output/");
        if(best) s.append("best_");
        else s.append("pareto_");
        s.append(String.format("%.3f",score));
        File outputFile;
        if(blackandwhite) {
            getBWRaster(bean, pop, raster);
            s.append("_bw_");
        }else{
            getGreenRaster(bean,pop,raster);
            s.append("_green_");
        }
        s.append(pop.dev+"_"+pop.conc+"_"+pop.segmentToPixel.length+".png");
        outputFile = new File(s.toString());

        try {
            ImageIO.write(image, "png", outputFile);
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
    }

    public static void writeEdges(int img, boolean edges[][]){
        BufferedImage image = new BufferedImage(edges[0].length, edges.length, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();

        File outputFile;

        int[] blackPixel = new int[]{0,0,0};
        int[] whitePixel = new int[]{255,255,255};
        for (int i = 0; i < edges.length; i++) {
            raster.setPixel(0,i, blackPixel);
            raster.setPixel(edges[0].length-1,i, blackPixel);
        }
        for (int i = 0; i < edges[0].length; i++) {
            raster.setPixel(i,0, blackPixel);
            raster.setPixel(i,edges.length-1, blackPixel);
        }
        for (int i = 1; i < edges.length-1; i++) {
            for (int j = 1; j < edges[0].length-1; j++) {
                if(!edges[i][j]){
                    raster.setPixel(j,i, whitePixel);
                }else{
                    raster.setPixel(j,i, blackPixel);
                }
            }
        }

        outputFile = new File("output/"+img+".png");

        try {
            ImageIO.write(image, "png", outputFile);
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
    }

    private static void getGreenRaster(Bean bean, Pop pop, WritableRaster raster){
        int[] pixels = new int[]{0,255,0};
        for (int i = 0; i < bean.height; i++) {
            raster.setPixel(0,i, pixels);
            raster.setPixel(bean.width-1,i, pixels);
        }
        for (int i = 0; i < bean.width; i++) {
            raster.setPixel(i,0, pixels);
            raster.setPixel(i,bean.height-1, pixels);
        }
        for (int i = 1; i < bean.height-1; i++) {
            for (int j = 1; j < bean.width-1; j++) {
                if(j+1==bean.width||i+1==bean.height){
                    pixels = new int[]{bean.pixelR[i][j],bean.pixelG[i][j],bean.pixelB[i][j]};
                }else if(pop.pixelToSegment[i][j]==pop.pixelToSegment[i+1][j] &&
                        pop.pixelToSegment[i][j]==pop.pixelToSegment[i][j+1] &&
                        pop.pixelToSegment[i][j]==pop.pixelToSegment[i+1][j+1]){
                    pixels = new int[]{bean.pixelR[i][j],bean.pixelG[i][j],bean.pixelB[i][j]};
                }else{
                    pixels = new int[]{0,255,0};
                }
                raster.setPixel(j,i, pixels);
            }
        }
    }

    private static void getBWRaster(Bean bean, Pop pop, WritableRaster raster){
        int[] blackPixel = new int[]{0,0,0};
        int[] whitePixel = new int[]{255,255,255};
        for (int i = 0; i < bean.height; i++) {
            raster.setPixel(0,i, blackPixel);
            raster.setPixel(bean.width-1,i, blackPixel);
        }
        for (int i = 0; i < bean.width; i++) {
            raster.setPixel(i,0, blackPixel);
            raster.setPixel(i,bean.height-1, blackPixel);
        }
        for (int i = 1; i < bean.height-1; i++) {
            for (int j = 1; j < bean.width-1; j++) {
                if(pop.pixelToSegment[i][j]==pop.pixelToSegment[i+1][j] &&
                        pop.pixelToSegment[i][j]==pop.pixelToSegment[i][j+1] &&
                        pop.pixelToSegment[i][j]==pop.pixelToSegment[i+1][j+1]){
                    raster.setPixel(j,i, whitePixel);
                }else{
                    raster.setPixel(j,i, blackPixel);
                }
            }
        }
    }

 /*
    public void write(Bean bean){
        BufferedImage image = new BufferedImage(bean.width, bean.height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();

        for (int i = 0; i < bean.width; i++) {
            for (int j = 0; j < bean.height; j++) {
                int[] pixels = {bean.pixelR[i][j],bean.pixelG[i][j],bean.pixelB[i][j]};
                raster.setPixel(i,j, pixels);
            }
        }

        File outputFile = new File("output.png");
        try {
            ImageIO.write(image, "png", outputFile);
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
    }
    */
}
