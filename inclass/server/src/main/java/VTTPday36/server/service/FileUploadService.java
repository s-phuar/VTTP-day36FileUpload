package VTTPday36.server.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import VTTPday36.server.models.Post;
import VTTPday36.server.repository.FileUploadRepository;

@Service
public class FileUploadService {
    @Autowired
    private FileUploadRepository fileUploadRepository;


    public String upload(MultipartFile file, String comment) throws SQLException, IOException{
        return fileUploadRepository.upload(file, comment);
    }

    public Optional<Post> getPostById(String postId){
        return fileUploadRepository.getPostById(postId);
    }






}
