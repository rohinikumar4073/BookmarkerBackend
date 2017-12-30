package com.bookmarker.repository;

import com.bookmarker.modal.BrowserHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BrowserHistoryRepository extends MongoRepository<BrowserHistory, String> {
    public BrowserHistory findById(String id);
    public BrowserHistory findByBrowserId(String browserId);

}
