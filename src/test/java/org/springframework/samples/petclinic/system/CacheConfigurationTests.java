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
package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CacheConfigurationTests {

	@Test
	void customizerCreatesVetCacheWithStatistics() {
		CacheConfiguration config = new CacheConfiguration();
		JCacheManagerCustomizer customizer = config.petclinicCacheConfigurationCustomizer();

		CacheManager cm = Mockito.mock(CacheManager.class);
		// simulate createCache returning null (not used)
		when(cm.createCache(eq("vets"), Mockito.any())).thenReturn(null);

		customizer.customize(cm);

		ArgumentCaptor<javax.cache.configuration.Configuration> captor = ArgumentCaptor
			.forClass(javax.cache.configuration.Configuration.class);
		verify(cm).createCache(eq("vets"), captor.capture());

		javax.cache.configuration.Configuration cfg = captor.getValue();
		assertThat(cfg).isInstanceOf(MutableConfiguration.class);
		MutableConfiguration<?, ?> mcfg = (MutableConfiguration<?, ?>) cfg;
		assertThat(mcfg.isStatisticsEnabled()).isTrue();
	}

}
