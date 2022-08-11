import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;

public class MedianFilterParallel {
    //global variables for both
    static BufferedImage img = null, copy=null;
    static int width, window;
    //START MAIN
    public static void main(String[] args){
        File f = null, f2=null;
        int height;

        String inImage = args[0];  //input image
        String path1 = "../images/"+inImage;
        String outImage = args[1]; //output image
        String path2 = "../images/"+outImage;
        window = Integer.valueOf(args[2]); //window width
        
        //Ensuring that window index>=3 and odd
        if (window < 3) {
            System.out.println("Please provide index >=3");
            System.exit(0);
        } else if (window %2 == 0) {
            System.out.println("Please provide an odd index");
            System.exit(0);
        }

        
        //Reading in image from images folder
        try {
 
            f = new File(path1);
            img = ImageIO.read(f);
            copy = ImageIO.read(f);
            
        } catch (IOException e) {
            System.out.println("File not found!");
            System.exit(0);
        }
        
        width = img.getWidth();
        height = img.getHeight();

        MedianFilterParallel medianF = new MedianFilterParallel();
        RunMedianFilterParallel mfp = medianF.new RunMedianFilterParallel( 0, height);
        ForkJoinPool fjPool = new ForkJoinPool();

        long startTime = System.currentTimeMillis(); //start time for benchmarking
        fjPool.invoke(mfp);
        long endTime = System.currentTimeMillis(); //end time for benchmarking

        try {
            f2 = new File(path2) ;
            ImageIO.write(copy,"jpg", f2);
         } catch (IOException e) {
             System.out.println("Could not write to output image");
             System.exit(0);
         }

         System.out.println("Filter window index: "+window+". Image dimensions: "+width+"x"+height+". Time taken: "+ (endTime-startTime)*0.001 +" seconds.");

    }//end MAIN

    public class RunMedianFilterParallel extends RecursiveAction{
        private int SEQUENTIAL_CUTOFF = 500;
        private int starting_H;
        private int ending_H;
        private int windHalf = window/2;

        public RunMedianFilterParallel( int start_height, int end_height) {
            starting_H = start_height;
            ending_H = end_height;
        }

        protected void computeMedianFilter(){  
            int[] arrAlpha = new int[window*window];
            int[] arrRed = new int[window*window];
            int[] arrGreen = new int[window*window];
            int[] arrBlue = new int[window*window];
            int middle = (window*window)/2;
            for (int i = starting_H; i < ending_H-window; i++) {
                for (int j = 0; j < width-window; j++) {
                    int hStart = i; int hEnd = hStart +window-1; int hMiddle = (hStart+hEnd)/2;
                    int wStart = j; int wEnd = wStart +window-1; int wMiddle = (wStart+wEnd)/2;
                    int counter = 0; //helps with creating an array
                    //Going through each window
                    for (int y = 0; y < window; y++) {
                        
                        for (int x = 0; x < window; x++) {
                            int p = img.getRGB(wStart, hStart);
                            
                            arrAlpha[counter] = (p>>24) & 0xff;
                            arrRed[counter] = (p>>16) & 0xff;
                            arrGreen[counter] = (p>>8) & 0xff;
                            arrBlue[counter] = p & 0xff;
                            counter++;
                            
                            wStart++;
                        }
                        wStart = j;
                        hStart++;
                    }
                    hStart = i;
                    Arrays.sort(arrAlpha);
                    int aMid = arrAlpha[middle];
                    Arrays.sort(arrRed);
                    int rMid = arrRed[middle];
                    Arrays.sort(arrGreen);
                    int gMid = arrGreen[middle];
                    Arrays.sort(arrBlue);
                    int bMid = arrBlue[middle];
                    int filtered_pixel =  (aMid<<24) | (rMid<<16) | (gMid<<8) | bMid;
                    copy.setRGB(wMiddle, hMiddle, filtered_pixel);
                }
            }
        }// **End of Mean Filter

        protected void compute() {
            if (ending_H-starting_H < SEQUENTIAL_CUTOFF) {
                computeMedianFilter();
                return;
            }
            int middle = (starting_H+ending_H)/2;
            invokeAll(new RunMedianFilterParallel(starting_H, middle+windHalf ), new RunMedianFilterParallel( middle-windHalf, ending_H) );
        }

    }//END INNER CLASS
}//END OUTER CLASS
