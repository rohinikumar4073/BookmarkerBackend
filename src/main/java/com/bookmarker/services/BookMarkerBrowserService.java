package com.bookmarker.services;

import com.bol.crypt.CryptVault;
import com.bookmarker.modal.*;
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
import weka.core.Instances;
import weka.core.SerializationHelper;
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
    @Autowired
    CryptVault cryptVault;

    private boolean isSingleInstance;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public GridFsTemplate getGridFsTemplate() {
        return gridFsTemplate;
    }

    public void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public BrowserHistoryRepository getBrowserHistoryRepository() {
        return browserHistoryRepository;
    }

    public void setBrowserHistoryRepository(BrowserHistoryRepository browserHistoryRepository) {
        this.browserHistoryRepository = browserHistoryRepository;
    }

    public boolean isSingleInstance() {
        return isSingleInstance;
    }

    public void setSingleInstance(boolean singleInstance) {
        isSingleInstance = singleInstance;
    }

    public Integer getLimitHostNameCount() {
        return limitHostNameCount;
    }

    public void setLimitHostNameCount(Integer limitHostNameCount) {
        this.limitHostNameCount = limitHostNameCount;
    }

    public void saveUploadedFiles(BrowserDataList browserDataList) throws Exception {
        this.setSingleInstance(false);
        Type listType = new TypeToken<List<BrowserHistory>>() {
        }.getType();
        List<BrowserHistory> browserHistory = new Gson().fromJson(browserDataList.getBrowserLogs(),
                listType);
        if (browserHistory.size() > 0) {
            browserHistory.forEach(item -> item.setBrowserId(browserDataList.browserId));
            InputStream browserStream = convertToJSONFile(browserHistory);
            Instances train = getInstances(browserStream);
            File classificationsFile = generateClassifications(train);
            File instanceFile = generateInstancesFile(train);

            saveClassificationAndBrowserHistory(browserDataList, browserHistory, classificationsFile, instanceFile);
        } else {
            throw new RuntimeException("No browser logs present");
        }
    }

    private File generateInstancesFile(Instances train) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("instances", "modal");
            SerializationHelper.write(tempFile.getAbsolutePath(), train);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    private void saveClassificationAndBrowserHistory(BrowserDataList browserDataList, List<BrowserHistory> browserHistory,
                                                     File classificationFile, File instancesFile) throws Exception {
        DBObject metaData = new BasicDBObject();
        metaData.put("browserId", browserDataList.browserId);
        InputStream classificationfileInputStream = new FileInputStream(classificationFile.getAbsolutePath());
        InputStream instancesfileStream = new FileInputStream(instancesFile.getAbsolutePath());

        String fileId =
                gridFsTemplate.store(classificationfileInputStream, browserDataList.browserId, null, metaData).getId().toString();
        String trainFileId =
                gridFsTemplate.store(instancesfileStream, browserDataList.browserId, null, metaData).getId().toString();
        BrowserClassifications browserClassifications = new BrowserClassifications(browserDataList.browserId, fileId, trainFileId);
        this.mongoTemplate.save(browserClassifications, "BrowserClassifications");
        this.browserHistoryRepository.save(browserHistory);
    }

    private File generateClassifications(Instances train) throws Exception {
        J48 j48 = new J48();
        train.setClassIndex(train.numAttributes() - 1);
        j48.buildClassifier(train);
        File tempFile = File.createTempFile("classification", "modal");
        SerializationHelper.write(tempFile.getAbsolutePath(), j48);
        return tempFile;
    }

    private Instances getInstances(InputStream browserStream) throws IOException {
        JSONLoader jsonLoader = new JSONLoader();
        jsonLoader.setSource(browserStream);
        return jsonLoader.getDataSet();
    }

    public InputStream convertToJSONFile(List<BrowserHistory> browserHistoryList) throws IOException {
        String browserJsonObject = new Gson().toJson(getBrowserHistoryJson(browserHistoryList));
        InputStream browserJsonStream = new ByteArrayInputStream(browserJsonObject.getBytes());
        return browserJsonStream;
    }

    public BrowserHistoryJson getBrowserHistoryJson(List<BrowserHistory> browserHistoryList) {
        BrowserHistoryJson browserHistoryJson = new BrowserHistoryJson();
        Map<String, Integer> hostNameMap = null;
        hostNameMap = populateHostMap(browserHistoryList);
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
                    if (!(hostname == null || hostname.isEmpty() || hostname.contains("google")) && (this.isSingleInstance() || hostNameMap.get(hostname) > getLimitHostNameCount())) {
                        populateBrowserHistoryJSONData(browserHistoryJsonDataList, browserHistory, hostname);
                    }


                }

        );
        return browserHistoryJsonDataList;
    }

    private void populateBrowserHistoryJSONData(List<BrowserHistoryJsonData> browserHistoryJsonDataList, BrowserHistory browserHistory, String hostname) {
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

    public Map<String, Integer> populateHostMap(List<BrowserHistory> browserHistoryList) {
        Map<String, Integer> hostNameMap = new HashMap<String, Integer>();
        browserHistoryList.forEach(browserHistory -> {
                    String hostname = null;
                    try {
                        hostname = new URL(browserHistory.getUrl()).getHost();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (hostname != null && !hostname.isEmpty()) {
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

    public Set<String> getBrowserData(Long date, String browserId) {

        BrowserClassifications browserClassifications;
        browserClassifications = this.mongoTemplate.findOne(new Query(Criteria.where("_id").is(browserId)),
                BrowserClassifications.class);
        GridFSDBFile classifierFile = this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(
                browserClassifications.getFileId())));
        GridFSDBFile trainfile = this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(
                browserClassifications.getTrainFileId())));
        Map<String, Double> sortedMap = new LinkedHashMap<>();

        try {
            this.setSingleInstance(true);
            Classifier j48 = (Classifier) weka.core.SerializationHelper.read(classifierFile.getInputStream());
            Instances trainingData = (Instances) weka.core.SerializationHelper.read(trainfile.getInputStream());

            BrowserHistory browserHistory = new BrowserHistory();
            browserHistory.setLastVisitTime(date);
            browserHistory.setBrowserId(browserId);
            browserHistory.setUrl("http://www.dummy.com");
            List<BrowserHistory> browserHistoryList = new ArrayList<BrowserHistory>();
            browserHistoryList.add(browserHistory);
            Instances instances = getInstances(
                    convertToJSONFile(browserHistoryList)
            );
            instances.setClassIndex(instances.numAttributes() - 1);
            instances.get(0).setClassMissing();
            trainingData.add(instances.get(0));
            System.out.println(trainingData.classAttribute());
            double[] prediction = j48.distributionForInstance(trainingData.lastInstance());
            Map<String, Double> predictionMap = new HashMap<>();
            for (int i = 0; i < prediction.length; i = i + 1) {

                System.out.println("Probability of class " +
                        trainingData.classAttribute().value(i) +
                        " : " + Double.toString(prediction[i]));
                if (prediction[i] > 0) {
                    predictionMap.put(trainingData.classAttribute().value(i), prediction[i]);
                }
            }
            List<Map.Entry<String, Double>> entries
                    = new ArrayList<>(predictionMap.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            for (Map.Entry<String, Double> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sortedMap.keySet();

    }
}
