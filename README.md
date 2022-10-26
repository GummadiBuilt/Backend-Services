# GummadiBuilt - API

## Pre-Requisites

1. JDK 1.8
2. Maven 3.8.4
3. PostgreSQL 13.x

GummadiBuilt APIs were written in Spring Boot and uses JAVA JDK 1.8. Project can be built using maven

## Contributing guidelines

Before you can start contributing on this repo run the follow command to enable the hooks on the repo
`git config core.hooksPath .githooks`

## Installing Dependencies

To install the dependencies run the following command `mvn install`

## Building a jar file

Run the command `mvn build`

## Swagger documentation

You can access swagger documentation using the url http://localhost:9001/api/swagger-ui/index.html

## Release

Run the following to install **standard-version** globally `npm i -g @dwmkerr/standard-version`
Once the plugin is installed globally run the command `npx standard-version --packageFiles pom.xml --bumpFiles pom.xml`
to create a tag and CHANGELOG.md file with all the changes


## Commit message example
Commit message should follow the pattern of [Conventional-Commits](https://www.conventionalcommits.org/en/v1.0.0/#summary)

### Pattern for commits
`
<type>[optional scope]: <description>
[optional body]
[optional footer(s)]
`

### Sample

`
git commit -m "docs(README.MD): updated readme for usage" -m "Documentation of change-log creation & tags were updated in readme file"