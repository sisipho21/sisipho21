import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MedianFilterSerial {
    public static void main(String[] args) {
        BufferedImage img = null;
        File f = null;

        String inImage = args[0];
        String path = "../images/"+inImage;
        
        
        try {
 
            f = new File(path);
            img = ImageIO.read(f);
            
        } catch (IOException e) {
            System.out.println("File not found!");
            System.exit(0);
        }
        System.out.println("File successfully found!"); 
    }
}
