package com.sourcesense.opencmis.server;

import org.hippoecm.hst.core.container.AbstractValve;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FlushCmisValve extends AbstractValve {

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