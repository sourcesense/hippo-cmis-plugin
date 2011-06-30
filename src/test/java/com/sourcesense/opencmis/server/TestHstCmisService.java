/*
  Copyright 2011 Sourcesense

  Licensed under the Apache License, Version 2.0 (the  "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS"
  BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

//package com.sourcesense.opencmis.server;
//
//import org.apache.chemistry.opencmis.client.api.Session;
//import org.apache.chemistry.opencmis.client.api.SessionFactory;
//import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
//import org.apache.chemistry.opencmis.commons.SessionParameter;
//import org.apache.chemistry.opencmis.commons.enums.BindingType;
//import org.hippoecm.hst.core.container.ContainerException;
//import org.hippoecm.hst.core.container.HstContainerConfig;
//import org.hippoecm.hst.core.container.Pipeline;
//import org.hippoecm.hst.core.container.Pipelines;
//import org.hippoecm.hst.site.HstServices;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.core.Response;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//public class TestHstCmisService extends AbstractJaxrsSpringTestCase {
//
//  protected Pipelines pipelines;
//  protected Pipeline jaxrsPipeline;
//  protected ServletConfig servletConfig;
//  protected ServletContext servletContext;
//  protected HstContainerConfig hstContainerConfig;
//
//  @Override
//  public void setUp() throws Exception {
//    super.setUp();
//    setUpOpenCmisClient();
//    setUpHstEnvironment();
//  }
//
//  private void setUpOpenCmisClient() throws Exception {
//    // default factory implementation of client runtime
//    SessionFactory f = SessionFactoryImpl.newInstance();
//    Map<String, String> parameter = new HashMap<String, String>();
//
//    // user credentials
//    parameter.put(SessionParameter.USER, "admin");
//    parameter.put(SessionParameter.PASSWORD, "admin");
//
//    // connection settings
//    parameter.put(SessionParameter.ATOMPUB_URL, "http://<host>:<port>/cmis/atom");
//    parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
//    parameter.put(SessionParameter.REPOSITORY_ID, "myRepository");
//
//    // session locale
//    parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
//    parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");
//    parameter.put(SessionParameter.LOCALE_VARIANT, "");
//  }
//
//  private void setUpHstEnvironment() throws Exception {
//    HstServices.setComponentManager(getComponentManager());
//
//    pipelines = (Pipelines) getComponent(Pipelines.class.getName());
//    jaxrsPipeline = this.pipelines.getPipeline("CmisPipeline");
//    assertNotNull(jaxrsPipeline);
//
//    servletConfig = getComponent("cmisServiceServletConfig");
//    servletContext = servletConfig.getServletContext();
//    assertNotNull(servletContext);
//
//    hstContainerConfig = new HstContainerConfig() {
//        public ClassLoader getContextClassLoader() {
//            return TestHstCmisService.class.getClassLoader();
//        }
//        public ServletConfig getServletConfig() {
//            return servletConfig;
//        }
//    };
//  }
//
//  @Test
//  public void testDemo() throws Exception {
//    MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
//    request.setProtocol("HTTP/1.1");
//    request.setScheme("http");
//    request.setServerName("localhost");
//    request.setServerPort(8085);
//    request.setMethod("GET");
//    request.setRequestURI("/site/preview/cmis/");
//    request.setContextPath("/site");
//    request.setServletPath("/preview/cmis");
//    request.setPathInfo("/");
//
//    MockHttpServletResponse response = new MockHttpServletResponse();
//
//    invokeCmisPipeline(request, response);
//
//    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    assertNotNull(response.getContentAsString());
//  }
//
//  protected void invokeCmisPipeline(HttpServletRequest request, HttpServletResponse response) throws ContainerException {
//      jaxrsPipeline.beforeInvoke(hstContainerConfig, request, response);
//
//      try {
//          jaxrsPipeline.invoke(hstContainerConfig, request, response);
//      } catch (Exception e) {
//          throw new ContainerException(e);
//      } finally {
//          jaxrsPipeline.afterInvoke(hstContainerConfig, request, response);
//      }
//  }
//
//}
