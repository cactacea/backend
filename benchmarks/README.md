# Cactacea Benchmarks

We use [JMH](https://openjdk.java.net/projects/code-tools/jmh/) as our benchmarking framework.

## Running

[JMH](https://openjdk.java.net/projects/code-tools/jmh/) is integrated via the 
[`sbt-jmh`](https://github.com/ktoso/sbt-jmh) plugin.

Benchmarks can be run using [SBT](https://www.scala-sbt.org/) via the plugin, e.g., from the
top-level Cactacea directory to run all benchmarks:

```
[backend]$ ./sbt 'project benchmarks' jmh:run
```

or to run a specific benchmark:

```
[backend]$ ./sbt 'project benchmarks' 'jmh:run AuthenticationControllerBenchmark'
```