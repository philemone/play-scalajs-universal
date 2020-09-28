## Universal Play+Scala.js framework - For you to create web application!

### Run with Docker container

Make sure **application.conf** has mongodb configured as `mongodb.uri = "mongodb://root:example@mongodb:27017/test"`

Run app with: `docker-compose up` command.



### Run app without Docker

- Start **mongoDB** as you wish or use command `docker-compose up mongodb` inside a project main folder.

- Change **application.conf** `mongodb.uri` setting accordingly to your mongoDB instance.

*for ex. when running mongoDB from docker-compose as said earlier change it to: `mongodb.uri = "mongodb://root:example@localhost:27017/test"
`*

**When you are running mongoDB on your own** remember to init *test* db with *mongo-init.js* file.

- Run `sbt` and run application with `run` command.