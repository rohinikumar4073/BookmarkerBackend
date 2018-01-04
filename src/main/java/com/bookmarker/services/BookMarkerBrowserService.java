package com.bookmarker.services;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.modal.BrowserHistory;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookMarkerBrowserService {

    @Autowired
    private BrowserHistoryRepository browserHistoryRepository;

    public void saveUploadedFiles(BrowserDataList browserDataList) throws IOException {
        Type listType = new TypeToken<List<BrowserHistory>>() {
        }.getType();
        List<BrowserHistory> browserHistory = new Gson().fromJson(browserDataList.getBrowserLogs(),
                listType);
        if (browserHistory.size() > 0) {
            browserHistory.forEach(item -> item.setBrowserId(browserDataList.browserId));
            browserHistoryRepository.insert(browserHistory);


        } else {
            throw new RuntimeException("No browser logs present");
        }

    }

    public File convertToJSONFile(List<BrowserHistory> browserHistoryList) {
        List<String> uniqueDomains = new ArrayList();
        Map<String, String> hostNameMap = new HashMap<String, String>();
        browserHistoryList.forEach(browserHistory -> {
            browserHistory.

                }
        );

        return null;
    }
}
