package com.bookmarker.services;

import com.bookmarker.modal.*;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class BookMarkerBrowserService {

    @Autowired
    private BrowserHistoryRepository browserHistoryRepository;
    private Integer limitHostNameCount = 40;

    public BrowserHistoryRepository getBrowserHistoryRepository() {
        return browserHistoryRepository;
    }

    public void setBrowserHistoryRepository(BrowserHistoryRepository browserHistoryRepository) {
        this.browserHistoryRepository = browserHistoryRepository;
    }

    public Integer getLimitHostNameCount() {
        return limitHostNameCount;
    }

    public void setLimitHostNameCount(Integer limitHostNameCount) {
        this.limitHostNameCount = limitHostNameCount;
    }

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
        getBrowserHistoryJson(browserHistoryList);
        return null;
    }

    public BrowserHistoryJson getBrowserHistoryJson(List<BrowserHistory> browserHistoryList) {
        BrowserHistoryJson browserHistoryJson = new BrowserHistoryJson();
        browserHistoryJson.setHeader(getBrowserHeader());
        Map<String, Integer> hostNameMap = populateHostMap(browserHistoryList);
        browserHistoryJson.setData(populateBrowserHistoryData(browserHistoryList, hostNameMap));
        return browserHistoryJson;
    }

    public List<BrowserHistoryJsonData> populateBrowserHistoryData(List<BrowserHistory> browserHistoryList, Map<String, Integer> hostNameMap) {
        List<BrowserHistoryJsonData> browserHistoryJsonDataList = new ArrayList<BrowserHistoryJsonData>();
        browserHistoryList.forEach(browserHistory -> {
                    try {
                        String hostname = new URI(browserHistory.getUrl()).getHost();
                        if (hostNameMap.get(hostname) > this.limitHostNameCount) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(new Double(browserHistory.getLastVisitTime()).longValue());
                            BrowserHistoryJsonData browserHistoryJsonData = new BrowserHistoryJsonData(false, 10, new String[]{
                                    Integer.toString(calendar.get(Calendar.DAY_OF_WEEK)),
                                    Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
                                    Integer.toString(calendar.get(Calendar.MONTH)), Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH)), hostname});
                                    browserHistoryJsonDataList.add(browserHistoryJsonData);
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

        );
        return browserHistoryJsonDataList;
    }

    public Map<String, Integer> populateHostMap(List<BrowserHistory> browserHistoryList) {
        Map<String, Integer> hostNameMap = new HashMap<String, Integer>();
        browserHistoryList.forEach(browserHistory -> {
                    try {
                        String hostname = new URI(browserHistory.getUrl()).getHost();
                        if (hostNameMap.containsKey(hostname)) {
                            hostNameMap.put(hostname, hostNameMap.get(hostname) + 1);
                        } else {
                            hostNameMap.put(hostname, 1);
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
        );
        return hostNameMap;
    }

    public BrowserHistoryJsonHeader getBrowserHeader() {
        BrowserHistoryJsonHeader browserHistoryJsonHeader = new BrowserHistoryJsonHeader();
        browserHistoryJsonHeader.setRelation("BrowserHistory");
        browserHistoryJsonHeader.setAttributes(getBrowserHeaderAttributes());
        return browserHistoryJsonHeader;
    }

    private List<BrowserHistoryJsonHeaderAttribute> getBrowserHeaderAttributes() {
        List<BrowserHistoryJsonHeaderAttribute> browserHistoryJsonHeaderAttributeList = new ArrayList<BrowserHistoryJsonHeaderAttribute>();
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("dayOfTheWeek", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("time", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("dayOfTheMonth", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("Month", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("weekOfTheMonth", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("hostname", "numeric", false, 1.0, null));
        return browserHistoryJsonHeaderAttributeList;
    }
}
