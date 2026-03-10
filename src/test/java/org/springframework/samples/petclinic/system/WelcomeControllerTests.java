package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WelcomeControllerTests {

	@Test
	void welcomeReturnsViewName() {
		WelcomeController controller = new WelcomeController();
		String view = controller.welcome();
		assertThat(view).isEqualTo("welcome");
	}

}