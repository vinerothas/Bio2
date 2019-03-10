package main;

public class Util {

    static float rgbDist(int i1, int j1, int i2, int j2,Bean bean){
        short r1= bean.pixelR[i1][j1];
        short g1=bean.pixelG[i1][j1];
        short b1=bean.pixelB[i1][j1];
        short r2=bean.pixelR[i2][j2];
        short g2=bean.pixelG[i2][j2];
        short b2=bean.pixelB[i2][j2];
        return (float)Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

    static float rgbDist(int i1, int j1, float r2, float g2,float b2,Bean bean){
        short r1= bean.pixelR[i1][j1];
        short g1=bean.pixelG[i1][j1];
        short b1=bean.pixelB[i1][j1];
        return (float)Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

    static float segmentDeviation(Index[] segment, Bean bean){
        float r = 0;
        float g = 0;
        float b = 0;
        for (Index index: segment) {
            r+= bean.pixelR[index.i][index.j];
            g+= bean.pixelG[index.i][index.j];
            b+= bean.pixelB[index.i][index.j];
        }
        r /= (float)segment.length;
        g /= (float)segment.length;
        b /= (float)segment.length;

        float dev = 0;
        for (Index index: segment) {
            dev += rgbDist(index.i,index.j,r,g,b,bean);
        }
        return dev;
    }

    static int calculateConc(Bean bean, short[][] pixelToSegment){
        int conc = 0;

        int i = 0;
        int j = 0;
        // top left
        if(pixelToSegment[i][j]!=pixelToSegment[i][j+1])conc++; //right
        if(pixelToSegment[i][j]!=pixelToSegment[i+1][j])conc++; //down
        if(pixelToSegment[i][j]!=pixelToSegment[i+1][j+1])conc++; // down right
        // top
        for (j = 1; j < bean.width-1; j++) {
            if(pixelToSegment[i][j]!=pixelToSegment[i][j+1])conc++; //right
            if(pixelToSegment[i][j]!=pixelToSegment[i][j-1])conc++; //left
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j])conc++; //down
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j+1])conc++; //down right
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j-1])conc++; //down left
        }
        // top right
        if(pixelToSegment[i][j]!=pixelToSegment[i][j-1])conc++; //left
        if(pixelToSegment[i][j]!=pixelToSegment[i+1][j])conc++; //down
        if(pixelToSegment[i][j]!=pixelToSegment[i+1][j-1])conc++; //down left

        for (i = 1; i < bean.height-1; i++) {
            j = 0;
            // left
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j])conc++; //down
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j])conc++; //up
            if(pixelToSegment[i][j]!=pixelToSegment[i][j+1])conc++; //right
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j+1])conc++; //down right
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j+1])conc++; //up right
            for (j = 1; j < bean.width-1; j++) {
                // middle
                if(pixelToSegment[i][j]!=pixelToSegment[i][j+1])conc++; //right
                if(pixelToSegment[i][j]!=pixelToSegment[i][j-1])conc++; //left
                if(pixelToSegment[i][j]!=pixelToSegment[i+1][j])conc++; //down
                if(pixelToSegment[i][j]!=pixelToSegment[i-1][j])conc++; //up
                if(pixelToSegment[i][j]!=pixelToSegment[i+1][j+1])conc++; //down right
                if(pixelToSegment[i][j]!=pixelToSegment[i+1][j-1])conc++; //down left
                if(pixelToSegment[i][j]!=pixelToSegment[i-1][j+1])conc++; //up right
                if(pixelToSegment[i][j]!=pixelToSegment[i-1][j-1])conc++; //up left
            }
            // right
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j])conc++; //down
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j])conc++; //up
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j-1])conc++; //up left
            if(pixelToSegment[i][j]!=pixelToSegment[i+1][j-1])conc++; //down left
            if(pixelToSegment[i][j]!=pixelToSegment[i][j-1])conc++; //left
        }

        j = 0;
        // bottom left
        if(pixelToSegment[i][j]!=pixelToSegment[i-1][j])conc++; //up
        if(pixelToSegment[i][j]!=pixelToSegment[i-1][j+1])conc++; //up right
        if(pixelToSegment[i][j]!=pixelToSegment[i][j+1])conc++; //right
        for (j = 1; j < bean.width-1; j++) {
            // bottom
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j])conc++; //up
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j+1])conc++; //up right
            if(pixelToSegment[i][j]!=pixelToSegment[i][j+1])conc++; //right
            if(pixelToSegment[i][j]!=pixelToSegment[i][j-1])conc++; //left
            if(pixelToSegment[i][j]!=pixelToSegment[i-1][j-1])conc++; //up left
        }
        // bottom right
        if(pixelToSegment[i][j]!=pixelToSegment[i][j-1])conc++; //left
        if(pixelToSegment[i][j]!=pixelToSegment[i-1][j-1])conc++; //up left
        if(pixelToSegment[i][j]!=pixelToSegment[i-1][j])conc++; //up

        return conc++;
    }
}
