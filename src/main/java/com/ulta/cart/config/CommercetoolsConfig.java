package com.ulta.cart.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;

@Configuration
public class CommercetoolsConfig {

    @Bean(destroyMethod = "close")
    public SphereClient sphereClient() throws IOException {
    	final Properties prop = new Properties();
        prop.load(CommercetoolsConfig.class.getResourceAsStream("/dev.properties"));
        SphereClientConfig config = SphereClientConfig.ofProperties(prop, "ct");
        final SphereClient asyncClient = SphereClientFactory.of().createClient(config);
        return asyncClient;
		// BlockingSphereClient.of(asyncClient, 20, TimeUnit.SECONDS);
	}
}