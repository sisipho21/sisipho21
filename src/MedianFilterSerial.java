import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MedianFilterSerial {
    public static void main(String[] args) {
        MedianFilterSerial medSerial = new MedianFilterSerial();
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
        
        //Reading in the image file & storing it in img
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
            medSerial.OutputImage(copy, path2);
            System.exit(0);
        } else if (h<3) {
            System.out.println("Image height is too small to be filtered.");
            medSerial.OutputImage(copy, path2);
            System.exit(0);
        }

        
        int[] arrAlpha = new int[window*window];
        int[] arrRed = new int[window*window];
        int[] arrGreen = new int[window*window];
        int[] arrBlue = new int[window*window];
        int middle = (window*window)/2;
        //Going through the whole image
        for (int i = 0; i < h-window; i++) {
            for (int j = 0; j < w-window; j++) {
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
                
                int fltPixel = (aMid<<24) | (rMid<<16) | (gMid<<8) | bMid;
                copy.setRGB(wMiddle, hMiddle, fltPixel);

            }
        }

        //Outputing the image file into another image
        medSerial.OutputImage(copy, path2);

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
