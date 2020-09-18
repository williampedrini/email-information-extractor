package com.custodio.email.extractor.domain.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Definition of the class that holds the properties used to configure the application.
 *
 * @since 1.0.0
 */
@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationProperties {
    @Getter
    @Value("${email.account.datasource.file}")
    private String emailAccountDatasourceFile;
}
