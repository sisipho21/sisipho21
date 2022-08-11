import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

 public class MeanFilterParallel{  //Outer class
    
    public static void main(String[] args) {  //main method for outer class
        BufferedImage img = null, copy=null;
        File f = null, f2=null;
        int width, height;

        String inImage = args[0];  //input image
        String path1 = "../images/"+inImage;
        String outImage = args[1]; //output image
        String path2 = "../images/"+outImage;
        int window_w = Integer.valueOf(args[2]); //window width
        
        //Ensuring that window index>=3 and odd
        if (window_w < 3) {
            System.out.println("Please provide index >=3");
            System.exit(0);
        } else if (window_w %2 == 0) {
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

        //Ensuring that small pictures are not filtered
        /*if (width<3) {
            System.out.println("Image width is too small to be filtered.");
            try {
                f2 = new File(path2) ;
                ImageIO.write(copy,"jpg", f2);
             } catch (IOException e) {
                 System.out.println("Could not write to output image");
                 System.exit(0);
             }
            System.exit(0);
        } else if (height<3) {
            System.out.println("Image height is too small to be filtered.");
            try {
                f2 = new File(path2) ;
                ImageIO.write(copy,"jpg", f2);
             } catch (IOException e) {
                 System.out.println("Could not write to output image");
                 System.exit(0);
             }
            System.exit(0);
        }*/

        MeanFilterParallel meanF = new MeanFilterParallel();
        RunMeanFilterParallel mfp = meanF.new RunMeanFilterParallel(img, copy, window_w, 0, height-1, width);
        ForkJoinPool fjPool = new ForkJoinPool();

        fjPool.invoke(mfp);

        try {
            f2 = new File(path2) ;
            ImageIO.write(copy,"jpg", f2);
         } catch (IOException e) {
             System.out.println("Could not write to output image");
             System.exit(0);
         }

         System.out.println("Parallel Mean Filtering complete!");

    }

    //INNER CLASS START
    public class RunMeanFilterParallel extends RecursiveAction{
    private int SEQUENTIAL_CUTOFF = 150;
    private int window = 3;
    private int starting_H;
    private int ending_H;
    private int w;
    BufferedImage srcImg;
    BufferedImage dstImg;
    int theHeight;

    public RunMeanFilterParallel(BufferedImage sourceImage, BufferedImage destImage, int window_width, int start_height, int end_height, int width) {
        window = window_width;
        starting_H = start_height;
        ending_H = end_height;
        w = width;
        srcImg = sourceImage;
        dstImg = destImage;
        theHeight = ending_H - starting_H;
    }
    
    //executes mean filter in serial  **Start of Filtering
    protected void computeMeanFilter(){  
        //long startTime = System.currentTimeMillis(); //start time for benchmarking
        int width_squared = window*window;
        for (int i = starting_H; i < ending_H-window; i++) {
            for (int j = 0; j < w-window; j++) {
                int hStart = i; int hEnd = hStart +window-1; int hMiddle = (hStart+hEnd)/2;
                int wStart = j; int wEnd = wStart +window-1; int wMiddle = (wStart+wEnd)/2;
                int rSum=0, gSum=0, bSum=0;
                //Going through each window
                for (int y = 0; y < window; y++) {
                    
                    for (int x = 0; x < window; x++) {
                        int p = srcImg.getRGB(wStart, hStart);
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
                dstImg.setRGB(wMiddle, hMiddle, filtered_pixel);
            }
        }
    }// **End of Mean Filter

    protected void compute() {
        if (theHeight < SEQUENTIAL_CUTOFF) {
            computeMeanFilter();
            return;
        }
        int middle = ending_H/2;
        invokeAll(new RunMeanFilterParallel(srcImg, dstImg, window, starting_H, middle, w), new RunMeanFilterParallel(srcImg, dstImg, window, starting_H+middle, middle, w) );
    }

    

}// END INNER CLASS

 }//END OUTER CLASS