package com.bookmarker;
import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.services.BookMarkerBrowserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BookMarkerBrowserServiceTest {
    @Autowired
    BookMarkerBrowserService bookMarkerBrowserService;
    @Test
    public void findWhetherUserIdExists() throws Exception {
        BrowserDataList browserDataList= new BrowserDataList("","")
        bookMarkerBrowserService.saveUploadedFiles(browserDataList);
        assert

    }
}

