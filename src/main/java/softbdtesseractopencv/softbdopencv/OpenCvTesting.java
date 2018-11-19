package softbdtesseractopencv.softbdopencv;

import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import softbdtesseractopencv.softbdtesseract.TxtDetect;

import java.awt.*;
import java.io.File;

import java.io.PrintWriter;
import java.util.*;
import java.util.List;

import static org.opencv.core.Core.addWeighted;
//import static org.opencv.core.Core.meanStdDev;
import static org.opencv.highgui.HighGui.*;
import static org.opencv.highgui.ImageWindow.WINDOW_AUTOSIZE;
import static org.opencv.imgcodecs.Imgcodecs.*;
import static org.opencv.imgproc.Imgproc.*;

public class OpenCvTesting {
    static {
        nu.pattern.OpenCV.loadShared();
    }
    
    public static void main(String[] args) {
        OpenCvTesting openCvTesting = new OpenCvTesting();

//        openCvTesting.detectRectangle();
        openCvTesting.testTesseract();
    }
    
    private void testTesseract() {
        
        Mat sourceImg = imread("resources/input/tabular_data1.png");
        File tabularImg = new File("resources/input/tabular_data1.png");

//      File bangTxtImg = new File("resources/input/bangla-txt-img.jpg");
//      File nIDImg = new File("resources/input/national-id-card1.jpg");
        
        try {
            long starttime = System.currentTimeMillis();
            HashMap<Integer, Rect> listOfRect = getRectangles(sourceImg);
            long endtime = System.currentTimeMillis();
            System.out.println("get Rectangle time: " + (endtime - starttime) / 1000F);
            TxtDetect txtDetect = new TxtDetect();
//            txtDetect.setPageSegMode(3);

//            String[] tabularData = new String[30];
//            int i = 0;
            ArrayList<Integer> txtSequence = new ArrayList<>(listOfRect.keySet());
            Collections.sort(txtSequence, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2 - o1;
                }
            });
            starttime = System.currentTimeMillis();
            String strHtml = "<html><head><title>ocr result</title></head><body><table ><tr>";
            int i = 0;
            for (int seq : txtSequence) {
                
                Rect rect = listOfRect.get(seq);
                if (rect.height > 200) {
                    continue;
                }
                String extractedTxt = txtDetect.doOCR(tabularImg, new Rectangle(rect.x, rect.y, rect.width, rect.height));
                if (extractedTxt != null && !extractedTxt.trim().equals("")) {
//                    tabularData[i++] = extractedTxt;
                    System.out.println("[(" + (seq - i) + "/" + seq + " -  " + rect.width + "/" + rect.height + ")  " + extractedTxt + "  ]");
                    System.out.println("-----------------------------");
                    if ((i - seq) > 20) {
                        strHtml += "</tr><tr>";
                    }
                    strHtml = strHtml + "<td style='border: 1px solid #e1e1e1; padding: 5px; text-align: center;'>" + extractedTxt + "</td>";
                }
                i = seq;
            }
            strHtml += "</tr></table></body></html>";
            endtime = System.currentTimeMillis();
            System.out.println("ocr time: " + (endtime - starttime) / 1000F);
            
            PrintWriter htmlFileOut = new PrintWriter("resources/output/htmlfile.html");
            htmlFileOut.println(strHtml);
            htmlFileOut.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static HashMap<Integer, Rect> getRectangles(Mat sourceImg) {
        
        Mat destinationImg = sourceImg.clone();
        Mat edges = new Mat();
        Imgproc.cvtColor(sourceImg, destinationImg, COLOR_BGR2GRAY);
        int threshold = 100;
        equalizeHist(destinationImg, destinationImg);
        Canny(destinationImg, edges, threshold, threshold * 3);
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        
        HashMap<Integer, Rect> listOfRect = new HashMap<>();
        
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        
        for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
            MatOfPoint contour = contours.get(idx);
            Rect rect = Imgproc.boundingRect(contour);
            
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea <= 500F) {
                continue;
            }
            
            matOfPoint2f.fromList(contour.toList());
            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
            long total = approxCurve.total();
            
