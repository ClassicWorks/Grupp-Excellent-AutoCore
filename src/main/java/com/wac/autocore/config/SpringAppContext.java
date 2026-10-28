package com.wac.autocore.config;

import com.wac.autocore.security.Authenticator;
import com.wac.autocore.security.CurrentUserProvider;
import com.wac.autocore.service.GarageSystem;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringAppContext implements AppContext {
    private final ConfigurableApplicationContext context;

    public SpringAppContext() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);

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

    @Override
    public Authenticator authenticator() {
        return context.getBean(Authenticator.class);
    }

    @Override
    public CurrentUserProvider currentUserProvider() {
        return context.getBean(CurrentUserProvider.class);
    }
}
