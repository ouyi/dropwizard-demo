
[![Build Status](https://travis-ci.org/ouyi/dropwizard-demo.svg?branch=master)](https://travis-ci.org/ouyi/dropwizard-demo)

# README

This demo project showcases the following:

- dropwizard for RESTful web services
- rabbitmq as the message broker for service coordination
- jdbi for database accesses

## Overview

The dwdemo application allows some csv files to be uploaded, cleansed, transformed, and inserted into databases.

It (entry point: `io.github.ouyi.dwdemo.DemoApplication`) exposes two endpoints:

    PUT     /upload/{target} (io.github.ouyi.dwdemo.api.UploadResource)
    POST    /transform/{filename} (io.github.ouyi.dwdemo.api.TransformResource)

External dependencies of the application:

- message queue (tested with rabbitmq)
- database (configured to use h2 in the following sections)

After successfully processing a file upload request, the upload service publishes a message (filename) to the message 
queue. A worker application (entry point: `io.github.ouyi.dwdemo.mq.DemoWorker`) subscribes to the message queue. For
each of the messages received, it makes a POST request to the transform service, which then processes the uploaded file, 
whose lines are converted to records, which are inserted into the database.

For simplicity, the dwdemo application and the worker application are implemented in the same Java project. However, 
nothing prevents them from being deployed separately. The dwdemo application can also be easily deployed as two 
separate services.

## Assumptions

- Transformer always has access to the files uploaded, which is possible in either of the following cases:
    - centralized storage, e.g., S3 or NAS, or
    - no centralized storage: uploader, mq, and transformer run on the same host (horizontal scaling by host)
- The upload API caller dictates the file name on the server side, without directory structure (can be added easily)
- Re-uploading a file will overwrite the previously uploaded version
- On DB primary key conflict, the conflicting entries in the DB will be deleted (and overwritten)
- Dirty records (e.g., name is empty, or time_of_start does not match the pattern: MM-dd-yyyy HH:mm:ss) are dropped

## Features

- Idempotent and atomic PUT and POST
- Automatic DB migrations (table creation, schema evolution, etc)

## Tests

The following tests (including a test with the provided data set data_test.zip) are done successfully in this env:

    CentOS Linux release 7.3.1611 (Core) 
    OpenJDK Runtime Environment (build 1.8.0_111-b15)
    OpenJDK 64-Bit Server VM (build 25.111-b15, mixed mode)

### Build (includes unit tests) and poor man's packaging

    ./gradlew clean build && ./gradlew distZip && unzip build/distributions/dwdemo.zip -d build/distributions/

### Integration test

- Start rabbitmq

        docker run -p 15671:15671 -p 15672:15672 -p 25672:25672 -p 4369:4369 -p 5671:5671 -p 5672:5672 -d --hostname b50 --name rabbit0 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:3-management

- Integration test

        ./gradlew integrationTest

### Manual end-to-end tests

- Start the dwdemo application (services)

        ./gradlew run
    or

        build/distributions/dwdemo/bin/dwdemo db status build/resources/test/dwdemo.yml
        build/distributions/dwdemo/bin/dwdemo db migrate build/resources/test/dwdemo.yml
        build/distributions/dwdemo/bin/dwdemo server build/resources/test/dwdemo.yml

- Start the worker application

        java -cp "./build/distributions/dwdemo/lib/*" io.github.ouyi.dwdemo.mq.DemoWorker -c build/resources/test/worker.yml

- File upload

        curl -X PUT --data-binary @build/resources/test/test.csv localhost:8080/upload/test.csv
        ls -l /tmp/dwdemo/upload/

- File transform (optional, triggered by the Worker automatically)

        curl -X POST localhost:8080/transform/test.csv

- Verify data in the database

        # After stopping the dwdemo application (ctrl+c in the `./gradlew run` terminal), connect to the database using the database connection
        # data from build/resources/test/dwdemo.yml
        java -cp ./build/distributions/dwdemo/lib/h2-1.4.193.jar org.h2.tools.Shell
        sql> select count(1) from person;

## TODOs

- Add error handling (retries) to the resources or to the worker
- More CI/CD stuff (Jenkins, Ansible, Docker, packaging, etc)
