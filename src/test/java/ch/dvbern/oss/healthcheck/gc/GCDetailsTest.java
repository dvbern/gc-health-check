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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GCDetailsTest {

	@Test
	void testImmutalbeProperties() {
		int maxPercent = 10;
		double gcTimeInPercent = 123.456;
		int accessTimeMillis = 123;

		GCDetails gcDetails = new GCDetails(maxPercent, gcTimeInPercent, accessTimeMillis);
		assertEquals(maxPercent, gcDetails.getMaxPercent());
		assertEquals(gcTimeInPercent, gcDetails.getGcTimeInPercent());
		assertEquals(accessTimeMillis, gcDetails.getAccessTimeMillis());
	}

	@Test
	void testIsOk() {
		assertTrue(new GCDetails(100, 100, 0).isHealthy());
		assertTrue(new GCDetails(0, 0, 0).isHealthy());
		assertTrue(new GCDetails(50, 49.9, 0).isHealthy());

		assertFalse(new GCDetails(0, 1, 0).isHealthy());
		assertFalse(new GCDetails(50, 50.1, 0).isHealthy());
	}

	@Test
	void testGetState() {
		assertEquals(GCState.HEALTHY, new GCDetails(100, 100, 0).getState());
		assertEquals(GCState.HEALTHY, new GCDetails(0, 0, 0).getState());
		assertEquals(GCState.HEALTHY, new GCDetails(50, 49.9, 0).getState());

		assertEquals(GCState.UNHEALTHY, new GCDetails(0, 1, 0).getState());
		assertEquals(GCState.UNHEALTHY, new GCDetails(50, 50.1, 0).getState());
	}

	@Test
	void testToString() {
		GCDetails gcDetails = new GCDetails(10, 9.02467, 0);
		assertEquals("Percent GC time: 9.02. Threshold: 10. State: HEALTHY", gcDetails.toString());
	}

	@Test
	void testToStringRoundsUp() {
		GCDetails gcDetails = new GCDetails(10, 9.02567, 0);
		assertEquals("Percent GC time: 9.03. Threshold: 10. State: HEALTHY", gcDetails.toString());
	}
}
