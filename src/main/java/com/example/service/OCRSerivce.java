package com.example.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRSerivce {
    static {
        OpenCV.loadLocally();
    }
    public String extractText(File file) throws Exception {

        File processed = preprocessImage(file);

        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

        return tesseract.doOCR(processed);
    }
    public File preprocessImage(File file) throws Exception {

        Mat img = Imgcodecs.imread(file.getAbsolutePath());

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        Mat threshold = new Mat();
        Imgproc.threshold(gray, threshold, 150, 255, Imgproc.THRESH_BINARY);

        File processed = new File("processed.png");
        Imgcodecs.imwrite(processed.getAbsolutePath(), threshold);

        return processed;
    }
}
