package main;

public class Bean {

    // book indexing [y][x]
    short[][] pixelR;
    short[][] pixelG;
    short[][] pixelB;

    float[][] leftDist;
    float[][] rightDist;
    float[][] upDist;
    float[][] downDist;

    int width;
    int height;
    int size;

    void calculateDist(){
        rightDist = new float[height][width];
        leftDist = new float[height][width];
        downDist = new float[height][width];
        upDist = new float[height][width];

        int i = 0;
        int j = 0;
        // top left
        rightDist[i][j] = rgbDist(i,j,i,j+1);
        leftDist[i][j] = Float.NaN;
        downDist[i][j] = rgbDist(i,j,i+1,j);
        upDist[i][j] = Float.NaN;
        // top
        for (j = 1; j < pixelR[i].length-1; j++) {
            rightDist[i][j] = rgbDist(i,j,i,j+1);
            leftDist[i][j] = rightDist[i][j-1];
            downDist[i][j] = rgbDist(i,j,i+1,j);
            upDist[i][j] = Float.NaN;
        }
        // top right
        rightDist[i][j] = Float.NaN;
        leftDist[i][j] = rightDist[i][j-1];
        downDist[i][j] = rgbDist(i,j,i+1,j);
        upDist[i][j] = Float.NaN;

        for (i = 1; i < pixelR.length-1; i++) {
            j = 0;
            // left
            rightDist[i][j] = rgbDist(i,j,i,j+1);
            leftDist[i][j] = Float.NaN;
            downDist[i][j] = rgbDist(i,j,i+1,j);
            upDist[i][j] = downDist[i-1][j];
            for (j = 1; j < pixelR[i].length-1; j++) {
                // middle
                rightDist[i][j] = rgbDist(i,j,i,j+1);
                leftDist[i][j] = rightDist[i][j-1];
                downDist[i][j] = rgbDist(i,j,i+1,j);
                upDist[i][j] = downDist[i-1][j];
            }
            // right
            rightDist[i][j] = Float.NaN;
            leftDist[i][j] = rightDist[i][j-1];
            downDist[i][j] = rgbDist(i,j,i+1,j);
            upDist[i][j] = downDist[i-1][j];
        }

        j = 0;
        // bottom left
        rightDist[i][j] = rgbDist(i,j,i,j+1);
        leftDist[i][j] = Float.NaN;
        downDist[i][j] = Float.NaN;
        upDist[i][j] = downDist[i-1][j];
        for (j = 1; j < pixelR[i].length-1; j++) {
            // bottom
            rightDist[i][j] = rgbDist(i,j,i,j+1);
            leftDist[i][j] = rightDist[i][j-1];
            downDist[i][j] = Float.NaN;
            upDist[i][j] = downDist[i-1][j];
        }
        // bottom right
        rightDist[i][j] = Float.NaN;
        leftDist[i][j] = rightDist[i][j-1];
        downDist[i][j] = Float.NaN;
        upDist[i][j] = downDist[i-1][j];
    }

    float rgbDist(int i1, int j1, int i2, int j2){
        return rgbDist2(pixelR[i1][j1],pixelG[i1][j1], pixelB[i1][j1],pixelR[i2][j2],pixelG[i2][j2], pixelB[i2][j2]);
    }

    static float rgbDist2(short r1, short g1, short b1, short r2, short g2, short b2) {
        return (float)Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }
}
