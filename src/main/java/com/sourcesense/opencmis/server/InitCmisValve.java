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

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.impl.CallContextImpl;
import org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener;
import org.apache.chemistry.opencmis.server.impl.atompub.*;
import org.apache.chemistry.opencmis.server.shared.CallContextHandler;
import org.apache.chemistry.opencmis.server.shared.Dispatcher;
import org.hippoecm.hst.core.container.AbstractValve;
import org.hippoecm.hst.core.container.ContainerConstants;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.Map;

public class InitCmisValve extends AbstractValve {

  private static Logger logger = LoggerFactory.getLogger(InitCmisValve.class);

  private static final String METHOD_GET = "GET";
  private static final String METHOD_POST = "POST";
  private static final String METHOD_PUT = "PUT";
  private static final String METHOD_DELETE = "DELETE";

  private static final String CALL_CONTEXT_HANDLER_CLASS = "org.apache.chemistry.opencmis.server.shared.BasicAuthCallContextHandler";

  @Override
  public void invoke(ValveContext context) throws ContainerException {

    logger.debug("Debugging InitCmisValve.invoke()");

    HttpServletRequest servletRequest = context.getServletRequest();
    HttpServletResponse servletResponse= context.getServletResponse();

    HstRequestContext requestContext = (HstRequestContext) servletRequest.getAttribute(ContainerConstants.HST_REQUEST_CONTEXT);

    if (requestContext == null) {
      //requestContext = getRequestContextComponent().create(servletRequest, context.getServletResponse(), getContainerConfiguration());
      requestContext = getRequestContextComponent().create(true);
      //requestContext.se
      servletRequest.setAttribute(ContainerConstants.HST_REQUEST_CONTEXT, requestContext);
    }

    // initialize the callContext and place it in the servlet request
    CallContext callContext = createContext(servletRequest,servletResponse);
    servletRequest.setAttribute(CmisHelper.CALL_CONTEXT_PARAM, callContext);

    // initialize the dispatcher and place it in the servletContext
    Dispatcher dispatcher = getDispatcher(servletResponse);
    context.getRequestContainerConfig().getServletContext().setAttribute("dispatcher", dispatcher);

    // get the CMIS Service and place it in the servlet request
    CmisService cmisService = getCmisService(servletRequest, servletResponse, callContext);
    servletRequest.setAttribute(CmisHelper.CMIS_SERVICE_PARAM, cmisService);

    // continue
    context.invokeNext();
  }

  private CallContext createContext(HttpServletRequest request, HttpServletResponse response) throws ContainerException {
    String[] pathFragments = CmisHelper.splitPath(request);

    String repositoryId = null;
    if (pathFragments.length > 0) {
      repositoryId = pathFragments[0];
    }

    CallContextImpl context = new CallContextImpl(CallContext.BINDING_ATOMPUB, repositoryId, true);


    CallContextHandler callContextHandler = null;

    try {
        callContextHandler = (CallContextHandler) Class.forName(CALL_CONTEXT_HANDLER_CLASS).newInstance();
      } catch (Exception e) {
        CmisHelper.sendContainerError(e, response);
      }

    // call call context handler
    if (callContextHandler != null) {
      Map<String, String> callContextMap = callContextHandler.getCallContextMap(request);
      if (callContextMap != null) {
        for (Map.Entry<String, String> e : callContextMap.entrySet()) {
          context.put(e.getKey(), e.getValue());
        }
      }
    }

    // decode range
    String rangeHeader = request.getHeader("Range");
    if (rangeHeader != null) {
      rangeHeader = rangeHeader.trim();
      BigInteger offset = null;
      BigInteger length = null;

      int eq = rangeHeader.indexOf('=');
      int ds = rangeHeader.indexOf('-');
      if ((eq > 0) && (ds > eq)) {
        String offsetStr = rangeHeader.substring(eq + 1, ds).trim();
        if (offsetStr.length() > 0) {
          offset = new BigInteger(offsetStr);
        }

        if (ds < rangeHeader.length()) {
          String lengthStr = rangeHeader.substring(ds + 1).trim();
          if (lengthStr.length() > 0) {
            if (offset == null) {
              length = new BigInteger(lengthStr);
            } else {
              length = (new BigInteger(lengthStr)).subtract(offset);
            }
          }

          if (offset != null) {
            context.put(CallContext.OFFSET, offset.toString());
          }
          if (length != null) {
            context.put(CallContext.LENGTH, length.toString());
          }
        }
      }
    }
    return context;
  }

  private CmisService getCmisService(HttpServletRequest servletRequest, HttpServletResponse servletResponse, CallContext callContext) {

    // get services factory
    HstCmisServiceFactory cmisFactory = (HstCmisServiceFactory) servletRequest.getSession().getServletContext().getAttribute(
        CmisRepositoryContextListener.SERVICES_FACTORY);

    return cmisFactory.getService(callContext, servletRequest, servletResponse);
  }

