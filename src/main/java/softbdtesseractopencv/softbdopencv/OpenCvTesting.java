package softbdtesseractopencv.softbdopencv;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.highgui.HighGui.*;
import static org.opencv.highgui.ImageWindow.WINDOW_AUTOSIZE;
import static org.opencv.imgcodecs.Imgcodecs.*;
import static org.opencv.imgproc.Imgproc.*;

public class OpenCvTesting {
    static {
        nu.pattern.OpenCV.loadShared();
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
}
