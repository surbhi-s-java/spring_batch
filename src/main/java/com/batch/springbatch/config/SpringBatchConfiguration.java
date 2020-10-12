package com.batch.springbatch.config;

import com.batch.springbatch.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    ItemProcessor<User,User> itemProcessor;
    @Autowired
    ItemWriter<User> itemWriter;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value(value = "${file}")
    private Resource resource;
    @Bean
    Job job(){
        return jobBuilderFactory.get("etl-load-job").incrementer(new RunIdIncrementer()).start(step()).build();
    }

    @Bean
    Step step(){
        return stepBuilderFactory.get("etl-job-step")

                .<User,User>chunk(100)
                .reader(itemReader())
                .processor(itemProcessor)
                .writer(itemWriter).build();
    }

    private ItemReader<User> itemReader() {
        FlatFileItemReader<User> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setStrict(false);
        itemReader.setLineMapper(lineMapper());
        itemReader.setResource(resource);
        return itemReader;
    }

    private LineMapper<User> lineMapper() {
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames(new String[]{"id","name","dept","salary"});
        BeanWrapperFieldSetMapper<User> fieldSetMapper  = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(lineTokenizer);
        return lineMapper;
    }

}
