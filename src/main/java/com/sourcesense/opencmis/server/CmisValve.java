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
package com.sourcesense.opencmis.server;

import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.impl.atompub.RepositoryService;
import org.apache.chemistry.opencmis.server.shared.Dispatcher;
import org.hippoecm.hst.core.container.AbstractValve;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CmisValve extends AbstractValve {

  private static Logger logger = LoggerFactory.getLogger(InitCmisValve.class);

  @Override
  public void invoke(ValveContext context) throws ContainerException {

    logger.debug("Debugging CmisValve.invoke()");

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
