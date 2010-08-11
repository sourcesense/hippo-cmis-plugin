package com.sourcesense.opencmis.server;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */


import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HstCmisServiceFactory extends AbstractServiceFactory {


  private static final Log log = LogFactory.getLog(HstCmisServiceFactory.class);

  private RepositoryMap repositoryMap;

  private ThreadLocal<HstCmisService> threadLocalService = new ThreadLocal<HstCmisService>();

  public HstCmisServiceFactory() {
  }

  @Override
  public void init(Map<String, String> parameters) {
    super.init(parameters);
    repositoryMap = new RepositoryMap();
    
    HstCmisRepository hcr = new HstCmisRepository("my-repo-id", "my-root-folder-id");
    repositoryMap.addRepository(hcr);
  }

  @Override
  public void destroy() {
    super.destroy();
    threadLocalService = null;
  }

  @Override
  public CmisService getService(CallContext context) {
    throw new CmisInvalidArgumentException("The getService() needs servlet Request and Response in order to instanciate HST components");
  }

  public CmisService getService(CallContext context, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    repositoryMap.init(servletRequest, servletResponse);
    repositoryMap.authenticate(context);
    HstCmisService service = threadLocalService.get();
    if (service == null) {
      service = new HstCmisService(repositoryMap);
      threadLocalService.set(service);
    }
    service.setCallContext(context);
    return service;
  }

}
