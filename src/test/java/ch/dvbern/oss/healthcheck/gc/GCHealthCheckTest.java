/*
 * Copyright 2019 DV Bern AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * limitations under the License.
 */

package ch.dvbern.oss.healthcheck.gc;

import java.util.Timer;
import java.util.TimerTask;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GCHealthCheckTest {

	private static final Logger LOG = LoggerFactory.getLogger(GCHealthCheckTest.class);

	private GCHealthCheck healthCheck = null;

	@NonNull
	private static TimerTask run(@NonNull Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}

	@BeforeEach
	void setup() {
		healthCheck = GCHealthCheck.init(10);
	}

	@AfterEach
	void cleanup() {
		GCHealthCheck.destroy();
	}

	@Test
	void failMultipleRegistrations() {
		assertThrows(IllegalStateException.class, () -> GCHealthCheck.init(50), "GCHealthCheck is already "
			+ "initialised");
	}

	@Test
	void pollingTest() throws InterruptedException {
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(run(() -> {
			GCDetails current = healthCheck.current();
			LOG.info(current.toString());
			assertTrue(current.isHealthy());
		}), 0, 1000);

		timer.schedule(run(System::gc), 1200);
		timer.schedule(run(this::meow), 2100);
		timer.schedule(run(System::gc), 2300);

		Thread.sleep(5000);
	}

	@Test
	void fails() {
		assertTrue(healthCheck.current().isHealthy());

		meow();
		//noinspection CallToSystemGC
		System.gc();

		assertFalse(healthCheck.current().isHealthy());
	}

	@SuppressWarnings({ "unused", "UnusedAssignment", "ReuseOfLocalVariable" })
	private void meow() {
		int[] garbage = new int[1000];
		garbage = null;
	}
}
