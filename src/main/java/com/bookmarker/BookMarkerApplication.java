package com.bookmarker;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.services.BookMarkerBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@Controller
@SpringBootApplication
public class BookMarkerApplication {


    @Autowired
    private BookMarkerBrowserService bookMarkerBrowserService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BookMarkerApplication.class, args);
    }

    @CrossOrigin(origins = "chrome-extension://mfgpcckppbddjggfkiddleckmbokeikb")
    @RequestMapping(value = "/browserData", method = RequestMethod.GET)
    public ResponseEntity<?> getRecommendations(@RequestParam Long date,@RequestParam String browserId) {
        Set<String> predictionList =null;

        try {
            predictionList=  bookMarkerBrowserService.getBrowserData(date,browserId);
        }  catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(predictionList,
                new HttpHeaders(), HttpStatus.OK);
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
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("Successfully uploaded data ",
                new HttpHeaders(), HttpStatus.OK);
    }


}
