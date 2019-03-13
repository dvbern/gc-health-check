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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

public class GCHealthCheck {

	private static final int PERECENT_OF_PERCENT_FACTOR = 10000;
	private static final double PERECENT_FACTOR = 100.0;

	private static boolean initialised = false;
	private static long lastTimeAccessed = 0;
	private static long lastCollectionTime = 0;

	private final int maxPercent;

	GCHealthCheck(int gcMaxPercent) {
		maxPercent = gcMaxPercent;
	}

	@NonNull
	public static GCHealthCheck init(int gcMaxPercent) {
		if (initialised) {
			throw new IllegalStateException("GCHealthCheck is already initialised");
		}

		initialised = true;

		return new GCHealthCheck(gcMaxPercent);
	}

	static void destroy() {
		initialised = false;
		lastTimeAccessed = 0;
		lastCollectionTime = 0;
	}

	private static double getGCTimeInPercent(long collectionTime) {
		if (lastTimeAccessed == 0) {
			return 0;
		}

		long timeSinceLastAccessed = System.currentTimeMillis() - lastTimeAccessed;
		if (timeSinceLastAccessed <= 0) {
			return 0;
		}

		long thisCollectionTime = collectionTime - lastCollectionTime;

		long gcTimeInPercentOfPercents = thisCollectionTime * PERECENT_OF_PERCENT_FACTOR / timeSinceLastAccessed;

		return gcTimeInPercentOfPercents / PERECENT_FACTOR;
	}

	private static void updateTimeStamps(long collectionTimeMillis, long accesTimeMillis) {
		lastCollectionTime = collectionTimeMillis;
		lastTimeAccessed = accesTimeMillis;
	}

	/**
	 * The HealthCheck does not make sense when calling healthCheck.current() within a very short interval.
	 * In that case, it will most likely be in failed state (100% GC).
	 * The checks should be executed with some delay, to get reasonable percentage values.
	 */
	@NonNull
	public GCDetails current() {
		long collectionTime = getCollectionTimeMillis();
		long accessTimeMillis = System.currentTimeMillis();
		double gcTimeInPercent = getGCTimeInPercent(collectionTime);

		updateTimeStamps(collectionTime, accessTimeMillis);

		GCDetails gcDetails = new GCDetails(maxPercent, gcTimeInPercent, accessTimeMillis);

		return gcDetails;
	}

	private long getCollectionTimeMillis() {
		return ManagementFactory.getGarbageCollectorMXBeans().stream()
			.map(GarbageCollectorMXBean::getCollectionTime)
			.filter(millis -> millis != -1)
			.reduce(0L, Long::sum);
	}
}
