package com.sau.hdfs.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class HdfsConfig {

    @Value("${hdfs.namenode.uri:hdfs://localhost:9000}")
    private String namenodeUri;

    @Bean
    public FileSystem fileSystem() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", namenodeUri);
        return FileSystem.get(configuration);
    }
}
