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

import edu.umd.cs.findbugs.annotations.NonNull;

public class GCDetails {

	private final int maxPercent;
	private final double gcTimeInPercent;
	private final long accessTimeMillis;

	public GCDetails(int maxPercent, double gcTimeInPercent, long accessTimeMillis) {
		this.maxPercent = maxPercent;
		this.gcTimeInPercent = gcTimeInPercent;
		this.accessTimeMillis = accessTimeMillis;
	}

	public boolean isHealthy() {
		return gcTimeInPercent <= maxPercent;
	}

	@NonNull
	public GCState getState() {
		return isHealthy() ? GCState.HEALTHY : GCState.UNHEALTHY;
	}

	@Override
	@NonNull
	public String toString() {
	return 	String.format("Percent GC time: %.2f. Threshold: %d. State: %s", gcTimeInPercent, maxPercent, getState());
	}

	public int getMaxPercent() {
		return maxPercent;
	}

	public double getGcTimeInPercent() {
		return gcTimeInPercent;
	}

	public long getAccessTimeMillis() {
		return accessTimeMillis;
	}
}
