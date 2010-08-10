package com.sourcesense.opencmis.server;

import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: enricocervato
 * Date: Jul 28, 2010
 * Time: 2:43:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class MyServiceFactory extends AbstractServiceFactory {

  @Override
  public CmisService getService(CallContext callContext) {
    return new MyCmisServiceImpl();
  }

  public CmisService getService(CallContext callContext, HttpServletRequest request, HttpServletResponse response) {
    return new MyCmisServiceImpl(request, response);
  }

  @Override
  public void init(Map<String, String> parameters) {

    super.init(parameters);
  }

  @Override
  public void destroy() {
    super.destroy();
  }
}

