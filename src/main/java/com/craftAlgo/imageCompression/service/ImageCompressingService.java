package com.craftAlgo.imageCompression.service;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class ImageCompressingService {

    @Value("${image.upload.directory}")
    private String uploadDir;

    @Value("${image.result.directory}")
    private String resultDirectory;
    public String processJPEG(String file) throws IOException
    {
        // Create the input file
        File input = new File( uploadDir + File.separator + file);
        // Check if the output directory is available or not... If not create one...
        File directory = new File(resultDirectory);
        if(!directory.exists()){
            directory.mkdirs();
        }

        // Create the result file
        String[] fileSplit = file.split("\\.");
        String outputName = fileSplit[0]+ "_Compressed." + fileSplit[fileSplit.length-1];
        File output = new File(resultDirectory + File.separator + outputName);
        System.out.println("here "+output.getPath());
        // Compress the file by 0.5x
        Thumbnails.of(input)
                .scale(1)
                .outputQuality(0.5)
                .toFile(output);

        // Return the response to the calling function
        return output.getName();
    }

    public String processPNG(String file) throws IOException
    {
        // Create the input file
        PngImage inputImage = new PngImage(Files.newInputStream(Paths.get(uploadDir + File.separator + file)));

        System.out.println("png " + inputImage.getFileName());
        // Check if the output directory is available or not... If not create one...
        File directory = new File(resultDirectory);
        if(!directory.exists()){
            directory.mkdirs();
        }

        // Create the result file
        String[] fileSplit = file.split("\\.");
        String outputName = fileSplit[0]+ "_Compressed." + fileSplit[fileSplit.length-1];
        OutputStream output = Files.newOutputStream(Paths.get(resultDirectory + File.separator + outputName));
        // Compress the file
        PngOptimizer optimizer = new PngOptimizer();
        PngImage optimized = optimizer.optimize(inputImage);
        optimized.writeDataOutputStream(output);

        // Return the response to the calling function
        return outputName;
    }
}
