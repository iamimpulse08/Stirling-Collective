package co.uk.stirling_index.inventory.config;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(true)
                .parameterName("format")
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("hal", MediaType.parseMediaType("application/prs.hal-forms+json"));
    }
}
