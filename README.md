# Variant Experience Server </br> Standard Server-side Extensions
### Release 0.9.3
#### Requires: Java 8 or later

[__Documentation__](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-4) | [__Javadoc__](https://getvariant.github.io/variant-extapi-standard/)

A library of standard extension objects for [Variant Experience Server](https://www.getvariant.com/resources/docs/0-9/experience-server/user-guide/), included in the Variant Server distribution as `variant-extapi-stadndard-<release>.jar` file. Server-side extensions are run by and provide runtime customization mechanism for Variant Experience server. They integrate with the server via [the server-side extension API, or ExtAPI](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-4).

Variant server-side extensions are one of two types: _Lifecycle Hooks_ and _Event Flushers_. 

## 1. Lifecycle Hooks 
Lifecycle hooks are callback methods subscribed to Variant server's life-cycle events. Whenever a lifecycle event is raised, all hooks subscribed to it are posted.  For example, the variation quealification event is raised when a user session is about to be qualified for a variation. For more information, see [Variant User Guide](https://www.getvariant.com/resources/docs/0-9/experience-server/user-guide/#section-4.7.1) for more information.

#### [ChromeTargetingHook](https://github.com/getvariant/variant-extapi-standard/blob/master/src/main/java/com/variant/extapi/standard/hook/ChromeTargetingHook.java)

Illustrates the use of a targeting hook. Used in the [Demo application](https://github.com/getvariant/variant-java-demo). Assigns all Chrome traffic to the control experience.

Configuration:
```
   'hooks': [
      ...
      {
         'name': 'myHook',
         'class': 'com.variant.extapi.standard.hook.ChromeTargetingHook'
      }
      ...
   ]
```
Refer to [Variant Server Reference](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-3) for more information on the variation schema grammar.

## 2. Trace Event Flushers
Event flushers handle the terminal ingestion of Variant trace events. They are responsible for writing out Variant trace events to some form of external storage, suitable for your technology stack, so they can be later used for analysis of Variant experiments. See [Variant User Guide](https://www.getvariant.com/resources/docs/0-9/experience-server/user-guide/#section-4.7.2) for more information.

#### 2.1. [TraceEventFlusherH2](https://github.com/getvariant/variant-extapi-standard/blob/master/src/main/java/com/variant/extapi/standard/flush/jdbc/TraceEventFlusherH2.java)

Writes trace events to an H2 database.  

Configuration:
For server-wide default configuration, which applies to all schemata managed by a Variant server that do not define their own flusher.
```
variant.event.flusher.class.name = com.variant.extapi.standard.flush.jdbc.TraceEventFlusherH2
variant.event.flusher.class.init = {"url":"jdbc:h2:<url>","user":"<user>","password":"<password>"}
 ```
Refer to [Variant Server Reference](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-2) for more information on Variant server configuration.

For schema-specific configuration (overrides the server-wide default):
```
   'flusher': {
      'class': 'com.variant.extapi.standard.flush.jdbc.TraceEventFlusherH2',
      'init': {'url':'jdbc:h2:<url>','user':'<user>','password':'<password>'}
   }
```

Refer to [Variant Server Reference](https://www.getvariant.com/resources/docs/0-9/experience-server/reference/#section-3) for more information on the variation schema grammar.

#### 2.2. [TraceEventFlusherPostgres](https://github.com/getvariant/variant-extapi-standard/blob/master/src/main/java/com/variant/extapi/standard/flush/jdbc/TraceEventFlusherPostgres.java)

Writes trace events to an PostgreSQL database.  

Configuration:
For server-wide default configuration, which applies to all schemata managed by a Variant server that do not define their own flusher.
```
variant.event.flusher.class.name = com.variant.extapi.standard.flush.jdbc.TraceEventFlusherPostgres
variant.event.flusher.class.init = {"url":"jdbc:postgresql:<url>","user":"<user>","password":"<password>"}
 ```
 
For schema-specific configuration (overrides the server-wide default):
```
   'flusher': {
      'class': 'com.variant.extapi.standard.flush.jdbc.TraceEventFlusherPostgres',
      'init': {'url':'jdbc:postgresql:<url>','user':'<user>','password':'<password>'}
   }
```

#### 2.3. [TraceEventFlusherMysql](https://github.com/getvariant/variant-extapi-standard/blob/master/src/main/java/com/variant/extapi/standard/flush/jdbc/TraceEventFlusherMysql.java)

Writes trace events to an H2 database.  

Configuration:
For server-wide default configuration, which applies to all schemata managed by a Variant server that do not define their own flusher.
```
variant.event.flusher.class.name = com.variant.extapi.standard.flush.jdbc.TraceEventFlusherMysql
variant.event.flusher.class.init = {"url":"jdbc:mysql:<url>","user":"<user>","password":"<password>"}
 ```
 
For schema-specific configuration (overrides the server-wide default):
```
   'flusher': {
      'class': 'com.variant.extapi.standard.flush.jdbc.TraceEventFlusherMysql',
      'init': {'url':'jdbc:mysql:<url>','user':'<user>','password':'<password>'}
   }
```

## 3. Adding Standard Extensions to Your Variant Server Instance
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

