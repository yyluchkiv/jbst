### Changelog [v1.72]
— feat!: Java 21 virtual threads for async/events executors via new mandatory `jbst.async.virtual-threads` / `jbst.events.virtual-threads` (servers: true); `threads-*-pool-percentage` now optional (required only when virtual-threads is false); breaking — consumers must add the property to YAML
— refactor!: JbstWorker lock migrated synchronized → ReentrantLock (virtual-thread pinning avoidance); breaking — `getLock()` now returns `ReentrantLock`
— refactor!: JbstLatencySynchronizedQueue migrated to ReentrantLock-guarded queue; breaking — `JbstLatencyJSON` constructor now takes `List<Long>`
— refactor: SequencedCollection `getFirst()` (JEP 431) in JbstCollectors, JbstEnvUtils, JbstSpringBoot
— build: Generational ZGC (`-XX:+UseZGC -XX:+ZGenerational`) in docker-compose JVM_ARGUMENTS — testing/benchmarking purposes
