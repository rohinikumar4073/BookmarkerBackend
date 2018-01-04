package com.bookmarker;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.modal.BrowserHistory;
import com.bookmarker.modal.BrowserHistoryJson;
import com.bookmarker.modal.BrowserHistoryJsonHeader;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.bookmarker.services.BookMarkerBrowserService;
import com.bookmarker.util.TestUtils;
import com.google.gson.Gson;
import com.mongodb.WriteResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

import java.util.*;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class BookMarkerBrowserServiceTest {
    @Mock
    private BrowserHistoryRepository browserHistoryRepository;
    @InjectMocks
    BookMarkerBrowserService bookMarkerBrowserService=new BookMarkerBrowserService();
    @Test (expected = RuntimeException.class)
    public void BookMarkerBrowserServiceTest() throws Exception {
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
    }
    @Test
    public void json_file_conversion_header() throws Exception {
        BrowserHistoryJsonHeader browserHistoryJsonHeader=bookMarkerBrowserService.getBrowserHeader();
        assertEquals(browserHistoryJsonHeader.getAttributes().size(),6);
        assertEquals(browserHistoryJsonHeader.getAttributes().get(0).getName(),"dayOfTheWeek");
        assertEquals(browserHistoryJsonHeader.getAttributes().get(5).getName(),"hostname");
    }
    @Test
    public void json_file_conversion_data_hostmap() throws Exception {
        List<BrowserHistory> browserHistoryList = TestUtils.getSampleBrowserHistory();
        bookMarkerBrowserService.setLimitHostNameCount(0);
        Map<String, Integer>  hostMap=bookMarkerBrowserService.populateHostMap(browserHistoryList);
        int count=hostMap.get("www.facebook.com");
        assertEquals(count,1);
        assertEquals(bookMarkerBrowserService.populateBrowserHistoryData( browserHistoryList, hostMap).size(),1);
    }

}

