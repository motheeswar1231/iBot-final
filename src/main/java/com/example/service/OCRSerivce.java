package com.example.service;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRSerivce {
    public String extractText(File file) throws Exception {

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");

        return tesseract.doOCR(file);
    }
}
