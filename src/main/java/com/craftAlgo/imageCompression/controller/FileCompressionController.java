package com.craftAlgo.imageCompression.controller;

import com.craftAlgo.imageCompression.controller.service.ImageCompressingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/image")
public class FileCompressionController {

    @Value("${image.upload.directory}")
    private String uploadDir;

    private final Path storageLocation = Paths.get("/Users/DELL/Downloads/imageCompression/imageCompression/result").toAbsolutePath().normalize();
    private ImageCompressingService imageCompressingService;

    @Autowired
    public FileCompressionController(ImageCompressingService imageCompressingService){
        this.imageCompressingService = imageCompressingService;
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file) throws IOException
    {
        // Check if the file is passed in the Request...?
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("Please ensure you have shared the file to compress");
        }

        // Check the upload directory is available or not...?
        File directory = new File(uploadDir);
        if(!directory.exists()){
            directory.mkdirs();
        }

        // Generate complete file path to upload the file over server
        Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
        Files.write(path, file.getBytes());

        //Return the file name to call next API
        return ResponseEntity.ok().body(file.getOriginalFilename());
    }

    @GetMapping("/process/{fileName}")
    public ResponseEntity<String> processImage(@PathVariable String fileName) throws IOException {
        // Split the file name to get the extension of the image
        String[] fileFormat = fileName.split("\\.");

        //Call the appropriate function based on the image type
        String Res;
        if(fileFormat[fileFormat.length -1].toLowerCase().equals("png")){
            Res = imageCompressingService.processPNG(fileName);
            System.out.println(Res);
        } else if (fileFormat[fileFormat.length -1].toLowerCase().equals("jpg") || fileFormat[fileFormat.length -1].toLowerCase().equals("jpeg")) {
            Res = imageCompressingService.processJPEG(fileName);
            System.out.println(Res);
        }else{
            return ResponseEntity.badRequest().body("Please share the file in PNG/JPEG file format");
        }
        return ResponseEntity.accepted().body(Res);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> imageDownload (@PathVariable String fileName)
    {
        // Using try as the process can end-up in multiple exceptions
        try{
            // Creating the file path for provided filename
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            //Can check the file path URI created for the file name passed
            //System.out.println(filePath.toUri());

            //Checking the file is present and readable or not...?
            if(resource.exists() && resource.isReadable()){
                System.out.println("3");
                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }else{
                System.out.println("4");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }catch(Exception e){
            System.out.println("5");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
