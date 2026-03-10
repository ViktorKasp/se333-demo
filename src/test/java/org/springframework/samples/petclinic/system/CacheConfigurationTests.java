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