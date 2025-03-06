package VTTPday36.server.repository;

import java.io.IOException;
// import java.sql.SQLException;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

// import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import VTTPday36.server.models.Post;

@Repository
public class FileUploadRepository {
    @Autowired
    private JdbcTemplate template;

    // @Autowired
    // private DataSource dataSource;

    public static final String INSERT_POST = "INSERT INTO posts (post_id, comments, picture) values (?, ?, ?)";
    public static final String SELECT_POST_BY_ID = "select post_id, comments, picture from posts where post_id=?";
    // public static final String SELECT_POST_BY_ID = "select * from post where post_id=?";

 
    //uploading image bytes using datasource
    // public String upload(MultipartFile file, String comments) throws SQLException, IOException{
    //     try(Connection con = dataSource.getConnection();
    //         PreparedStatement ps = con.prepareStatement(INSERT_POST)){
    //             String postId = UUID.randomUUID().toString().substring(0,8);
    //             ps.setString(1, postId);
    //             ps.setString(2, comments);
    //             ps.setBytes(3, file.getBytes());
    //             ps.executeUpdate();
    //             return postId;
    //         }
    // }

    //uploading to s3 bucket




    //uploading image bytes to SQL using jdbctemplate
    public String upload(MultipartFile file, String comments){
        String postId = UUID.randomUUID().toString().substring(0,8);
        try{
            byte[] fileBytes = file.getBytes();

            //using prepared statement, more explicit than usual way of inserting
                // template.update(INSERT_POST, ps ->{
                //     ps.setString(1, postId);
                //     ps.setString(2, comments);
                //     ps.setBytes(3, fileBytes);
                // });

            //usual way of doing this
            template.update(INSERT_POST, postId, comments, fileBytes);
        }catch(IOException e){
            throw new RuntimeException("failed to upload file content", e);
        }
        return postId;
    }




    public Optional<Post> getPostById(String postId){
       return template.query(
        //1. query
        SELECT_POST_BY_ID,
        //2. rowmapping
        (ResultSet rs) -> {
            if(rs.next()){
                return Optional.of(Post.populate(rs));
            }else{
                return Optional.empty();
        }}
        //3. matching query placeholder
        , postId); 
    }




}
