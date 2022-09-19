### Weather Forcast Service

To build the project run:
```
> sbt compile
```

To run/start the project use:
```
> sbt run
```

Once the application is running you make requests to the `http://localhost:8080/weather/lat,lon` endpoint.

For example:
```
curl -vvv http://localhost:8080/weather/40.7484,-73.9856
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /weather/40.7484,-73.9856 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Date: Sat, 17 Sep 2022 00:37:20 GMT
< Connection: keep-alive
< Content-Type: application/json
< Content-Length: 44
< 
* Connection #0 to host localhost left intact
{"temp":"Pleasant","summary":"Mostly Clear"}
```