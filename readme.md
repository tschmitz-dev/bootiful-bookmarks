# Bootiful Bookmarks
A sample cloud native application that is build up with Spring Boot.

## Docker
The project uses the Spotify dockerfile-maven-plugin to build Docker images of each module. To build the images simply 
run `mvn clean package`.

The default name schema for the images is tschmitz-dev/${project.artifactId}.

