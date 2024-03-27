package sk.fiit.bp.BeesWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "sk.fiit.bp.BeesWeb.repository")
public class BeesWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeesWebApplication.class, args);
	}

}
