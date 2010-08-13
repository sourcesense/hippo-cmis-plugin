package com.sourcesense.opencmis.server;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.site.container.SpringComponentManager;
import org.junit.After;
import org.junit.Before;

/**
 * <p>
 * AbstractJaxrsSpringTestCase
 * </p>
 * <p>
 * <em>Note: This class will be removed when JAX-RS service components moved to core.</em>
 * </p>
 *
 * @version $Id: AbstractJaxrsSpringTestCase.java 22718 2010-05-11 12:53:42Z wko $
 */
public abstract class AbstractJaxrsSpringTestCase {

  protected ComponentManager componentManager;

  @Before
  public void setUp() throws Exception {
    this.componentManager = new SpringComponentManager(getContainerConfiguration());
    this.componentManager.setConfigurationResources(getConfigurations());

    this.componentManager.initialize();
    this.componentManager.start();
  }

  @After
  public void tearDown() throws Exception {
    this.componentManager.stop();
    this.componentManager.close();
  }

  /**
   * Required specification of spring configurations
   * @return the list of Spring configuration files to load @before tests start
   */
  protected String[] getConfigurations() {
    String classXmlFileName = getClass().getName().replace(".", "/") + ".xml";
    String classXmlFileName2 = getClass().getName().replace(".", "/") + "-*.xml";
    return new String[]{classXmlFileName, classXmlFileName2};
  }

  protected ComponentManager getComponentManager() {
    return this.componentManager;
  }

  /**
   *
   * @param name of the Spring component to load
   * @param <T> ObjectClass of the requested Spring component
   * @return The requested Spring component that matches the given name
   */
  protected <T> T getComponent(String name) {
    return getComponentManager().<T>getComponent(name);
  }

  protected Configuration getContainerConfiguration() {
    return new PropertiesConfiguration();
  }
}
