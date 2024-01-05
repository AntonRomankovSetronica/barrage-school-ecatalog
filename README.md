# E-Catalog

Educational Project, Barrage's Java School

## Task 8

## Task 7

Your Uncle goes mad and decide to create a mobile app for his catalog. So people will be able to search and order
products right from their cell-phones.

We need to support his idea from backend perspective and introduce several new endpoints.

### What to do?

* We need a new endpoint for making orders. Let's skip all this story with payment and say that to make an order we just
  need to call some POST request. We only gonna state following:
    * Order's endpoint will be `/e-catalog/api/v1/order`.
    * Every order should contain products from exactly one merchant.
    * Order should be able to contain multiple products and amounts.
    * Order should keep info about user who's made it.
    * Order should keep date/time when it was made.
* You need to introduce new entity - POS (point of sale).
    * POS should have GEO point (lat, lon).
    * Merchant can have more than one POS (let say Uncle has one POS right at his farm, and is opening another at
      neighbor village).
    * You need a CRUD for POSes (try to make it as simple as only possible)
        * Let it be smth like: `/e-catalog/api/v1/merchants/{merchantId}/pos/...`
    * Let say all the story happens somewhere at [one-horse town](https://maps.app.goo.gl/UfZMzzTMEb9hgT7y5). Use points
      from this place for your POSes.
* For main screen of our mobile app we will need a new endpoint - `/e-catalog/api/v1/mobile/mainscreen`
    * It should have the exactly following structure:
      ```json
      {
        "merchants": [
          {
            "name": "M1",
            "popular-products": [
              { 
                "name":  "P1", 
                "image": "img-url"
              }
              // ... more products ordered by popularity
            ]
          }
          // ... more merchants ordered by POS
        ]
      }
      ```
    * Mobile app will send customer's coordinates with query params (e.g. `?lat=1.234&lng=5.678`)
    * There should be exactly 3 merchants sorted by nearest POS (merchant who has the nearest POS should be the first &
      etc)
        * PostGIS ([extension](https://github.com/postgis/docker-postgis), [distance calc example](https://copyprogramming.com/howto/how-can-i-get-distance-between-two-points-on-earth-from-postgis#postgres-calculate-distance-with-postgis), [docker](docker-compose.yaml#L7))
    * There should be exactly 4 products in every merchant and they should be sorted by popularity in last N days (make
      this N configurable). First product will have the biggest number of orders it involves in during past N days.
    * Try to fetch all needed data with a single SQL request!
* Tests!

## Task 6

Business goes well and your uncle now wants to get some stats from his catalog. He wants to know different things:

* How often people list products.
* How often people list products of particular merchants.
* How fast legacy sync happens
* And etc...

To measure these stats we gonna use following tools:

* [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators) / [Micrometer.io](https://micrometer.io/docs/concepts#_meters)
* [Prometheus](https://prometheus.io/)
* [Grafana](https://grafana.com/)

### What to do?

* Consider the example:
    * [Counter](src/main/java/net/barrage/school/java/ecatalog/web/ProductController.java#L30)
    * [Security config update](src/main/java/net/barrage/school/java/ecatalog/config/SecurityConfiguration.java#L38)
    * [Docker compose update](docker-compose.yaml)
* There are list of meters in Micrometer.io lib, try at least following: `Timer`, `Counter`, `Gauge`.
  But will be nice if you try all of them: https://micrometer.io/docs/concepts#_meters
* Configure Grafana
    * Login (http://localhost:3000/ admin/admin)
    * Create source
    * Create a dashboard
    * Add all your metrics as graphics
* Write a test which will create a random load to your app. Let say it will be configurable and will send N requests per
  minute to some defined endpoint.
* Run your test and check your dashboard represents load to your app correctly.

## Task 5

As we're going to have admin part - so we need to provide some security to our API. We don't want anybody will be able
to use it unauthorized.

There is a bunch of different security flows, however we're going to implement only following 4:

* Bearer
* Basic
* Form based auth (with storing security context to session)
* Anonymous

Our goal is to add all these methods to the service, so it will be possible to use all of them in parallel.

### What to do?

* Consider the [example](src/main/java/net/barrage/school/java/ecatalog/config/SecurityConfiguration.java) configuration
  for Bearer JWT and [docs](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html).
  Ask your mentor in case of questions.
    * [Spring boot security arch](https://docs.spring.io/spring-security/reference/servlet/architecture.html)
    * [Introduction to Spring Security Architecture](https://medium.com/@rasheed99/introduction-on-spring-security-architecture-eb5d7de75a4f)
* Search for [Basic auth](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html)
  and try to add its support.
* Search for
  what [session management](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html)
  is. Discuss it with your mentor and try to implement then.
* Add [anonymous](https://docs.spring.io/spring-security/reference/servlet/authentication/anonymous.html) roles support.
* Cover every method with a [test](src/test/java/net/barrage/school/java/ecatalog/app/SecurityTest.java)!

## Task 4

Business of your uncle and his friends goes really well. With new ads through your catalog they begin to have way more
orders. They got more money and now want to extend the functionality. First of all, they want to have an admin part, so
it will be possible to manage their products from it (CRUD).

So, now you need to introduce some database and provide CRUD interface for it!

Unfortunately not all of his friends are ready to migrate to this new CRUD. Guy with XML want to continue with current
schema.

### What to do?

* Run postgres locally using [dcoker-compose](docker-compose.yaml)
* Consider [the example](src/test/java/net/barrage/school/java/ecatalog/app/HibernateExampleTest.java) of how to copy
  products to db.
* You will need new CRUD Rest API, e.g. `CrudProductController`
    * And new mappings, e.g. `@PostMapping`, `@PutMapping` &
      etc ([brief example](https://www.javadevjournal.com/spring-boot/spring-boot-rest-example/))
    * What REST API calls will look like? Is following enough?
        * GET `/e-catalog/api/v1/merchants` - List merchants
        * GET `/e-catalog/api/v1/merchants/{merchantId}` - Get merchant by id
        * GET `/e-catalog/api/v1/merchants/{merchantId}/products` - List products from selected merchant
        * GET `/e-catalog/api/v1/products` - List all products
        * POST `/e-catalog/api/v1/products` - Create product
        * GET `/e-catalog/api/v1/products/{productId}` - Get product by id
        * PUT `/e-catalog/api/v1/products/{productId}` - Update product by id
        * DELETE `/e-catalog/api/v1/products/{productId}` - Delete product by id
* Think on how to migrate existed products to db
* Think on how to keep products R/O for merchants who aren't ready to migrate to crud. And think on how to keep them
  syncing with db.
* Implement new changes. And don't forget to cover them with tests.

## Task 3

Uncle and his friends are happy for now... It's time to think about tech dept :)

You are from CS department and realize that reading products from dropbox on every request is not what we would call
performance. Even reading it from local file is too expensive.

You want to cache all the products in memory and invalidate them from time to time.

And you decide to start covering your code with tests.

### What to do?

* Think on config change. What extra configs will be needed?
* How do you plan to schedule cache invalidation?
    * Consider [@Scheduled](https://www.baeldung.com/spring-scheduled-tasks) for invalidation job
* Try to write more tests
  in [ProductServiceImplTest](src/test/java/net/barrage/school/java/ecatalog/app/ProductServiceImplTest.java)
    * We gonna use [JUnit5](https://junit.org/junit5/docs/current/user-guide/) in our project
    * Write tests to cover search and your changes about product sources
    * Write more test classes if you need different [profile](src/test/resources/application-fake.yaml)

## Task 2

Uncle is really proud of his site! He has shown it to all his neighbours and friends. Bob and John are also farmers from
the same village. They've been thinking of similar catalog for quite a long time, but business takes all their time.
They always put it off until later and now ask ur uncle just to publish their products in the same catalog.

You agreed, but remembering the sync process, asked them to put their lists to cloud in same format. But these guys are
old, to old for this modern stuff... So they only manage to put their files only to Dropbox. Converting - it's something
they don't even understand in theory :)

So now u will have 3 sources:

- https://www.dropbox.com/scl/fi/mhq4vmv42x1hy5k430ae3/products.json?rlkey=00w6n2cfjk8p5wwizz6c4tckr&dl=0
- https://www.dropbox.com/scl/fi/rxime4x4tvzxoqvts5g4d/vegetables.xlsx?rlkey=7mrjxnnuqrxygmmi79di5klgl&dl=0
- https://www.dropbox.com/scl/fi/wbbsxcx6mh58iood031qo/wines.xml?rlkey=x7dmdsnv0gjvalskxwstlxjlr&dl=0

### What to do?

* Now instead of one local file in one format you gonna have 3 cloud links to files in 3 different formats: json, xml,
  xlsx. Think of it!
    * How to organise your code to be able to deal with all these 3 formats? What to do if in some nearest future you
      will need to support more formats?
    * Are there any libraries which can work with docx, xml? We don't want to invent a wheel. We want as less code as
      only possible.
    * Can [common design patterns](https://refactoring.guru/design-patterns/catalog) help you anyhow?
    * How do you think the configuration of your product sources should look like now?
        * Check the [old variant](src/main/resources/application.yaml)
        * Suggest something new. Keep in mind that the list of sources and formats can grow.
    * Read about [@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot). It may
      help u!
    * Read about [@Configuration](https://www.digitalocean.com/community/tutorials/spring-configuration-annotation). You
      will need it to create a bean factory for your new product sources!
* Without writing anything discuss your ideas with your team and mentor.
    * Find uncovered questions
    * Google them
    * Discuss everything again and finalize ur plan
* Create `TASK-2` branch, write your code and send to the mentor.

## Task 1

Let's begin with some business story :)

Imagine you is a student in CS department... and you have an uncle from some far village.
He has his own business, he produces and sells a lot of products of his farm:
cheese, meet, eggs, vegetables, fruits, wine & etc. And he wants to grow up and increase his sells.

He's remembered smart nephew and asked you to write a modern website with e-catalog of his products.
He is a smart uncle too, but a bit old and was able to learn how to work only in raw text editor.
So he gave you just a [json file](src/main/resources/static/products.json) of the products
and promised to send updates every week via e-mail.

And here is what you've done...
Put pressure on your imagination and think of some good front-end which we don't really have :)

But the back-end is here!

### What to do?

* Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
  and [Java 17 SDK](https://www.jetbrains.com/help/idea/sdk.html)
* Fork from this project into your private one and open access to your
  mentor ([AntonRomankovSetronica](https://github.com/AntonRomankovSetronica))
    * How to fork - https://docs.github.com/en/get-started/quickstart/fork-a-repo
    * How to open
      access - https://docs.github.com/en/account-and-profile/setting-up-and-managing-your-personal-account-on-github/managing-access-to-your-personal-repositories/inviting-collaborators-to-a-personal-repository
* Open it in Idea and run
    * `Ctrl-Shift-A` - `Execute Gradle Task` - `gradle bootRun`
    * Check if you can open http://localhost:8080/e-catalog/api/v1/products
* Install [Postman](https://www.postman.com/downloads/) and import following collection
  ```
  https://api.postman.com/collections/28483684-b2d6b745-da61-479d-a3ae-45aa460184a5?access_key=PMAT-01HF13G5TXZ4C1Q82V1PDBDWZB
  ```
    * Try `List All Products`
    * Try `Search All Products`, it should fail with 500
* For this task you will need to create a new branch `TASK-1` and to do following things:
    * Read carefully everything you have in `src/main/**` and `*.gradle` and try to understand.
      Everywhere you have any doubts or questions
      on what is going on - leave a text comment right in the code, e.g:
      ```java
      public static void main(String[] args) {
          // Wtf is this? Magic?
          SpringApplication.run(ECatalogApplication.class, args);
      }
      ```
    * Implement following search
      API - [ProductController::searchProducts](src/main/java/net/barrage/school/java/ecatalog/web/ProductController.java)
      ```
      http://localhost:8080/e-catalog/api/v1/products/search?q=text
      ```
    * Create MR and send it to your mentor. He will review it and go through your comments.