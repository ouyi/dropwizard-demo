
# Assumptions

- Uploader and Transformer run on the same host
- Allow re-upload of a file (implying delete and insert for the transformer)

# End-to-end tests

    curl -X PUT --data-binary @test.txt localhost:8080/upload/test.txt
