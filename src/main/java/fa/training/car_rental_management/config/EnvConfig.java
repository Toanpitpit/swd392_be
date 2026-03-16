package fa.training.car_rental_management.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment Post Processor
 * Loads environment variables from .env file early in Spring Boot lifecycle
 * This runs before property placeholders are resolved
 */
@Component
public class EnvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Load .env file if it exists
        String envFilePath = ".env";
        File envFile = new File(envFilePath);

        if (envFile.exists()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .filename(".env")
                        .ignoreIfMissing()
                        .load();

                // Create a map from .env entries
                Map<String, Object> envMap = new HashMap<>();
                for (DotenvEntry entry : dotenv.entries()) {
                    envMap.put(entry.getKey(), entry.getValue());
                    // Also set as system property for fallback
                    System.setProperty(entry.getKey(), entry.getValue());
                }

                environment.getPropertySources()
                        .addFirst(new MapPropertySource(".env", envMap));

            } catch (Exception e) {
                System.err.println("Failed to load .env file: " + e.getMessage());
            }
        }
    }
}

