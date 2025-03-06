package VTTPday36.server.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;




@Service
public class S3Service {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${do.storage.bucket}")
    private String bucketName;

    @Value("${do.storage.endpoint}")
    private String finalpoint;

    public String upload (MultipartFile file, String comments, String postId) throws IOException{
        Map<String, String> userData = new HashMap<String, String>();
        //file metadata to be assocaited with objectmetadata
        userData.put("comments", comments);
        userData.put("postId", postId);
        userData.put("fileName", file.getOriginalFilename()); //has the extension
        userData.put("uploadDateTime", LocalDateTime.now().toString());

        //set media type of the object
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setUserMetadata(userData);



        StringTokenizer tk = new StringTokenizer(file.getOriginalFilename(), ".");

        String fileNameExt = "";
        while(tk.hasMoreTokens()) {
            fileNameExt = tk.nextToken();  // This will always get the last token (the extension)
        }
        System.out.println(fileNameExt);
        if(fileNameExt.equals("blob"))
            fileNameExt = fileNameExt + ".png";


        PutObjectRequest putReq = new PutObjectRequest(bucketName,"picture%s.%s".formatted(postId, fileNameExt), 
                    file.getInputStream(), metadata);
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);

        s3Client.putObject(putReq);
        System.out.println("Uploading to s3...");
        return "https://%s.%s/picture%s.%s".formatted(bucketName, finalpoint, postId, fileNameExt);
    }





    
}
