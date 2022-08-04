import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MedianFilterParallel {
    public static void main(String[] args) {
        BufferedImage img = null, copy=null;
        File f = null, f2=null;
        int w,h;

        String inImage = args[0];  //input image
        String path1 = "../images/"+inImage;
        String outImage = args[1]; //output image
        String path2 = "../images/"+outImage;
        
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

        //Outputing the image file into another image
        try {
            f2 = new File(path2) ;
            ImageIO.write(copy,"jpg", f2);
         } catch (Exception e) {
             System.out.println("Could not write to output image");
             System.exit(0);
         }
        
    }
}
