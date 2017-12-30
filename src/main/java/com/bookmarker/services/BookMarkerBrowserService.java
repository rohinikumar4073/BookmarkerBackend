package com.bookmarker.services;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.modal.BrowserHistory;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
@Service
public class BookMarkerBrowserService {

    @Autowired
    private BrowserHistoryRepository browserHistoryRepository;

    public void saveUploadedFiles(BrowserDataList browserDataList) throws IOException {
        Type listType = new TypeToken<List<BrowserHistory>>(){}.getType();
        List<BrowserHistory> browserHistory = new Gson().fromJson(browserDataList.getBrowserLogs(),
                listType);
        browserHistoryRepository.insert(browserHistory);
    }

    private void checkIfbrowserIdExists(String browserId) {
        browserHistoryRepository.exists(browserId);
    }
}
