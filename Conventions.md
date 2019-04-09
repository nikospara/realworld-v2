# Coding conventions

## For pom.xml

### Dependencies

1. Group dependencies in the following groups, in this order: `WILDFLY SWARM`, `SPECS`, `PROJECT`, `OTHER`, `TEST`. Within each group sort alphabetically, first by group, then by artifact.
2. Place the versions directly in the dependency/plugin management entry, if used only once. As soon as a version number is used by more that one artifacts, create a property and reuse it.

# Naming conventions

## Data objects

1. Give the entity the same name as the domain, e.g. `User`
2. Name the model interface after the domain, adding the suffix -Data, e.g. `UserData`
3. Name concrete implementations of domain objects that act as arguments to the APIs after the domain with the -Param suffix, e.g. `UserParam`
