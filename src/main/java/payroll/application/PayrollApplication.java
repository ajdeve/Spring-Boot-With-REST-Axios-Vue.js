package payroll.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@ComponentScan({"payroll.controller","payroll.assembler"})
@EnableJpaRepositories(basePackages="payroll.dao")
@EntityScan("payroll.model.domain")
@SpringBootApplication
public class PayrollApplication {

	public static void main(String[] args) {
	    SpringApplication.run(PayrollApplication.class, args);
	}

}
