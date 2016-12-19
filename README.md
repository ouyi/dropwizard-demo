
# Assumptions

- Transformer always has access to the files uploaded
    - centralized storage, e.g., S3 or NAS, or
    - no centralized storage: uploader, mq, and transformer run on the same host (horizontal scaling by host)
- The upload API caller dictates the file name on the server side, without directory structure (can be added in future iterations)
- Re-uploading a file will overwrite the previously uploaded version
- On DB primary key conflict, the conflicting entries in the DB will be deleted (and overwritten) 

# End-to-end tests

    curl -X PUT --data-binary @test.txt localhost:8080/upload/test.txt
