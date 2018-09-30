
# Spring Redis Mongo Fault Tolerant Application 

* A sample application that demonstrates a simple CRUD process with MongoDB as 
  a persistent store and Redis as a caching layer
  
* To achieve fault tolerance, we will be using a three node Redis Sentinel setup (locally),
  and a three node MongoDB cluster with two data nodes and one arbiter

## Setup environment 

* You can guide to setup a Redis sentinel cluster locally [here](https://github.com/Nj-kol/redis-ha/tree/master/redis-sentinel-demo)
* You can guide to setup a MongoDB cluster locally [here](https://github.com/Nj-kol/mongodb-ha/tree/master/mongodb-master-slave-demo)

# Run as a docker container

**Build Docker image**

mvn install 

mvn docker:build

**Recreate docker image**

mvn clean install 

mvn docker:build

**Create and run as a docker container**

docker run -it \
--name vendor-reviews-app \
--net=host \
-e SERVICE_PORT=8095 \
-e MONGO_URL=mongodb://localhost:40000,localhost:40001,localhost:40002/demo?replicaSet=myapp \
-e REDIS_CLUSTER_NAME=redis-cluster \
-e REDIS_SENTINEL_MACHINES=localhost:16380,localhost:16381,localhost:16382 \
-e CACHE_EXPIRY_IN_SEC=600 \
vendor-reviews

Runs in network mode

**Rerun the existing container**

docker start vendor-reviews-app

**Stop the container**

docker stop vendor-reviews-app

**Login to the container**

docker exec -it vendor-reviews-app bash

## Swagger API doc

http://localhost:8095/swagger-ui.html

## Quick DB commands

**MongoDB**

* Connect to a replica set from shell :

    mongo --host myapp/localhost:40000,localhost:40001,localhost:40002
  
	use demo
	
	db.vendor.find().pretty()
	db.vendor_reviews.find().pretty()

**Redis**

* Connect to Redis CLI 

    redis-cli -p 6380

    keys *

    get "vendorReviewCache:AP Apparels"

