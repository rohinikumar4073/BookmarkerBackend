package com.bookmarker;

import com.bookmarker.modal.BrowserDataList;
import com.bookmarker.modal.BrowserHistory;
import com.bookmarker.repository.BrowserHistoryRepository;
import com.bookmarker.util.TestUtils;
import com.google.gson.Gson;
import com.mongodb.WriteResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookMarkerApplication.class)
@AutoConfigureMockMvc
public class BookMarkControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BrowserHistoryRepository browserHistoryRepository;
    @MockBean
    private MongoTemplate mongoTemplate;
    @MockBean
    private GridFsTemplate gridFsTemplate;

    @Test
    public void loadBrowserData() throws Exception {
        String jsonData = "";
        doReturn(new WriteResult(1, true, null)).when(this.browserHistoryRepository)
                .save(Mockito.any(BrowserHistory.class));
        doReturn(new WriteResult(1, true, null)).when(this.mongoTemplate)
                .save(Mockito.any(String.class),Mockito.any(String.class));
        doReturn(new WriteResult(1, true, null)).when(this.gridFsTemplate)
                .store(Mockito.any(InputStream.class),Mockito.any(String.class),Mockito.any(String.class),
                        Mockito.any(String.class));
        String browserHistoryString = TestUtils.createBrowserHistoryString();
        String jsonString = new Gson().toJson(new BrowserDataList(browserHistoryString, "1234"));
        mvc.perform(post("/browserData").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)).andExpect(status().isOk())
                .andExpect(content().string("Successfully uploaded data "));

    }


}
