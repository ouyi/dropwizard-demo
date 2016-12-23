
# Assumptions

- Transformer always has access to the files uploaded
    - centralized storage, e.g., S3 or NAS, or
    - no centralized storage: uploader, mq, and transformer run on the same host (horizontal scaling by host)
- The upload API caller dictates the file name on the server side, without directory structure (can be added in future iterations)
- Re-uploading a file will overwrite the previously uploaded version
- On DB primary key conflict, the conflicting entries in the DB will be deleted (and overwritten) 
- No other data cleansing required (e.g., id is valid int)

# Features

- Idempotent and atomic PUT

# End-to-end tests

- File upload

    curl -X PUT --data-binary @test.csv localhost:8080/upload/test.csv

- File transform

    curl -X POST localhost:8080/transform/test.csv

- Starting rabbitmq

    docker run -p 15671:15671 -p 15672:15672 -p 25672:25672 -p 4369:4369 -p 5671:5671 -p 5672:5672 -d --hostname b50 --name rabbit0 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:3-management

# TODOs

- Add mq stuff
- Extract FileWriter
- CI/CD
- Delete conflicting entries before inserting (no need if not h2)
