package com.wac.autocore.config;

import com.wac.autocore.service.GarageSystem;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringAppContext implements AppContext {
    private final ConfigurableApplicationContext context;

    public SpringAppContext() {
        context = new SpringApplicationBuilder(SpringConfig.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run();
    }

    @Override
    public GarageSystem garageSystem() {
        return context.getBean(GarageSystem.class);
    }

    @Override
    public void close() {
        context.close();
    }
}