            if (total == 4) {
                List<Double> cos = new ArrayList<>();
                Point[] points = approxCurve.toArray();
                for (int j = 2; j < total + 1; j++) {
                    cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
                }
                Collections.sort(cos);
                Double minCos = cos.get(0);
                Double maxCos = cos.get(cos.size() - 1);
                boolean isRect = minCos >= -0.1 && maxCos <= 0.3;
//                boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
                if (isRect) {
                    listOfRect.put(idx, rect);
                }
            }
        }
        return listOfRect;
    }
    
    public void concatImages() {
        String[] args = new String[]{
                "resources/faces/face1.jpg",
                "resources/faces/face2.jpg",
                "resources/faces/face3.jpg",
                "resources/faces/face4.jpg",
        };
        
        ArrayList<Mat> images = new ArrayList<>();
        
        for (int i = 0; i < args.length; i++) {
            System.out.println("image path: " + args[i]);
            Mat img = imread(args[i]);
            if (!img.empty()) {
                images.add(img);
            }
        }
        System.out.println("processed images: " + images.size());
        if (images.size() > 0) {
            Mat dest = new Mat();
            Core.hconcat(images, dest);
            imwrite("resources/faces/concated_faces.jpg", dest);
//            org.bytedeco.javacpp.opencv_imgcodecs.imwrite("concated_faces.jpg", dest);
        }
    }
    
    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }
    
    private static void drawText(Mat colorImage, Point ofs, String text) {
        Imgproc.putText(colorImage, text, ofs, Core.FONT_HERSHEY_SIMPLEX, 0.25, new Scalar(0, 0, 255));
    }
    
    public void blendImg(String image1, String image2) {
        double alpha = 0.4;
        double beta;
        
        Mat src1, src2, dst = new Mat();
        System.out.println(" Simple Linear Blender ");
        
        src1 = imread(image1);
        src2 = imread(image2);
        if (src1.empty()) {
            System.out.println("Error loading src1");
            return;
        }
        if (src2.empty()) {
            System.out.println("Error loading src2");
            return;
        }
        Mat resizeimage = new Mat();
        resize(src2, resizeimage, src1.size(), 0, 0, INTER_AREA);
        beta = (1.0 - alpha);
        addWeighted(src1, alpha, resizeimage, beta, 0.0, dst);
        imshow("Linear Blend", dst);
        waitKey(0);
        System.exit(0);
    }
    
    public void loadSaveImg(String imgName) {
        Mat image = imread(imgName, IMREAD_COLOR);
        
        Mat gray_image = new Mat();
        cvtColor(image, gray_image, COLOR_BGR2GRAY);
        
        imwrite("resources/output/Gray_Image.jpg", gray_image);
        namedWindow(imgName, WINDOW_AUTOSIZE);
        namedWindow("Gray image", WINDOW_AUTOSIZE);
        imshow(imgName, image);
        imshow("Gray image", gray_image);
        waitKey(0);
    }
    
    public void detectFace(String imgToDetectFace, String ext) {
        System.out.println("\nRunning DetectFaceDemo");
        
        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier("resources/lbpcascade_frontalface.xml");
        Mat image = imread(imgToDetectFace);
        
        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        
        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        
        // Save the visualized detection.
        String filename = "resources/output/faceDetection." + ext;
        System.out.println(String.format("Writing %s", filename));
        imwrite(filename, image);
    }
    
    public void detectRectangle() {
        Mat sourceImg = imread("resources/input/tabular_data1.png");
//        Mat dst = tabularImg.clone();
//        imshow("Original", sourceImg);
        Mat destinationImg = sourceImg.clone();
        Mat edges = new Mat();
        Imgproc.cvtColor(sourceImg, destinationImg, COLOR_BGR2GRAY);
        int threshold = 100;
        equalizeHist(destinationImg, destinationImg);
        Canny(destinationImg, edges, threshold, threshold * 3);
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        ArrayList<Rect> listOfRect = new ArrayList<>();
        
        for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
            MatOfPoint contour = contours.get(idx);
            Rect rect = Imgproc.boundingRect(contour);
            
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea <= 500F) {
                continue;
            }
            listOfRect.add(rect);
//            System.out.println(rect.x + "-" + rect.y + "-" + rect.width + "-" + rect.height);
//            System.out.println(contourArea);
            matOfPoint2f.fromList(contour.toList());
            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
            long total = approxCurve.total();
            if (total == 3) { // is triangle
                // do things for triangle
            }
            if (total >= 4 && total <= 6) {
                List<Double> cos = new ArrayList<>();
                Point[] points = approxCurve.toArray();
                for (int j = 2; j < total + 1; j++) {
                    cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
                }
                Collections.sort(cos);
                Double minCos = cos.get(0);
                Double maxCos = cos.get(cos.size() - 1);
                boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
                boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
                if (isRect) {
                    double ratio = Math.abs(1 - (double) rect.width / rect.height);
                    drawText(sourceImg, rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
                }
                if (isPolygon) {
                    drawText(sourceImg, rect.tl(), "Polygon");
                }
            }
        }
        
        imshow("Detect Rect", sourceImg);
        
        waitKey(0);
        System.exit(1);
    }
}
