# Garbage Collection Health Check

This tiny library provides a simply metric for garbage collection usage:
the percentage of time that the JVM spends collecting garbage. 
The health check will report `HEALTHY`, if the percentage is below a specified threshold and `UNHEALTHY` otherwise. 
 
The GC percentage is calculated based on the last health check access time. 
Thus, you most likely get false positives when checking too often. Checking the health
every few seconds should suffice.

The calculations use some static properties. Thus you can only instantiate one `GCHealthCheck`. If you are using CDI, you can simply use a Producer as follows:

```java
@ApplicationScoped
public class HealthCheck {

	private GCHealthCheck gcHealthCheck = null;

	@Produces
	public GCHealthCheck produceGCHealthCheck() {
		return gcHealthCheck;
	}

	@PostConstruct
	public void init() {
		int threshold = 20;
		gcHealthCheck = GCHealthCheck.init(threshold);
	}
}
```
