/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
