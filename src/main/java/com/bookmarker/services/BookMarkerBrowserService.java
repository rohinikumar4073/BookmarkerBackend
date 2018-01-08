package com.bookmarker.services;

import com.bookmarker.modal.*;
import com.bookmarker.modal.BrowserClassifications;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.JSONLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class BookMarkerBrowserService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Value("${limitHostNameCount}")
    private Integer limitHostNameCount;
    @Autowired
    private BrowserHistoryRepository browserHistoryRepository;

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
            InputStream browserStream = convertToJSONFile(browserHistory, browserDataList.browserId);
            File tempFile = generateClassifications(browserStream);
            saveClassificationAndBrowserHistory(browserDataList, browserHistory, tempFile);
        } else {
            throw new RuntimeException("No browser logs present");
        }
    }

    private void saveClassificationAndBrowserHistory(BrowserDataList browserDataList, List<BrowserHistory> browserHistory, File tempFile) throws Exception {
        DBObject metaData = new BasicDBObject();
        metaData.put("browserId", browserDataList.browserId);
        InputStream inputStream = new FileInputStream(tempFile.getAbsolutePath());
        String fileId =
                gridFsTemplate.store(inputStream, "accessories.model", null, metaData).getId().toString();
        BrowserClassifications browserClassifications = new BrowserClassifications(browserDataList.browserId, fileId);
        this.mongoTemplate.save(browserClassifications, "BrowserClassifications");
        this.browserHistoryRepository.save(browserHistory);
    }

    private File generateClassifications(InputStream browserStream) throws Exception {
        JSONLoader jsonLoader = new JSONLoader();
        jsonLoader.setSource(browserStream);
        Instances train = jsonLoader.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);
        J48 nb = new J48();
        File tempFile = File.createTempFile("classification", "txt");
        SerializationHelper.write(tempFile.getAbsolutePath(), nb);
        return tempFile;
    }

    public InputStream convertToJSONFile(List<BrowserHistory> browserHistoryList, String browserId) throws IOException {
        String browserJsonObject = new Gson().toJson(getBrowserHistoryJson(browserHistoryList));
        InputStream browserJsonStream = new ByteArrayInputStream(browserJsonObject.getBytes());
        return browserJsonStream;
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
                    if (hostname != null && hostNameMap.get(hostname) > getLimitHostNameCount()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(new Double(browserHistory.getLastVisitTime()).longValue());
                        BrowserHistoryJsonData browserHistoryJsonData = new BrowserHistoryJsonData(false, 1.01,
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
        Set<String> hostNames = hostNameMap.keySet();
        String[] hostArr = new String[hostNames.size()];
        List<BrowserHistoryJsonHeaderAttribute> browserHistoryJsonHeaderAttributeList = new ArrayList<BrowserHistoryJsonHeaderAttribute>();
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute
                ("dayOfTheWeek", "numeric", false, 1.01f, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute(
                "time", "numeric", false, 1.01f, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("dayOfTheMonth", "numeric", false, 1.01f, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("Month", "numeric", false, 1.01f, null));
        browserHistoryJsonHeaderAttributeList.add(new BrowserHistoryJsonHeaderAttribute("weekOfTheMonth", "numeric", false, 1.01f, null));
        browserHistoryJsonHeaderAttributeList.add(
                new BrowserHistoryJsonHeaderAttribute("hostname", "nominal", false, 1.01f, hostNames.toArray(hostArr)));
        return browserHistoryJsonHeaderAttributeList;
    }

    public void getBrowserData(Long date,String browserId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        BrowserClassifications browserClassifications;
        browserClassifications = this.mongoTemplate.findOne(new Query(Criteria.where("_id").is(browserId)),
                BrowserClassifications.class);
        GridFSDBFile gridFsdbFile =  this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(
                browserClassifications.getFileId())));
        try{
            ObjectInputStream ois = new ObjectInputStream(
                    gridFsdbFile.getInputStream());
            Classifier cls = (Classifier) ois.readObject();
            double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    /* Create and an Instance with the above values and its class label set to "positive" */
            Instance instance = new DenseInstance(1.0,values);

            double label =cls.classifyInstance(instance);
            ois.close();

        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
