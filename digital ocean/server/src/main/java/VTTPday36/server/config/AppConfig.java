package VTTPday36.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AppConfig {


    @Value("${do.storage.key}")
    private String accessKey;

    @Value("${do.storage.secret}")
    private String secret;

    @Value("${do.storage.endpoint}")
    private String endpoint;

    @Value("${do.storage.endpoint.region}")
    private String region;


    @Bean
    public AmazonS3 createS3Client(){
        BasicAWSCredentials crd = new BasicAWSCredentials(accessKey, secret);
        EndpointConfiguration finalpoint = new EndpointConfiguration(endpoint, region);
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(finalpoint)
            .withCredentials(new AWSStaticCredentialsProvider(crd))
            .build();
    }


    
}
