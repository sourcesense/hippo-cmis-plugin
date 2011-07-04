Hippo HST CMIS Server
-----------

This module enables HST to publish contents via CMIS.

The packaging is a WAR and it must be overlayed by your HST application in order to work.

The package already provides a simple CMIS repository.properties and all the Spring configurations that need to be injected in the HST configuration in order to have the CMIS Server interface to run.

In order to use it, you need to edit your pom.xml and web.xml:

1. Build HippoGoGreen
----
svn co http://svn.onehippo.org/repos/hippo/hippo-demos/hippo-go-green/tags/hippogogreen-3.03.03/
cd hippogogreen-3.03.03/
mvn clean install

2. Build Hippo CMIS Plugin
----

git clone git://github.com/sourcesense/hippo-cmis-plugin.git
cd hippo-cmis-plugin
mvn clean install

3. Edit site/src/main/webapp/WEB-INF/web.xml and add the following configurations:
---

<listener>
  <listener-class>org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener</listener-class>
</listener>
<listener>
  <listener-class>com.sun.xml.ws.transport.http.servlet.WSServletContextListener</listener-class>
</listener>

4 - Run CMS and Site
----

cd hippogogreen-3.03.03
mvn -Pcargo.run

5 - Try it
----

http://localhost:8085/site/preview/cmis
http://localhost:8085/site/preview/cmis/hst-cmis-repository-id/id?id=143140f9-1e75-457f-a782-381cd4eade7c
