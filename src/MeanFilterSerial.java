import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MeanFilterSerial {
    
    
    
     public static void main(String[] args) {
        MeanFilterSerial mSerial = new MeanFilterSerial();
        BufferedImage img = null, copy=null;
        File f = null;
        int w,h;

        String inImage = args[0];  //input image
        String path1 = "../images/"+inImage;
        String outImage = args[1]; //output image
        String path2 = "../images/"+outImage;
        int window = Integer.valueOf(args[2]); //window width
        
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
        
        w = img.getWidth();
        h = img.getHeight();

        //Ensuring that small pictures are not filtered
        if (w<3) {
            System.out.println("Image width is too small to be filtered.");
            mSerial.OutputImage(copy, path2);
            System.exit(0);
        } else if (h<3) {
            System.out.println("Image height is too small to be filtered.");
            mSerial.OutputImage(copy, path2);
            System.exit(0);
        }
        

        //Mean Filtering process
        long startTime = System.currentTimeMillis(); //start time for benchmarking
        int width_squared = window*window;
        for (int i = 0; i < h-window; i++) {
            for (int j = 0; j < w-window; j++) {
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
        //End Mean Filtering Process
        long endTime = System.currentTimeMillis();  //end time for benchmarking

        //Outputting the original image file into copy another image
        mSerial.OutputImage(copy, path2);
        System.out.println("Filter window index: "+window+". Image dimensions: "+w+"x"+h+". Time taken: "+ (endTime-startTime)*0.001 +" seconds.");
        
    }

    /**
     * Creates an image file using the path and image you provide
     * @param image is a BufferedImage object
     * @param thePath is a string value with the path of the folder where image will be created
     * @throws IOException when the image cannot be created
     */
    private void OutputImage(BufferedImage image, String thePath) {
        File f2 = null;
        try {
            f2 = new File(thePath) ;
            ImageIO.write(image,"jpg", f2);
         } catch (IOException e) {
             System.out.println("Could not write to output image");
             System.exit(0);
         }
    }
}
