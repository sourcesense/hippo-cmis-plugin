package com.sourcesense.opencmis.server;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.site.container.SpringComponentManager;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractJaxrsSpringTestCase
{

    protected final static Logger log = LoggerFactory.getLogger(AbstractJaxrsSpringTestCase.class);

    protected ComponentManager componentManager;

    @Before
    public void setUp() throws Exception {
        this.componentManager = new SpringComponentManager(getContainerConfiguration());
        ((SpringComponentManager) this.componentManager).setConfigurationResources(getConfigurations());

        this.componentManager.initialize();
        this.componentManager.start();
    }

    @After
    public void tearDown() throws Exception {
        this.componentManager.stop();
        this.componentManager.close();
    }

    /**
     * required specification of spring configurations
     * the derived class can override this.
     */
    protected String[] getConfigurations() {
        String classXmlFileName = getClass().getName().replace(".", "/") + ".xml";
        String classXmlFileName2 = getClass().getName().replace(".", "/") + "-*.xml";
        return new String[] { classXmlFileName, classXmlFileName2 };
    }

    protected ComponentManager getComponentManager() {
        return this.componentManager;
    }

    protected <T> T getComponent(String name) {
        return getComponentManager().<T>getComponent(name);
    }

    protected Configuration getContainerConfiguration() {
        PropertiesConfiguration propConf = new PropertiesConfiguration();
        return propConf;
    }
}
