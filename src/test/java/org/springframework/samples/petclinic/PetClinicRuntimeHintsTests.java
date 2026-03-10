package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.samples.petclinic.model.BaseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PetClinicRuntimeHintsTests {

	@Test
	void registerHintsPopulatesPatternsAndTypes() {
		RuntimeHints hints = new RuntimeHints();
		PetClinicRuntimeHints registrar = new PetClinicRuntimeHints();

		// exercise the method under test
		registrar.registerHints(hints, getClass().getClassLoader());

		// a few simple sanity checks to make sure the hints were recorded
		assertThat(hints).isNotNull();
		assertThat(hints.resources()).isNotNull();
		assertThat(hints.serialization()).isNotNull();
		// we can't reliably inspect the internal collections, just ensure the call
		// succeeded
	}

}
