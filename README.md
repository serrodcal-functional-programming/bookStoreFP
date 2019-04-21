# Book Store

This repository contains an example of a Book Store Service developed using 
Functional Programming.

## Building

````bash
~$ mvn package
````

## Running

````bash
~$ java -jar target/book-card-service-0.1.0-SNAPSHOT-allinone.jar
````

## Testing

In other console tab, send _curl_ as given below:

````bash
~$ curl -i 'http://localhost:8080/bookCard/user@mail.com'
HTTP/1.1 200 OK
Server: akka-http/10.1.8
Date: Sun, 21 Apr 2019 22:38:59 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 56

BookCard(User(1,user@mail.com),List(Book(1,book_1_1,1)))%   
````