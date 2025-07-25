package phi.elyoum8.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class appConfig {
    @Bean
    ExecutorService executorService()
    {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
