# Bootiful Bookmarks
[![Build Status](https://travis-ci.com/tschmitz-dev/bootiful-bookmarks.svg?branch=master)](https://travis-ci.com/tschmitz-dev/bootiful-bookmarks)

A sample cloud native application that is build up with Spring Boot.

Pull the project with `git pull --recurse-submodules`

## Docker
The project uses the Spotify dockerfile-maven-plugin to build Docker images of each module. To build the images simply 
run `mvn clean package`.

The default schema for the image names is tschmitz-dev/${project.artifactId}.
