package softbdtesseractopencv;


import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import softbdtesseractopencv.softbdopencv.OpenCvTesting;
import softbdtesseractopencv.softbdtesseract.TxtDetect;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Application Started..");
        
        testTesseract();
    }
    
    private static void testTesseract() {
    
        URL url = App.class.getResource("/input/tabular_data1.png");
        System.out.println(url.getPath());
        Mat sourceImg = imread(url.getPath());
        File tabularImg = new File(url.getPath());
        
//      File bangTxtImg = new File("resources/input/bangla-txt-img.jpg");
//      File nIDImg = new File("resources/input/national-id-card1.jpg");
        
//        try {
            HashMap<Integer, Rect> listOfRect = OpenCvTesting.getRectangles(sourceImg);
            
            TxtDetect txtDetect = new TxtDetect();
//            txtDetect.setPageSegMode(3);
            
//            String[] tabularData = new String[30];
//            int i = 0;
//            for (Rect rect : listOfRect) {
//                String extractedTxt = txtDetect.doOCR(tabularImg, new Rectangle(rect.x, rect.y, rect.width, rect.height));
//                if (extractedTxt != null) {
////                    tabularData[i++] = extractedTxt;
//                    System.out.println("[  " + extractedTxt + "  ]");
//                    System.out.println("-----------------------------");
//                }
//
//            }

//            System.out.println("All Extracted Txt: " + extractedTxt);
//            String nationalId = SoftBDUtils.getNationalID(extractedTxt);
//            System.out.println("Extracted National ID: " + nationalId);
//        } catch (TesseractException e) {
//            System.err.println(e.getMessage());
//        }
    }
}
