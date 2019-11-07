To reproduce:
 1. go into `example-maven-plugin`: `cd ./example-maven-plugin`
 2. install plugin locally: `mvn clean install`
 3. go into example project: `cd ../example-project`
 4. install it: `mvn clean install`
 5. try to install again: `mvn clean install` <- ERROR
