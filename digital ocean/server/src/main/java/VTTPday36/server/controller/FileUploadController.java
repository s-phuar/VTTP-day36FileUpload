package VTTPday36.server.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import VTTPday36.server.models.Post;
import VTTPday36.server.service.FileUploadService;
import VTTPday36.server.service.S3Service;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Controller
public class FileUploadController {

    // hardcoded image type, see angular form as well

    private static final String BASE64_PREFIX = "data:asd/whocares;base64,";

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AmazonS3 s3Client;

    @Value("${do.storage.bucket}")
    private String bucketName;


    @PostMapping(path="/api/uploadbucket", 
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadS3(
        @RequestPart("file") MultipartFile file,
        @RequestPart("comments") String comments) {

        System.out.println("accessing bucket upload...");

        String postId = "";
        String s3EndpointUrl = "";
        try{
            postId = this.fileUploadService.upload(file, comments); //uploads to sql
            System.out.println("Post ID: " + postId);
            if(postId !=null && !postId.isEmpty())
                s3EndpointUrl = this.s3Service.upload(file, comments, postId); //uploads to bucket
            System.out.println(s3EndpointUrl);
        }catch(SQLException | IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        JsonObject payload=  Json.createObjectBuilder()
            .add("postId", postId)
            .build();
        return ResponseEntity.ok(payload.toString());
    }
    
    @GetMapping(path="/api/get-imagebucket/{postId}")
    public ResponseEntity<String> getImageBucket(@PathVariable(name="postId") String postId){

        //get list of all objects in the s3 bucket
        ObjectListing objectListing = s3Client.listObjects(bucketName);

        //get object matching picture{postId}
        String correctKey = null;
        for (S3ObjectSummary objSummary : objectListing.getObjectSummaries()) {
            if (objSummary.getKey().startsWith("picture" + postId)) {
                correctKey = objSummary.getKey();
                System.out.println("picture" + postId);
                System.out.println(correctKey);
                break;
            }
        }

        //if I want metdata for some ungodly reason
            // ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, correctKey);
           // String fileExt = metadata.getUserMetadata().getOrDefault("fileExtension", "unknown");
    
        GetObjectRequest getReq =  new GetObjectRequest(bucketName, correctKey); //correctKey includes file extension
        S3Object obj = s3Client.getObject(getReq);

        try (S3ObjectInputStream inputStream = obj.getObjectContent()) {
            byte[] fileBytes = inputStream.readAllBytes(); // Convert to byte array
            String encodingString = Base64.getEncoder().encodeToString(fileBytes);
            
            
        JsonObject jObj = Json.createObjectBuilder()
            .add("image", BASE64_PREFIX + encodingString) //attached base64 prefix (mime type) so the browser knows whats it looking at
            .build();
            
        return ResponseEntity.ok(jObj.toString());

        }catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    
    }
        
        

    @PostMapping(path="/api/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> upload(
        @RequestPart("file") MultipartFile file,
        @RequestPart("comments") String comments){
        
            String postId = "";
            try{
                postId = this.fileUploadService.upload(file, comments);
                System.out.println(postId);
            }catch(SQLException | IOException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }

            JsonObject obj = Json.createObjectBuilder()
                .add("postId", postId)
                .build();
            
            return ResponseEntity.ok(obj.toString());
    }

    @GetMapping(path = "/api/get-image/{postId}")
    public ResponseEntity<String> getImage(@PathVariable(name="postId") String postId){
        Optional<Post> optionalP = this.fileUploadService.getPostById(postId);
        Post p = optionalP.get();

        //encoding byte array into base 64 string
        String encodingString = Base64.getEncoder().encodeToString(p.getImage());

        JsonObject obj = Json.createObjectBuilder()
            .add("image", BASE64_PREFIX + encodingString) //attached base64 prefix (mime type) so the browser knows whats it looking at
            .build();

        return ResponseEntity.ok(obj.toString());
    }


    //chuk's alternative
    //https://github.com/chukmunnlee/vttp2023_batch4/blob/main/day38/upload/src/main/java/vttp/batch4/csf/day38/upload/controllers/PictureController.java

    // @GetMapping(path = "/api/get-image/{postId}")
    // @ResponseBody
    // public ResponseEntity<byte[]> getPicture(@PathVariable String postId) {

    //     Optional<Post> opt = fileUploadService.getPostById(postId);
    //     Post p = opt.get();
    //     String contentType = "image/png";

    //     return ResponseEntity.status(200)
    //             .header("Content-Type", contentType)
    //             .header("Cache-Control", "max-age=604800")
    //             .body(p.getImage());
    // }
    



}
