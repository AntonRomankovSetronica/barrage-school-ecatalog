# E-Catalog

Educational Project, Barrage's Java School

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