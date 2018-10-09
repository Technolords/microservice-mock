package net.technolords.micro.test.factory;

import java.util.ArrayList;
import java.util.List;

import net.technolords.micro.model.jaxb.Configuration;

public class ConfigurationsFactory {

    public static List<Configuration> createConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        // Get
        Configuration configuration = new Configuration();
        configuration.setType("get");
        configuration.setUrl("/mock/get");
        configurations.add(configuration);
        // Post 1
        configuration = new Configuration();
        configuration.setType("post");
        configuration.setUrl("/mock/post1");
        configurations.add(configuration);
        // Post 2
        configuration = new Configuration();
        configuration.setType("post");
        configuration.setUrl("/mock/post2");
        configurations.add(configuration);
        return configurations;
    }
}
