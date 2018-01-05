package com.bookmarker.services;

import com.bookmarker.modal.*;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.JSONLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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

    public void saveUploadedFiles(BrowserDataList browserDataList) throws Exception {
        Type listType = new TypeToken<List<BrowserHistory>>() {
        }.getType();
        List<BrowserHistory> browserHistory = new Gson().fromJson(browserDataList.getBrowserLogs(),
                listType);
        if (browserHistory.size() > 0) {
            browserHistory.forEach(item -> item.setBrowserId(browserDataList.browserId));
            File file = convertToJSONFile(browserHistory, browserDataList.browserId);
            JSONLoader jsonLoader = new JSONLoader();
            jsonLoader.setSource(file);
            Instances train = jsonLoader.getDataSet();
            train.setClassIndex(train.numAttributes() - 1);
            J48 nb = new J48();
            Evaluation evaluation = new Evaluation(train);
            evaluation.crossValidateModel(nb, train, 1, new Random(1));
            System.out.println(evaluation.toSummaryString());
            SerializationHelper.write("/Users/Vishnu/fb.model", nb);
            browserHistoryRepository.insert(browserHistory);

        } else {
            throw new RuntimeException("No browser logs present");
        }

    }

    public File convertToJSONFile(List<BrowserHistory> browserHistoryList, String browserId) throws IOException {
        FileWriter fileWriter = null;
        File file = null;
        try {
            file = File.createTempFile(browserId, ".json");
            fileWriter = new FileWriter(file);
            fileWriter.write(new Gson().toJson(getBrowserHistoryJson(browserHistoryList)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileWriter.flush();
            fileWriter.close();
        }
        return file;
    }

    public BrowserHistoryJson getBrowserHistoryJson(List<BrowserHistory> browserHistoryList) {
        BrowserHistoryJson browserHistoryJson = new BrowserHistoryJson();
        Map<String, Integer> hostNameMap = populateHostMap(browserHistoryList);
        browserHistoryJson.setHeader(getBrowserHeader(hostNameMap));

        browserHistoryJson.setData(populateBrowserHistoryData(browserHistoryList, hostNameMap));
        return browserHistoryJson;
    }

    public List<BrowserHistoryJsonData> populateBrowserHistoryData(List<BrowserHistory> browserHistoryList, Map<String, Integer> hostNameMap) {
        List<BrowserHistoryJsonData> browserHistoryJsonDataList = new ArrayList<BrowserHistoryJsonData>();
        browserHistoryList.forEach(browserHistory -> {
                    String hostname = null;
                    try {
                        hostname = new URL(browserHistory.getUrl()).getHost();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    }
                    if (hostname != null && hostNameMap.get(hostname) > this.limitHostNameCount) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(new Double(browserHistory.getLastVisitTime()).longValue());
                        BrowserHistoryJsonData browserHistoryJsonData = new BrowserHistoryJsonData(false, 10,
                                new String[]{
                                Integer.toString(calendar.get(Calendar.DAY_OF_WEEK)),
                                        Integer.toString(calendar.get(Calendar.HOUR)),
                                        Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)),
                                Integer.toString(calendar.get(Calendar.MONTH)),
                                        Integer.toString(calendar.get(Calendar.WEEK_OF_MONTH)), hostname});
                        browserHistoryJsonDataList.add(browserHistoryJsonData);
                    }


                }

        );
        return browserHistoryJsonDataList;
    }

    public Map<String, Integer> populateHostMap(List<BrowserHistory> browserHistoryList) {
        Map<String, Integer> hostNameMap = new HashMap<String, Integer>();
        browserHistoryList.forEach(browserHistory -> {
                    String hostname = null;
                    try {
                        hostname = new URL(browserHistory.getUrl()).getHost();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (hostname != null) {
                        if (hostNameMap.containsKey(hostname)) {
                            hostNameMap.put(hostname, hostNameMap.get(hostname) + 1);
                        } else {
                            hostNameMap.put(hostname, 1);
                        }
                    }


                }
        );
        return hostNameMap;
    }

    public BrowserHistoryJsonHeader getBrowserHeader(Map<String, Integer> hostNameMap) {
        BrowserHistoryJsonHeader browserHistoryJsonHeader = new BrowserHistoryJsonHeader();
        browserHistoryJsonHeader.setRelation("BrowserHistory");
        browserHistoryJsonHeader.setAttributes(getBrowserHeaderAttributes(hostNameMap));
        return browserHistoryJsonHeader;
    }

    private List<BrowserHistoryJsonHeaderAttribute> getBrowserHeaderAttributes(Map<String, Integer> hostNameMap) {
        Set <String> hostNames= hostNameMap.keySet();
        String[] hostArr = new String[hostNames.size()];
        List<BrowserHistoryJsonHeaderAttribute> browserHistoryJsonHeaderAttributeList = new ArrayList<BrowserHistoryJsonHeaderAttribute>();
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("dayOfTheWeek", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("time", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("dayOfTheMonth", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("Month", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("weekOfTheMonth", "numeric", false, 1.0, null));
        browserHistoryJsonHeaderAttributeList.add(
                new BrowserHistoryJsonHeaderAttribute("hostname", "nominal", false, 1.0, hostNames.toArray(hostArr )));
        return browserHistoryJsonHeaderAttributeList;
    }
}
