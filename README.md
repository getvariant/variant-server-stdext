# Variant Experience Server </br> Standard Server-side Extensions
### Release 0.9.3
#### Requires: Java 8 or later

[__Documentation__](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-4) | [__Javadoc__](https://getvariant.github.io/variant-extapi-standard/)

This project contains a set of standard extension objects for the [Variant Experience Server's](https://www.getvariant.com/resources/docs/0-9/experience-server/user-guide/) [server-side extension API, or ExtAPI](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-4).

used for injecting custom semantics into the server’s regular execution path. It exposes two principal mechanisms for injecting custom semantics into the server's default behavior:

* Life-cycle Hooks are callback methods that can be subscribed to the life-cycle events. This enables custom, application-aware semantics to take over and alter the default behavior.

* Event Flushers allow the application developer to define the persistence details of the trace events on the schema-by-schema basis.

Both life-cycle hooks and event flushers are configured in the experiment schema.

Start by clonining this repository into a local workspace:

```
% git clone https://github.com/getvariant/variant-server-extapi.git
```

The Variant ExtAPI is provided in the `lib/variant-server-extapi-\<release\>.jar` JAR file and the dependent library `lib/variant-core-\<release\>.jar`. You may either directly import these into your project or, if you use a dependency management tool like Maven, install them into your local Maven repository:

```
% mvn install:install-file -Dfile=/path/to/variant-server-extapi-<release>.jar -DgroupId=com.variant \
                -DartifactId=variant-server-extapi -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-<release>.jar -DgroupId=com.variant \
                -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar
```

Note that the repository contains several sample objects in `/src/main/java/com/variant/server/ext/demo/`. These are provided for illustration only and can be removed if you don't need them.

To make your extension classes available to Variant server at run time, you must package them into a JAR file and copy the jar file into Variant server's `ext/` directory, along with all the dependencies.

To package objects in this repository:

```
% mvn package
```

This will build the distribution JAR file in the `target/` directory, which you need to copy into Variant server's `ext/` directory, along with all the dependencies.

