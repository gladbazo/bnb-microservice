package com.example.bnbmicroservice.Init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class Initialization {
    @Autowired
    private DataSource dataSource;
//    @Bean
//    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
//        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
//        resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));
//
//        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
//        dataSourceInitializer.setDataSource(dataSource);
//        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
//
//        return dataSourceInitializer;
//    }
    public void insertDataIntoDatabase(String filePath) {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScripts(new ClassPathResource(filePath));
        rdp.execute(dataSource);
    }
}