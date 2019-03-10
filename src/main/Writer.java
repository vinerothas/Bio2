package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Writer {

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
}
