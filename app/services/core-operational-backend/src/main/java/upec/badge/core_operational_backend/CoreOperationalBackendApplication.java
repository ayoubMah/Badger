package upec.badge.core_operational_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CoreOperationalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreOperationalBackendApplication.class, args);
	}

}
