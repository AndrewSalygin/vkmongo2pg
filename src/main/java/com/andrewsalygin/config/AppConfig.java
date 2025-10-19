package com.andrewsalygin.config;

import me.paulschwarz.springdotenv.DotenvConfig;
import me.paulschwarz.springdotenv.DotenvPropertySource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

@Configuration
@ComponentScan("com.andrewsalygin")
@PropertySource("classpath:application.yaml")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        MutablePropertySources sources = new MutablePropertySources();

        // .env
        DotenvPropertySource dotenvSource = new DotenvPropertySource(new DotenvConfig(new Properties()));
        sources.addLast(dotenvSource);

        // yaml
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yaml"));
        Properties yamlProps = yaml.getObject();
        if (yamlProps != null) {
            sources.addLast(new PropertiesPropertySource("yaml", yamlProps));
        }

        configurer.setPropertySources(sources);
        return configurer;
    }
}
