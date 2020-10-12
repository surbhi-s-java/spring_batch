package com.batch.springbatch.config;

import com.batch.springbatch.Repository.UserRepository;
import com.batch.springbatch.model.User;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DbItemWriter implements ItemWriter<User> {

    @Autowired
    UserRepository repository;
    @Override
    public void write(List<? extends User> list) throws Exception {
        repository.saveAll(list);

    }
}
