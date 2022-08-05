import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MeanFilterSerial {
    
    
    
     public static void main(String[] args) {
        MeanFilterSerial mSerial = new MeanFilterSerial();
        BufferedImage img = null, copy=null;
        File f = null, f2=null;
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
        

        //writing to a file using pixels
        for (int i=0; i<h; i++)
        {
            for (int j = 0; j < w; j++) {

                int p = img.getRGB(j, i); //pixel from original image
                int alpha = (p>>24) & 0xff; //bits 24 to 31
                int red = (p>>16) & 0xff; //bits 16 to 23
                int green = (p>>8) & 0xff; //bits 8 to 15
                int blue = p & 0xff; //0 to 7 bits
                
                int p2 = (alpha<<24) | (red<<16) | (green<<8) | blue; //pixel for new image

                copy.setRGB(j, i, p2); //create the value at the pixel coordinate

            }
        }

        //Outputting the original image file into copy another image
        mSerial.OutputImage(copy, path2);
         
        
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
