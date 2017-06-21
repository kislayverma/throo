# Nexus
A Vert.x/Spring based HTTP reverse-proxy

## How to use this application
### Clone
Clone this repository to your system.

### Configure proxy routes
Open the "src/main/resources/spring/beans-proxy-routes.xml" file with your favourite and add the routes you want to proxy there along with other data to get this proxy working. A few sample routes are given under "src/main/resources/spring/samples/".    

### Build
mvn clean package    
This will generate a "nexus-<version>-fat.jar" under the "target" folder. This is an executable fat jar.

### Run
Execute the fat jar using the command : java -jar target/nexus-<version>-fat.jar    
This should start up Nexus on port 8080 of your machine. Hitting http://localhost:8080/<your proxied routes> should get you the data from the target server.

If you face problems related to DNS resolution (i.e. if you get an error saying the targte host could not be resolved, or no such host etc.), try running the application by disabling the netty DNS resolver and defaulting to the JDK DNS resolver.

Like this : java -Dvertx.disableDnsResolver=true -jar target/nexus-<version>-fat.jar    
