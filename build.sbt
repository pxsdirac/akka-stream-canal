name := "akka-stream-canal"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "com.alibaba.otter" % "canal.client" % "1.1.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.18"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "1.0-M1"
// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"

