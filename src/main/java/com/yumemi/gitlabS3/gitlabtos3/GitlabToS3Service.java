package com.yumemi.gitlabS3.gitlabtos3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_nam on 2017/05/24.
 */
@Service
public class GitlabToS3Service {

    @Value("${spring.aws.accessKey}")
    private String accessKey;

    @Value("${spring.aws.secretKey}")
    private String secretKey;

    @Value("${spring.gitlab.username}")
    private String username;

    @Value("${spring.gitlab.password}")
    private String password;

    @Value("${spring.gitlab.uri}")
    private String gitlabURI;

    @Value("${spring.gitlab.repository}")
    private String repository;

    @Value("${spring.gitlab.branchName}")
    private String gitBranchName;

    @Value("${spring.s3.bucketName}")
    private String bucketName;

    protected boolean uploadZipFile() throws Exception {
        try {

            CredentialsProvider credentialsProvider
                    = new UsernamePasswordCredentialsProvider( username, password);
            Git git = Git.cloneRepository()
                    .setURI(gitlabURI)
                    .setDirectory(new File(repository))
                    .setCredentialsProvider(credentialsProvider)
                    .call();

            //Checkout Branch
            if(!StringUtils.isEmpty(gitBranchName)){
                git.branchCreate().setForce(true).setName(gitBranchName).setStartPoint("origin/" + gitBranchName).call();
                git.checkout().setName(gitBranchName).call();
            }


            File directoryToZip = new File("./"+repository+"/");

            List<File> fileList = new ArrayList<>();

            ZipDirectory.getAllFiles(directoryToZip, fileList);
            ZipDirectory.writeZipFile(directoryToZip, fileList);

            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3Client s3client = new AmazonS3Client(credentials);
            String fileName = repository + ".zip";
            s3client.putObject(new PutObjectRequest(bucketName, fileName, new File("./"+repository+".zip")));

            if( new File("./"+repository+".zip").delete() ){
                File index = new File("./"+repository);
                delete(index);
                //TODO - Log
            }else{
                //TODO - Log
            }
        }
        catch(Exception ex) {
            //TODO - Log
            ex.printStackTrace();
            throw ex;
        }
        return true;
    }

    private void delete(File file) throws IOException {
        if(file.isDirectory()){
            if(file.list().length==0){

                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        }else{
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }

}
