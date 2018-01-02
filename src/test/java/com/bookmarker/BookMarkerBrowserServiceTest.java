package com.bookmarker;
import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.modal.BrowserHistory;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.bookmarker.services.BookMarkerBrowserService;
import com.bookmarker.util.TestUtils;
import com.google.gson.Gson;
import com.mongodb.WriteResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class BookMarkerBrowserServiceTest {
    @MockBean
    private BrowserHistoryRepository browserHistoryRepository;
    @Autowired
    BookMarkerBrowserService bookMarkerBrowserService;
    @Test (expected = RuntimeException.class)
    public void thow_exception_if_no_dat() throws Exception {
        doReturn(new WriteResult(1, true, null)).when(this.browserHistoryRepository)
                .insert(Mockito.any(BrowserHistory.class));
        BrowserDataList browserDataList= new BrowserDataList("","");
        bookMarkerBrowserService.saveUploadedFiles(browserDataList);

    }
    @Test
    public void call_save_if_data_present() throws Exception {
        doReturn(new WriteResult(1, true, null)).when(this.browserHistoryRepository)
                .insert(Mockito.any(BrowserHistory.class));
        String browserHistoryString = TestUtils.createBrowserHistoryString();
        String jsonString = new Gson().toJson(new BrowserDataList(browserHistoryString, "1234"));
        BrowserDataList browserDataList= new BrowserDataList(browserHistoryString,"");
        bookMarkerBrowserService.saveUploadedFiles(browserDataList);
        verify(browserHistoryRepository, times(1)).insert(Mockito.any(BrowserHistory.class));

    }
}

