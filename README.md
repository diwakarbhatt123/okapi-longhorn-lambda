The **Okapi Longhorn** project allows you to execute batch configurations remotely.

For more information about Okapi Longhorn, see the [corresponding page on the main wiki](http://www.opentag.com/okapi/wiki/index.php?title=Longhorn).

Bug report and enhancement requests: https://bitbucket.org/okapiframework/longhorn/issues

#### Downloads: ####

The latest stable version of Okapi Longhorn is at https://bintray.com/okapi/Distribution/Longhorn

#### Developing with the API ####

Java bindings for the Longhorn REST API are available as a maven artifact.  Add this repository and dependency and to your pom.xml:

```
#!xml
  <repositories>
    <repository>
        <id>okapi-longhorn-release</id>
        <name>Okapi Longhorn Release</name>
        <url>http://repository-opentag.forge.cloudbees.com/release/</url>
    </repository>
  </repositories>
  <!-- .... -->
  <dependencies>
    <dependency>
      <groupId>net.sf.okapi.lib</groupId>
      <artifactId>okapi-lib-longhorn-api</artifactId>
      <version>0.28</version>
    </dependency>
  </dependencies>
```

To develop with the latest nightly snapshot build, use this instead:
```
#!xml
  <repositories>
    <repository>
        <id>okapi-longhorn-release</id>
        <name>Okapi Longhorn Release</name>
        <url>http://repository-opentag.forge.cloudbees.com/snapshot/</url>
    </repository>
  </repositories>
  <!-- .... -->
  <dependencies>
    <dependency>
      <groupId>net.sf.okapi.lib</groupId>
      <artifactId>okapi-lib-longhorn-api</artifactId>
      <version>0.29-SNAPSHOT</version>
    </dependency>
  </dependencies>
```