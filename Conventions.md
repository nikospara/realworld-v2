# Coding conventions

## General

1. TAB for indentation.

## For pom.xml

### Dependencies

1. Group dependencies in the following groups, in this order: `QUARKUS`, `SPECS`, `PROJECT`, `OTHER`, `TEST`. Within each group sort alphabetically, first by group, then by artifact.
2. Define the version of dependencies and plugins in properties, even if used only in a single dependency. This makes it easy to upgrade dependencies with `mvn -N versions:display-property-updates`.

# Naming conventions

## Data objects

1. Give the entity the name of the domain with the `-Entity` suffix, e.g. `UserEntity`
2. Name the model interface after the domain, adding the suffix -Data, e.g. `UserData`. Use the `-Data` suffix for types that are input to business logic, even if they do not correspond to domain types.
3. Name concrete implementations of domain objects that act as arguments to the APIs after the domain with the `-Param` suffix, e.g. `UserParam`
4. Name classes that are (more or less) full representations of a domain object to an external API with the `-Dto` suffix, e.g. `ArticleSearchResultDto`
    - **What is the difference between `-Param` and `-Dto`?**
        1. `-Param` is *input* to an API, when the data differ significantly from the domain object
        2. `-Dto` can be *both input and output*, both when there is no relevant domain object for the transferred data, or when the information we need to transfer closely matches the structure of the domain object.

## Tests

1. Name the tested thing `sut` for "system under test"
