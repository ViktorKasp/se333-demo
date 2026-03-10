package org.springframework.samples.petclinic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class PetClinicApplicationTests {

	@AfterEach
	void cleanup() {
		ConfigurableApplicationContext ctx = PetClinicApplication.getContext();
		if (ctx != null) {
			SpringApplication.exit(ctx);
		}
	}

	@Test
	void mainStartsAndExits() {
		// call main to execute the line that was previously uncovered
		PetClinicApplication
			.main(new String[] { "--spring.main.web-application-type=none", "--spring.main.banner-mode=off" });

		// after main returns we expect a context to be available and non-null
		ConfigurableApplicationContext ctx = PetClinicApplication.getContext();
		assertThat(ctx).isNotNull();
		assertThat(ctx.isActive()).isTrue();
	}

}