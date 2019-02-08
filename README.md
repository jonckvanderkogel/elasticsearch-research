This is a research project how to access Elasticsearch programmatically using the new RestHighLevelClient.

First, pull the Elasticsearch Docker image:
```
docker pull docker.elastic.co/elasticsearch/elasticsearch:6.6.0
```

Start the application by running:
```
mvn spring-boot:run
```

Next, you can query the api as follows.

To get all the terms:
```
curl localhost:8080/search/terms
```

To query how many times a certain phrase occurs within a certain term:
```
curl localhost:8080/search/terms
```

To query how many times a certain phrase occurs within a certain term:
```
curl "localhost:8080/search/terms?query=hostile"
```

To query in which quote a certain phrase occurs:
```
curl "localhost:8080/search?query=hostile"
```

