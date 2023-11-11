# E-Catalog

Educational Project, Barrage's Java School

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
  https://api.postman.com/collections/28483684-b2d6b745-da61-479d-a3ae-45aa460184a5?access_key=PMAT-01HEZ63EZ9TA1N7HKQBTSD9KGQ
  ```
    * Try `List All Products`
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