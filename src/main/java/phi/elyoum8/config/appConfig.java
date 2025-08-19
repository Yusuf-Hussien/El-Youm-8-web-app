package phi.elyoum8.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class appConfig {
    
    @Bean
    ExecutorService executorService()
    {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    
    @Bean
    CommonsRequestLoggingFilter commonsRequestLoggingFilter()
    {
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        filter.setIncludeHeaders(true);
        filter.setIncludeClientInfo(true);

        return filter;
    }

}
