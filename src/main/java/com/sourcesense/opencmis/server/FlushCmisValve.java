package com.sourcesense.opencmis.server;

import org.hippoecm.hst.core.container.AbstractValve;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FlushCmisValve extends AbstractValve {

/*
  protected List<ResourceLifecycleManagement> resourceLifecycleManagements;

  private static final Logger log = LoggerFactory.getLogger(FlushCmisValve.class);

  public void setResourceLifecycleManagements(List<ResourceLifecycleManagement> resourceLifecycleManagements) {
    this.resourceLifecycleManagements = resourceLifecycleManagements;
  }
*/

  @Override
  public void invoke(ValveContext context) throws ContainerException {

    HttpServletResponse servletResponse = context.getServletResponse();

    try {
      servletResponse.flushBuffer();
    } catch (IOException e) {
      throw new ContainerException(e);
    }
        
    context.invokeNext();
  }

}