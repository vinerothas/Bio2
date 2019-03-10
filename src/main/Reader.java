package main;

import javafx.scene.paint.Color;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Reader {

    public void readImage(int testNumber, Bean bean) {
        String filename;
        if(testNumber>0) {
            filename = "resources/" + testNumber + "/Test_image.jpg";
        }else{
            filename = "resources/1/Test_image2.jpg";
        }

        File file = new File(getClass().getResource(filename).getFile());

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }

        int width = image.getWidth();
        int height = image.getHeight();
        bean.width = width;
        bean.height = height;
        bean.size = width*height;
        WritableRaster raster = image.getRaster();

        bean.pixelR = new short[height][];
        bean.pixelG = new short[height][];
        bean.pixelB = new short[height][];
        for(int j=0; j<height; j++) {
            bean.pixelR[j] = new short[width];
            bean.pixelG[j] = new short[width];
            bean.pixelB[j] = new short[width];
            for(int i=0; i<width; i++) {
                bean.pixelR[j][i] = (short)raster.getSample(i,j,0);
                bean.pixelG[j][i] = (short)raster.getSample(i,j,1);
                bean.pixelB[j][i] = (short)raster.getSample(i,j,2);
            }
        }

        bean.calculateDist();



    }
}
