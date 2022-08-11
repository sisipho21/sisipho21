import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

 public class MeanFilterParallel{  //Outer class
    
    //Variables shared between the outer class and the inner class
    static BufferedImage img = null, copy=null;
    static int width, window;
    public static void main(String[] args) {  //main method for outer class
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

        MeanFilterParallel meanF = new MeanFilterParallel();
        RunMeanFilterParallel mfp = meanF.new RunMeanFilterParallel( 0, height);
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


    }

    //INNER CLASS START
    public class RunMeanFilterParallel extends RecursiveAction{
    private int SEQUENTIAL_CUTOFF = 500;
    private int starting_H;
    private int ending_H;
    private int windHalf = window/2;

    public RunMeanFilterParallel( int start_height, int end_height) {
        //window = window_width;
        starting_H = start_height;
        ending_H = end_height;
    }
    
    //executes mean filter in serial  **Start of Filtering
    protected void computeMeanFilter(){  
        //long startTime = System.currentTimeMillis(); //start time for benchmarking
        int width_squared = window*window;
        for (int i = starting_H; i < ending_H-window; i++) {
            for (int j = 0; j < width-window; j++) {
                int hStart = i; int hEnd = hStart +window-1; int hMiddle = (hStart+hEnd)/2;
                int wStart = j; int wEnd = wStart +window-1; int wMiddle = (wStart+wEnd)/2;
                int rSum=0, gSum=0, bSum=0;
                //Going through each window
                for (int y = 0; y < window; y++) {
                    
                    for (int x = 0; x < window; x++) {
                        int p = img.getRGB(wStart, hStart);
                        rSum += (p>>16) & 0xff;
                        gSum += (p>>8) & 0xff;
                        bSum += p & 0xff;
                        wStart++;
                    }
                    wStart = j;
                    hStart++;
                }
                hStart = i;
                int rMean = rSum/width_squared;
                int gMean = gSum/width_squared;
                int bMean = bSum/width_squared;
                int filtered_pixel =  (rMean<<16) | (gMean<<8) | bMean;
                copy.setRGB(wMiddle, hMiddle, filtered_pixel);
            }
        }
    }// **End of Mean Filter

    protected void compute() {
        if (ending_H-starting_H < SEQUENTIAL_CUTOFF) {
            computeMeanFilter();
            return;
        }
        int middle = (starting_H+ending_H)/2;
        invokeAll(new RunMeanFilterParallel(starting_H, middle+windHalf ), new RunMeanFilterParallel( middle-windHalf, ending_H) );
    }

    

}// END INNER CLASS

 }//END OUTER CLASS