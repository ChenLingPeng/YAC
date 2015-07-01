[![Build Status](https://travis-ci.org/ChenLingPeng/YAC.svg?branch=master)](https://travis-ci.org/ChenLingPeng/YAC)

# YAC (Yet Another Crawler)

YAC (spelled as **Yet Another Crawler**), is an extended edition for crawler [here](https://github.com/ChenLingPeng/arachnez) written by me. My plan is to add distributed feature first and some other functions. 

## Basic Feature (on plan)

1. Job specific crawler for website
2. Support login fetch
3. Support http proxy
4. Job oriented with multi-thread
5. Support incremental
6. Offer persist API with HDFS & Mysql
7. Job customization with config
8. Distributed
9. Bandwidth limit

## Features Completed

1. None

## Technology would like to use

1. [Scala](http://www.scala-lang.org/): which is a functional programming language
2. [Akka](http://akka.io/): which offer a well used actor model & distributed support
3. [HttpClient](http://hc.apache.org/httpcomponents-client-4.5.x/index.html): the real worker to get web pages
4. [Slick](http://slick.typesafe.com/): database library for scala
5. [Spray](http://spray.io/): for RESTful HTTP server
