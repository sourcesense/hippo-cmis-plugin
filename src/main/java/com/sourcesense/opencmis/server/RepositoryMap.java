/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.sourcesense.opencmis.server;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.server.CallContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository map.
 *
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 */
public class RepositoryMap {

  private Map<String, HstCmisRepository> fMap;

  public RepositoryMap() {
    fMap = new HashMap<String, HstCmisRepository>();
  }

  public Collection<HstCmisRepository> getRepositories() {
    return fMap.values();
  }

  public void init(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    for(HstCmisRepository hcr : getRepositories()) {
      if ((hcr != null) && (hcr.getRepositoryId() != null)) {
        hcr.init(servletRequest, servletResponse);
      }
    }
  }

  /**
   * Adds a repository object.
   */
  public void addRepository(HstCmisRepository hcr) {
    if ((hcr == null) || (hcr.getRepositoryId() == null)) {
      return;
    }
    fMap.put(hcr.getRepositoryId(), hcr);
  }

  /**
   * Gets a repository object by id.
   */
  public HstCmisRepository getRepository(String repositoryId) {
    // get repository object
    HstCmisRepository result = fMap.get(repositoryId);
    if (result == null) {
      throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
    }

    return result;
  }

  /**
   * Takes user and password from the CallContext and checks them.
   */
  public void authenticate(CallContext context) {
    // check user and password first
    if (!authenticate(context.getUsername(), context.getPassword())) {
      throw new CmisPermissionDeniedException();
    }
  }

  /**
   * Authenticates a user against the configured logins.
   */
  private boolean authenticate(String username, String password) {
    //@TODO implement
    return true;
  }

}
