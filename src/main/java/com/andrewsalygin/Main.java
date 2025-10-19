package com.andrewsalygin;

import com.andrewsalygin.config.AppConfig;
import com.andrewsalygin.service.MongoToPostgresConverterService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        MongoToPostgresConverterService service = context.getBean(MongoToPostgresConverterService.class);
        service.convertPostsInAllGroups();

        context.close();
    }
}