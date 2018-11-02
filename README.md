# Variant Experience Server Extension API (ExtAPI) Sample Objects
### Release 0.9.3
#### Requires: Java 8 or later

[__Documentation__](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-4) | [__Javadoc__](https://www.getvariant.com/javadoc/0.9/)

This project provides a development environment for the [Variant Experience Server's](https://www.getvariant.com/resources/docs/0-9/experience-server/user-guide/) [server-side extension API, or ExtAPI](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-4), used for injecting custom semantics into the server’s regular execution path. It exposes two principal mechanisms for injecting custom semantics into the server's default behavior:

* Life-cycle Hooks are callback methods that can be subscribed to the life-cycle events. This enables custom, application-aware semantics to take over and alter the default behavior.

* Event Flushers allow the application developer to define the persistence details of the trace events on the schema-by-schema basis.

Both life-cycle hooks and event flushers are configured in the experiment schema.

Start by clonining this repository into a local workspace:

```
% git clone https://github.com/getvariant/variant-server-extapi.git
```

The Variant ExtAPI is provided in the lib/variant-server-extapi-\<release\>.jar JAR file and the dependent Variant core library lib/variant-core-\<release\>.jar. You may either directly import these into your project or, if you use a dependency management tool like Maven, install them into your local Maven repository:

```
% mvn install:install-file -Dfile=/path/to/variant-server-extapi-<release>.jar -DgroupId=com.variant \
                -DartifactId=variant-server-extapi -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-<release>.jar -DgroupId=com.variant \
                -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar
```

Note the  repository also contains the extension objects used by the Petclinic demo application . These are provided for illustration only and can be removed if you don't need them.

You must make your extension classes available to Variant server by adding them to the server's runtime classpath. The simplest and recommended way of doing so is:

    Package your extension classes in JAR file.
    Copy the JAR file to the server's ext/ directory.
    Restart Variant server. 

Note, that if you're using Maven, you may take advantage of the included pom.xml file to package your extension classes:

% mvn package

This will build the distribution target/variant-extapi-custom-<release>.jar JAR file, which must be copied to the server's ext/ directory.

