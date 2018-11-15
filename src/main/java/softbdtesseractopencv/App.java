package softbdtesseractopencv;

import net.sourceforge.tess4j.TesseractException;
import softbdtesseractopencv.softbdopencv.OpenCvTesting;
import softbdtesseractopencv.softbdtesseract.TxtDetect;

import java.io.File;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Application Started..");

//        testTesseract();
//        testDetectFace();
        testImageConcat();
    }
    
    private static void testDetectFace() {
        OpenCvTesting openCvTesting = new OpenCvTesting();
//        openCvTesting.detectFace("resources/input/face_detection_sample.png", "png");
//        openCvTesting.detectFace("resources/input/national-id-card1.jpg", "jpg");
//        openCvTesting.detectFace("resources/input/national-id-card3.jpg", "jpg");
//        openCvTesting.detectFace("resources/input/national-id-card2.jpg", "jpg");
    
    }
    
    private static void testImageConcat() {
        OpenCvTesting openCvTesting = new OpenCvTesting();
//        openCvTesting.loadSaveImg("resources/input/national-id-card1.jpg");
        openCvTesting.blendImg("resources/input/bangla-txt-img.jpg", "resources/input/national-id-card2.jpg");
    }
    
    private static void testTesseract() {
        File tabularImg = new File("resources/input/tabular_data.png");
        File bangTxtImg = new File("resources/input/bangla-txt-img.jpg");
        File nIDImg = new File("resources/input/national-id-card1.jpg");
        
        try {
            
            TxtDetect txtDetect = new TxtDetect();
            String extractedTxt = txtDetect.doOCR(nIDImg);
//            System.out.println("All Extracted Txt: " + extractedTxt);
            String nationalId = SoftBDUtils.getNationalID(extractedTxt);
            System.out.println("Extracted National ID: " + nationalId);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
