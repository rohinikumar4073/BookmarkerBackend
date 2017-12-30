package com.bookmarker.repository;

import com.bookmarker.modal.Browser;
import com.bookmarker.modal.BrowserHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BrowserRepository extends MongoRepository<Browser, String> {
    public BrowserHistory findById(String id);
}
