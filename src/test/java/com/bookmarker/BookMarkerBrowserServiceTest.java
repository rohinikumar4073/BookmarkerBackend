package com.bookmarker;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.modal.BrowserHistory;
import com.bookmarker.modal.BrowserHistoryJsonHeader;
import com.bookmarker.services.BookMarkerBrowserService;
import com.bookmarker.util.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class BookMarkerBrowserServiceTest {

    @InjectMocks
    BookMarkerBrowserService bookMarkerBrowserService=new BookMarkerBrowserService();

    @Test (expected = RuntimeException.class)
    public void BookMarkerBrowserServiceTest() throws Exception {

        BrowserDataList browserDataList= new BrowserDataList("","");
        bookMarkerBrowserService.setLimitHostNameCount(0);
        bookMarkerBrowserService.saveUploadedFiles(browserDataList);
    }

    @Test
    public void json_file_conversion_header() throws Exception {
        Map<String, Integer> hostNameMap = bookMarkerBrowserService.populateHostMap(TestUtils.getSampleBrowserHistoryMulitple());
        BrowserHistoryJsonHeader browserHistoryJsonHeader=bookMarkerBrowserService.getBrowserHeader(hostNameMap);
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

