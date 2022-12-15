# SAMS(StudyAliesMatchSystem)-BackEnd
### A simple system based on Spring-boot, Mybatis-plus and Redis.
#### Technical Point:
- Use Knife4j + Swagger to automatically generate back-end interface documents, and supplement interface annotations by writing ApiOperation and other annotations, avoiding the trouble of manually writing maintenance documents.
- Use Redis to implement distributed sessions to solve the problem of inter-cluster login status synchronization; and use Hash instead of String to store user information, which saves memory and facilitates single-field modification.
- Use the Levenshtein Distance algorithm to realize the function of matching the most similar users according to the label, and use the priority queue to reduce the memory usage during the TOP N operation.

#### To run the project, you need to revise `` application.yml `` 
```xml
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc: your database URL
    username: your database username
    password: your database password
```
```xml
# change into your own redis settings
  redis:
    port: 6379
    host: localhost
    database: 0
