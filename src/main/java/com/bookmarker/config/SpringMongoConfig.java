package com.bookmarker.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.db}")
    private String databasename;
    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.port}")
    private Integer port;
    @Value("${spring.data.mongodb.user}")
    private String user;
    @Value("${spring.data.mongodb.password}")
    private String password;

    @Override
    protected String getDatabaseName() {
        return databasename;
    }

    @Override
    public Mongo mongo() throws Exception {
        MongoCredential credentials = MongoCredential.createCredential(this.user, this.databasename, this.password.toCharArray());
        List<MongoCredential> credentials1 = new ArrayList<MongoCredential>();
        credentials1.add(credentials);
        ServerAddress serverAddress = new ServerAddress(host, port);
        return new MongoClient(serverAddress, credentials1);
    }
}