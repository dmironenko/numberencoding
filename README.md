Solution of task described in TASK.md

Technology stack:
- Java 7

Downloading project:
git clone https://github.com/dmironenko/numberencoding.git

Building project:
cd numberencoding
mvn clean install
Application is assembled to jar with manifest using maven jar plugin

Running project:
To run project you have to specify
- arg[0] - full path to file with dictionary.
- arg[1] - full path to file with tns.

Example of running:
java -jar target/numberencoding-1.0.jar /srv/dictionary.txt /srv/tns.txt