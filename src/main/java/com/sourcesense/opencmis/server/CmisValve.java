package com.sourcesense.opencmis.server;

import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.impl.atompub.RepositoryService;
import org.apache.chemistry.opencmis.server.shared.Dispatcher;
import org.hippoecm.hst.core.container.AbstractValve;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CmisValve extends AbstractValve {

  @Override
  public void invoke(ValveContext context) throws ContainerException {

    HttpServletRequest servletRequest = context.getServletRequest();
    HttpServletResponse servletResponse = context.getServletResponse();

    //Set by the InitCmisValve
    CallContext callContext = (CallContext) servletRequest.getAttribute(CmisHelper.CALL_CONTEXT_PARAM);
    CmisService cmisService = (CmisService) servletRequest.getAttribute(CmisHelper.CMIS_SERVICE_PARAM);

    try {
      dispatch(context, cmisService, callContext, servletRequest, servletResponse);
    } catch (CmisPermissionDeniedException e) {
        if ((callContext == null) || (callContext.getUsername() == null)) {
          throw new CmisUnauthorizedException("Authentication Required!",e);
        }
    } catch (Exception e) {
     CmisHelper.sendContainerError(e, servletResponse);
    }

    context.invokeNext();
  }


  private void dispatch(ValveContext valveContext, CmisService service, CallContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {

    try {

      // analyze the path
      String[] pathFragments = CmisHelper.splitPath(request);

      if (pathFragments.length < 2) {
        // root -> service document
        RepositoryService.getRepositories(context, service, request, response);
        return;
      }

      String method = request.getMethod();
      String repositoryId = pathFragments[0];
      String resource = pathFragments[1];

      Dispatcher dispatcher = (Dispatcher) valveContext.getRequestContainerConfig().getServletContext().getAttribute("dispatcher");

      // dispatch
      boolean methodFound = dispatcher.dispatch(resource, method, context, service, repositoryId, request,
          response);

      // if the dispatcher couldn't find a matching method, return an
      // error message
      if (!methodFound) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Unknown operation");
      }
    } finally {
      if (service != null) {
        service.close();
      }
    }
  }
}
