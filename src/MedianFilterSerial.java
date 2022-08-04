import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MedianFilterSerial {
    public static void main(String[] args) {
        BufferedImage img = null, copy=null;
        File f = null, f2=null;
        int w,h;
        //int w, h, p, alpha, red, green, blue;
        String inImage = args[0];  //input image
        String path1 = "../images/"+inImage;
        String outImage = args[1]; //output image
        String path2 = "../images/"+outImage;
        
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

        /*p = img.getRGB(0,0);
        alpha = (p>>24) & 0*ff; //bits 24 to 31
        red = (p>>16) & 0*ff; //bits 16 to 23
        green = (p>>8) & 0*ff; //bits 8 to 15
        blue = p & 0*ff; //0 to 7 bits*/

        

        /*int[][] pArray = new int[h][w];
        int[][] aArray = new int[h][w];
        int[][] rArray = new int[h][w];
        int[][] gArray = new int[h][w];
        int[][] bArray = new int[h][w];*/
        
       /*  for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int alpha, red, green, blue;
                
                pArray[i][j] = img.getRGB(i,j);
                mirror.setRGB(i,j, img.getRGB(i, j));
                /*alpha = (p>>24) & 0xff; //bits 24 to 31
                red = (p>>16) & 0xff; //bits 16 to 23
                green = (p>>8) & 0xff; //bits 8 to 15
                blue = p & 0xff; //0 to 7 bits
                pArray[i][j] = p;
                aArray[i][j] = alpha;
                rArray[i][j] = red;
                gArray[i][j] = green;
                bArray[i][j] = blue;

                
            }
            
        }*/
        

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
