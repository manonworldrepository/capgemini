# Twilight Project Test Assessment

For the Twilight project we are looking for new developers. Because this project is high in both complexity and pressure,  
the requirement is that new developers are already familiar with the Spring framework and Java version 17.

*If at any time you have questions, feel free to ask! We would not expect you to work on the project alone without asking if you are blocked by something.*

You have _four_ hours to complete the test:

1) Make all tests from AssessmentApplicationTests pass.
2) Write good, clean, readable code.
3) Think about how you structure your code.
4) When done, zip your project and mail it back to the person you got it from.

You are free to use the internet to look up things. That's how all good coders work. However, make sure you understand what you copy ;)

The scenario is a system where we can store and retrieve books and authors.

## Source Data
In the folder test/resources you will find several *.json files. These are the files that are used as input (and output) of the endpoints you are going to write.

## Required Endpoints
You will need to write a SpringBoot application that can support the following endpoints:

Endpoint: /author  
HTTP Verb: GET  
Description: Retrieve all authors

Endpoint: /author  
HTTP Verb: POST  
Description: Create a new author

Endpoint: /author/{id}  
HTTP Verb: GET  
Description: Retrieve a specific author

Endpoint: /author/{id}  
HTTP Verb: PUT  
Description: Update a specific author

Endpoint: /author/search?query=term  
HTTP Verb: GET  
Description: Search the authors. Term is one of: most-books, last-modified

Endpoint: /book  
HTTP Verb: GET  
Description: Retrieve all books

Endpoint: /book  
HTTP Verb: POST  
Description: Create a new book

Endpoint: /book/{id}  
HTTP Verb: GET  
Description: Retrieve a specific book

Endpoint: /book/{id}  
HTTP Verb: PUT  
Description: Update a specific book

Endpoint: /book/search?query=term(&fromDate=2000-01-01&toDate=2000-01-01)
HTTP Verb: GET  
Description: Search the books. Term is one of: longest, oldest, last-modified, by-date
Additional: for by-date, 2 optional queryparameters are given: from-date and to-date

The data can be stored in the in-memory H2 database that is provided.
It can be accessed through spring-data-jpa means.

## The tests
The tests in the AssessmentApplicationTests class follow the general rule of making HTTP calls to the application, and checking the results.  
The class AssessmentApplicationTests contains a number of tests, they are structured into 4 parts:

// BASIC TESTS //  
void basicTest()

This test is just a smoketest for the empty project. Run it first to see if everything works.

// AUTHOR TESTS //  
testNoAuthors()   
testAddAuthor()  
testUpdateAuthorNotExists()   
testUpdateAuthorWrongId()  
testUpdateAuthor()  
testCreateAllAuthors()

These tests test the retrieval/creation/updating of authors

// BOOK TESTS //  
testNoBooks()  
testAddBookNoAuthor()   
testAddBookWithAuthor()   
testUpdateBookNotExists()  
testUpdateBookWrongId()  
testUpdateBook()  
testCreateAllBooks()

These tests test the retrieve/creation/updating of books

// FUNCTIONALITY TESTS //  
testSearchInvalidParameter()  
testFindLongestBook()  
testFindOldestBook()  
testFindBooksByDate()  
testFindAuthorWithMostBooks()  
testFindLastModifiedAuthor()  
testFindLastModifiedBook()

These tests test the search functionalities

// UTILITY METHODS //

These methods are used as utilities in the test, you can look at them if you want, but they are not very important

Solution:

To run tests and see the passing tests ``` ./mvnw clean test ```
To run API Docs ``` ./mvnw spring-boot:run ``` and then point your browser to ``` http://localhost:8080/swagger-ui.html ```
Default actuator endpoints are also installed

----------------------------------------------

### Start the application on kubernetes:

``` docker build -t proxysql-custom:k8s-local -f Dockerfile.proxysql . ```

``` docker build -t book-author-api:k8s-local -f Dockerfile.native . ```

``` minikube start --memory 8192 --cpus 4 ```

``` eval $(minikube -p minikube docker-env) ```

``` minikube image load proxysql-custom:k8s-local ```

``` minikube image load book-author-api:k8s-local ```

``` kubectl apply -f k8s-pvc.yml ```

``` kubectl create configmap proxysql-config --from-file=proxysql.cnf ```

``` kubectl apply -f k8s-deployment.yml ```

### Stop the application on kubernetes:

``` minikube delete ```

### Start the application on docker:

``` docker compose up -d --build ```

### Stop the application on docker:

``` docker compose down ```