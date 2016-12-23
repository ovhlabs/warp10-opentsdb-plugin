# warp10-opentsdb-plugin

Warp10 plugin to add support for OpenTSDB protocol

## Build

```
gradle shadowJar
```

## Deploy

add to conf:

```
plugin.opentsdb.port = 8089
plugin.opentsdb.host = 127.0.0.1
plugin.opentsdb.idle.timeout = 600
plugin.opentsdb.jetty.threadpool = 4
plugin.opentsdb.jetty.maxqueuesize = 4
plugin.opentsdb.acceptors = 1
plugin.opentsdb.selectors = 1
plugin.opentsdb.warp10.endpoint = http://127.0.0.1:8080/api/v0/update
```

And add it to classpath at start ;-)