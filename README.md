# warp10-opentsdb-plugin

Warp10 plugin to add support for OpenTSDB protocol

It let you send OpenTSDB input format to your favourite Warp10 instance

## Input format example 

```
{
  "metric": "os.cpu",
  "timestamp": 1,
  "value": 5,
  "tags": {
    "a": "b",
    "c": "d"
  }
}
```
Multiple data-points
```
[
    {
        "metric": "sys.cpu.nice",
        "timestamp": 1346846400,
        "value": 18,
        "tags": {
           "host": "web01",
           "dc": "lga"
        }
    },
    {
        "metric": "sys.cpu.nice",
        "timestamp": 1346846400,
        "value": 9,
        "tags": {
           "host": "web02",
           "dc": "lga"
        }
    }
]

```

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

## Contributing

Instructions on how to contribute to warp10-opentsdb-plugin are available on the [Contributing][contrib] page.

[contrib]: CONTRIBUTING.md
