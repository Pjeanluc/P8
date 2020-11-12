#TourGuide App
TourGuide is a travel app made for all.

## purpose
You can get : 
- Users and their positions
- All users locations
- Add user's preferences
- User's trip deals
- nearby attractions

## Model
![](DiagrammeModel.png)

## Prerequisite to run

- Java 1.8 JDK
- Gradle 6.6.1
- Docker

## Run app (localhost:8082)

Spring Boot
~~~
mvn spring-boot:run (run app)
mvn spring-boot:stop (stop app)
~~~~

Gradle
~~~
gradle bootRun
~~~

## Containerize
to build the image
~~~
docker build - t tourguide .
~~~

To run the app : 
~~~
docker run -d -p 8082:8282 -- name tourguide tourguide
~~~

## Documentation
 Documentation Postman [here](https://documenter.getpostman.com/view/10925968/TVYDdedS)
   
    