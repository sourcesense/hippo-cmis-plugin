Hippo HST CMIS Server
-----------

This module enables HST to publish contents via CMIS.

The packaging is a WAR and it must be overlayed by your HST application in order to work.

The package already provides a simple CMIS repository.properties and all the Spring configurations that need to be injected in the HST configuration in order to have the CMIS Server interface to run.

In order to use it, you need to edit your pom.xml and web.xml:

1. Build HippoGoGreen
----

svn co http://svn.onehippo.org/repos/hippo/hippo-demos/hippo-go-green/tags/hippogogreen-3.03.01/
cd hippogogreen-3.03.01/
mvn clean install

2. Build Hippo CMIS Plugin
----

git clone git://github.com/sourcesense/hippo-cmis-plugin.git
cd hippo-cmis-plugin
mvn clean install

3. Install Hippo CMIS into Hippo GoGreen
----

3.1. Edit site/pom.xml

Add

<dependency>
  <groupId>com.sourcesense.hippo.opencmis</groupId>
  <artifactId>hippo-cmis-plugin</artifactId>
  <version>3.03.01</version>
  <type>war</type>
</dependency>

and, in the plugins section

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-war-plugin</artifactId>
  <configuration>
    <overlays>
      <overlay>
        <groupId>com.sourcesense.hippo.opencmis</groupId>
        <artifactId>hippo-cmis-plugin</artifactId>
      </overlay>
    </overlays>
  </configuration>
</plugin>

3.2. Edit site/src/main/webapp/WEB-INF/web.xml and add the following configurations:

<listener>
  <listener-class>org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener</listener-class>
</listener>
<listener>
  <listener-class>com.sun.xml.ws.transport.http.servlet.WSServletContextListener</listener-class>
</listener>

3.3. Rebuild site module

cd site
mvn clean install

4 - Run CMS and Site
----

cd hippogogreen-3.03.01
mvn -Pcargo.run

5 - Create cmisrestapi node
----

Open the Hippo Console

http://localhost:8080/cms/console/

Navigate through

hdt:hst / hst:hosts / dev-localhost / localhost / hst:root

Copy the node restapi renaming it cmisrestapi

Modify its content replacing

hst:alias -> cmisrestapi
hst:namedpipeline -> CmisRestContentPipeline

Do the same with the reatapi node into preview (same node path)

5 - Try it
----

http://localhost:8080/site/preview/cmisrestapi
http://localhost:8080/site/preview/cmisrestapi/products/id?id=e3de6d62-beae-4536-a687-c750cb355fa5
