package com.bookmarker.util;

import com.bookmarker.modal.BrowserHistory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static String createBrowserHistoryString() {
        BrowserHistory browserHistory = new BrowserHistory();
        browserHistory.setId(17768);
        browserHistory.setLastVisitTime(1508667885561.341);
        browserHistory.setTitle("");
        browserHistory.setTypedCount(0);
        browserHistory.setUrl("https://www.facebook.com/photo.php?fbid=10211587966869663&set=a.1137309067822.21785.1080221148&type=3&theater");
        browserHistory.setVisitCount(1);
        List<BrowserHistory> browserHistoryList = new ArrayList<BrowserHistory>();
        browserHistoryList.add(browserHistory);
        return new Gson().toJson(browserHistoryList).toString();
    }
}
