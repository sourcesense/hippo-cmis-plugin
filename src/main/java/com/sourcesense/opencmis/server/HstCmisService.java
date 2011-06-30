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

import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeDefinitionListImpl;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * FileShare service implementation.
 */
public class HstCmisService extends AbstractCmisService {

  private static final Logger log = LoggerFactory.getLogger(HstCmisService.class);

  private RepositoryMap repositoryMap;
  private CallContext context;

  /**
   * Constructor.
   */
  public HstCmisService(RepositoryMap repositoryMap) {
    this.repositoryMap = repositoryMap;
  }

  // --- context ---

  public void setCallContext(CallContext context) {
    this.context = context;
  }

  public CallContext getCallContext() {
    return context;
  }

  public HstCmisRepository getRepository() {
      return repositoryMap.getRepository(getCallContext().getRepositoryId());
  }

  // --- CMIS Services implementation ---

  public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extensionsData) {
    List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();
    for (HstCmisRepository hcr : repositoryMap.getRepositories()) {
        result.add(hcr.getRepositoryInfo(getCallContext()));
    }
    return result;
  }

  public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
    return getRepository().getTypeDefinition(getCallContext(), typeId);
  }

  //@TODO - implement
  public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
                                            BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
    return getRepository().getTypeChildren(typeId, includePropertyDefinitions, maxItems, skipCount);
  }

  /**
   * NavigationService implementation
   */

  //@TODO - implement
  public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
                                        Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
                                        Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
    ObjectInFolderList obj = new ObjectInFolderListImpl();
    return obj;
  }

  //@TODO - implement
  public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
                                                 Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
                                                 Boolean includeRelativePathSegment, ExtensionsData extension) {
    return new ArrayList<ObjectParentData>();
  }

  public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
                              IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
                              Boolean includeAcl, ExtensionsData extension) {

    if (objectId == null) {
      throw new CmisInvalidArgumentException("Uuid cannot be null!");
    }

    HippoBean hippoBean = getRepository().getObjectByUuid(objectId);
    if (hippoBean == null) {
      throw new CmisInvalidArgumentException(String.format("UUID '%1' cannot be resolved for repository ID '%2'", objectId, repositoryId));
    } else {
      ObjectDataImpl objectData = new ObjectDataImpl();
      ObjectInfoImpl objectInfo = new ObjectInfoImpl();
      objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));
      return objectData;
    }
  }

  @Override
  public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
                                    IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
                                    Boolean includeAcl, ExtensionsData extension) {

    if (path == null || path.isEmpty()) {
      throw new CmisInvalidArgumentException("Path cannot be null!");
    }

    HippoBean hippoBean = getRepository().getObjectByPath(path);

    if (hippoBean == null) {
      throw new CmisInvalidArgumentException(String.format("Path '%1' cannot be resolved for repository ID '%2'", path, repositoryId));
    } else {
      ObjectInfoImpl objectInfo = new ObjectInfoImpl();
      ObjectDataImpl objectData = new ObjectDataImpl();
      objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));
      return objectData;
    }
  }

  //@TODO - implement
  @Override
  public ObjectList query(String repositoryId, String statement, Boolean searchAllVersions,
                          Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
                          BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

    ObjectList objectList = new ObjectListImpl();
    return objectList;
  }
}