  private Dispatcher getDispatcher(HttpServletResponse servletResponse) throws ContainerException {

    Dispatcher dispatcher = new Dispatcher();

    try {
      dispatcher.addResource(AtomPubUtils.RESOURCE_TYPES, METHOD_GET, RepositoryService.class, "getTypeChildren");
      dispatcher.addResource(AtomPubUtils.RESOURCE_TYPESDESC, METHOD_GET, RepositoryService.class, "getTypeDescendants");
      dispatcher.addResource(AtomPubUtils.RESOURCE_TYPE, METHOD_GET, RepositoryService.class, "getTypeDefinition");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CHILDREN, METHOD_GET, NavigationService.class, "getChildren");
      dispatcher.addResource(AtomPubUtils.RESOURCE_DESCENDANTS, METHOD_GET, NavigationService.class, "getDescendants");
      dispatcher.addResource(AtomPubUtils.RESOURCE_FOLDERTREE, METHOD_GET, NavigationService.class, "getFolderTree");
      dispatcher.addResource(AtomPubUtils.RESOURCE_PARENTS, METHOD_GET, NavigationService.class, "getObjectParents");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CHECKEDOUT, METHOD_GET, NavigationService.class, "getCheckedOutDocs");
      dispatcher.addResource(AtomPubUtils.RESOURCE_ENTRY, METHOD_GET, ObjectService.class, "getObject");
      dispatcher.addResource(AtomPubUtils.RESOURCE_OBJECTBYID, METHOD_GET, ObjectService.class, "getObject");
      dispatcher.addResource(AtomPubUtils.RESOURCE_OBJECTBYPATH, METHOD_GET, ObjectService.class, "getObjectByPath");
      dispatcher.addResource(AtomPubUtils.RESOURCE_ALLOWABLEACIONS, METHOD_GET, ObjectService.class, "getAllowableActions");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CONTENT, METHOD_GET, ObjectService.class, "getContentStream");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CONTENT, METHOD_PUT, ObjectService.class, "setContentStream");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CONTENT, METHOD_DELETE, ObjectService.class, "deleteContentStream");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CHILDREN, METHOD_POST, ObjectService.class, "create");
      dispatcher.addResource(AtomPubUtils.RESOURCE_RELATIONSHIPS, METHOD_POST, ObjectService.class, "createRelationship");
      dispatcher.addResource(AtomPubUtils.RESOURCE_ENTRY, METHOD_PUT, ObjectService.class, "updateProperties");
      dispatcher.addResource(AtomPubUtils.RESOURCE_ENTRY, METHOD_DELETE, ObjectService.class, "deleteObject");
      dispatcher.addResource(AtomPubUtils.RESOURCE_DESCENDANTS, METHOD_DELETE, ObjectService.class, "deleteTree");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CHECKEDOUT, METHOD_POST, VersioningService.class, "checkOut");
      dispatcher.addResource(AtomPubUtils.RESOURCE_VERSIONS, METHOD_GET, VersioningService.class, "getAllVersions");
      dispatcher.addResource(AtomPubUtils.RESOURCE_VERSIONS, METHOD_DELETE, VersioningService.class, "deleteAllVersions");
      dispatcher.addResource(AtomPubUtils.RESOURCE_QUERY, METHOD_GET, DiscoveryService.class, "query");
      dispatcher.addResource(AtomPubUtils.RESOURCE_QUERY, METHOD_POST, DiscoveryService.class, "query");
      dispatcher.addResource(AtomPubUtils.RESOURCE_CHANGES, METHOD_GET, DiscoveryService.class, "getContentChanges");
      dispatcher.addResource(AtomPubUtils.RESOURCE_RELATIONSHIPS, METHOD_GET, RelationshipService.class, "getObjectRelationships");
      dispatcher.addResource(AtomPubUtils.RESOURCE_UNFILED, METHOD_POST, MultiFilingService.class, "removeObjectFromFolder");
      dispatcher.addResource(AtomPubUtils.RESOURCE_ACL, METHOD_GET, AclService.class, "getAcl");
      dispatcher.addResource(AtomPubUtils.RESOURCE_ACL, METHOD_PUT, AclService.class, "applyAcl");
      dispatcher.addResource(AtomPubUtils.RESOURCE_POLICIES, METHOD_GET, PolicyService.class, "getAppliedPolicies");
      dispatcher.addResource(AtomPubUtils.RESOURCE_POLICIES, METHOD_POST, PolicyService.class, "applyPolicy");
      dispatcher.addResource(AtomPubUtils.RESOURCE_POLICIES, METHOD_DELETE, PolicyService.class, "removePolicy");
    } catch (NoSuchMethodException e) {
      CmisHelper.sendContainerError(e, servletResponse);
    }
    return dispatcher;
  }

}
