package com.bookmarker;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.services.BookMarkerBrowserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jscience.physics.amount.Amount;
import org.jscience.physics.model.RelativisticModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.measure.quantity.Mass;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import static javax.measure.unit.SI.KILOGRAM;

@Controller
@SpringBootApplication
public class BookMarkerApplication {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private BookMarkerBrowserService bookMarkerBrowserService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BookMarkerApplication.class, args);
    }


    @CrossOrigin(origins = "chrome-extension://mfgpcckppbddjggfkiddleckmbokeikb")
    @RequestMapping(value = "/browserData", method = RequestMethod.POST)
        public ResponseEntity<?> loadBrowserData(@RequestBody BrowserDataList browserDataList) {
        try {
            bookMarkerBrowserService.saveUploadedFiles(browserDataList);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("Error in saving data",
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("Successfully uploaded data ",
                new HttpHeaders(), HttpStatus.OK);
    }


}
