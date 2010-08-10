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


import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectBeanPersistenceManager;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.services.support.jaxrs.content.BaseHstContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * FileShare service implementation.
 */
public class HstCmisService extends BaseHstContentService implements CmisService, ObjectInfoHandler {

  private static final Logger log = LoggerFactory.getLogger(HstCmisService.class);

  private static final String MY_REPOSITORY_ID = "hst-cmis-repository-id";
  private static final String MY_ROOT_FOLDER_ID = "hst-cmis-root-folder-id";
  private static final String MY_REPOSITORY_NAME = "Hippo HST CMIS Repository";
  private static final String MY_REPOSITORY_DESCRIPTION = "Hippo HST CMIS Description";
  private static final String MY_CMIS_VERSION_SUPPORTED = "1.0";
  private static final String MY_PRINCIPAL_ANONYMOUS = "anonymous";
  private static final String MY_PRINCIPAL_ANYONE = "anyone";
  private static final String MY_VENDOR_NAME = "Hippo HST Vendor Name";
  private static final String MY_PRODUCT_NAME = "Hippo HST Product Name";
  private static final String MY_PRODUCT_VERSION = "Hippo HST Product Version";
    
  private RepositoryMap repositoryMap;
  private CallContext context;
  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;

  /**
   * Constructor.
   */
  public HstCmisService(RepositoryMap repositoryMap, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    this.repositoryMap = repositoryMap;
    this.servletRequest = servletRequest;
    this.servletResponse = servletResponse;
  }

  // --- context ---

  public void setCallContext(CallContext context) {
    this.context = context;
  }

  public CallContext getCallContext() {
    return context;
  }

  /**
   * CmisService implementation
   */

  public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
            VersioningState versioningState, List<String> policies, ExtensionsData extension) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
            ExtensionsData extension) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public ObjectInfo getObjectInfo(String repositoryId, String objectId) {
    ObjectInfo objectInfo = new ObjectInfoImpl();
    return objectInfo;
  }

  public void addObjectInfo(ObjectInfo objectInfo) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public Acl applyAcl(String repositoryId, String objectId, Acl aces, AclPropagation aclPropagation) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * RepositoryService implementation
   */

  public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extensionsData) {
    List<RepositoryInfo> ret = new ArrayList<RepositoryInfo>();
    ret.add(getRepositoryInfo(MY_REPOSITORY_ID, extensionsData));
    return ret;
  }

  public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extensionsData) {
    RepositoryInfoImpl repoInfo = new RepositoryInfoImpl();
    repoInfo.setRepositoryId(repositoryId);
    repoInfo.setRepositoryName(MY_REPOSITORY_NAME);
    repoInfo.setRepositoryDescription(MY_REPOSITORY_DESCRIPTION);
    repoInfo.setCmisVersionSupported(MY_CMIS_VERSION_SUPPORTED);
    repoInfo.setRepositoryCapabilities(null);
    repoInfo.setRootFolder(MY_ROOT_FOLDER_ID);
    repoInfo.setPrincipalAnonymous(MY_PRINCIPAL_ANONYMOUS);
    repoInfo.setPrincipalAnyone(MY_PRINCIPAL_ANYONE);
    repoInfo.setThinClientUri(null);
    repoInfo.setChangesIncomplete(Boolean.TRUE);
    repoInfo.setChangesOnType(null);
    repoInfo.setLatestChangeLogToken(null);
    repoInfo.setVendorName(MY_VENDOR_NAME);
    repoInfo.setProductName(MY_PRODUCT_NAME);
    repoInfo.setProductVersion(MY_PRODUCT_VERSION);

    // set capabilities
    RepositoryCapabilitiesImpl caps = new
        RepositoryCapabilitiesImpl();
    caps.setAllVersionsSearchable(false);
    caps.setCapabilityAcl(CapabilityAcl.NONE);
    caps.setCapabilityChanges(CapabilityChanges.PROPERTIES);

    caps.setCapabilityContentStreamUpdates(
        CapabilityContentStreamUpdates.PWCONLY);
    caps.setCapabilityJoin(CapabilityJoin.NONE);
    caps.setCapabilityQuery(CapabilityQuery.METADATAONLY);
    caps.setCapabilityRendition(CapabilityRenditions.READ);
    caps.setIsPwcSearchable(false);
    caps.setIsPwcUpdatable(true);
    caps.setSupportsGetDescendants(true);
    caps.setSupportsGetFolderTree(true);
    caps.setSupportsMultifiling(true);
    caps.setSupportsUnfiling(true);
    caps.setSupportsVersionSpecificFiling(false);
    repoInfo.setRepositoryCapabilities(caps);

    AclCapabilitiesDataImpl aclCaps = new AclCapabilitiesDataImpl();
    aclCaps.setAclPropagation(AclPropagation.REPOSITORYDETERMINED);
    aclCaps.setPermissionDefinitionData(null);
    aclCaps.setPermissionMappingData(null);
    repoInfo.setAclCapabilities(aclCaps);
    repoInfo.setAclCapabilities(aclCaps);
    return repoInfo;
  }

  public TypeDefinitionList getTypeChildren(String s, String s1, Boolean aBoolean, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    return new TypeDefinitionListImpl();
  }

  public List<TypeDefinitionContainer> getTypeDescendants(String s, String s1, BigInteger bigInteger, Boolean aBoolean, ExtensionsData extensionsData) {
    return new ArrayList<TypeDefinitionContainer>();
  }

  public TypeDefinition getTypeDefinition(String s, String s1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * NavigationService implementation
   */

  public ObjectInFolderList getChildren(String s, String s1, String s2, String s3, Boolean aBoolean, IncludeRelationships includeRelationships, String s4, Boolean aBoolean1, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    ObjectInFolderList obj = new ObjectInFolderListImpl();
    return obj;
  }

  public List<ObjectInFolderContainer> getDescendants(String s, String s1, BigInteger bigInteger, String s2, Boolean aBoolean, IncludeRelationships includeRelationships, String s3, Boolean aBoolean1, ExtensionsData extensionsData) {
    return new ArrayList<ObjectInFolderContainer>();
  }

  public List<ObjectInFolderContainer> getFolderTree(String s, String s1, BigInteger bigInteger, String s2, Boolean aBoolean, IncludeRelationships includeRelationships, String s3, Boolean aBoolean1, ExtensionsData extensionsData) {
    return new ArrayList<ObjectInFolderContainer>();
  }

  public List<ObjectParentData> getObjectParents(String s, String s1, String s2, Boolean aBoolean, IncludeRelationships includeRelationships, String s3, Boolean aBoolean1, ExtensionsData extensionsData) {
    return new ArrayList<ObjectParentData>();
  }

  public ObjectData getFolderParent(String s, String s1, String s2, ExtensionsData extensionsData) {
    ObjectData objectData = new ObjectDataImpl();
    return objectData;
  }

  public ObjectList getCheckedOutDocs(String s, String s1, String s2, String s3, Boolean aBoolean, IncludeRelationships includeRelationships, String s4, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    ObjectList objectList = new ObjectListImpl();
    return objectList;
  }

  /**
   * ObjectService implementation
   */
  public String createDocument(String s, Properties properties, String s1, ContentStream contentStream, VersioningState versioningState, List<String> strings, Acl acl, Acl acl1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String createDocumentFromSource(String s, String s1, Properties properties, String s2, VersioningState versioningState, List<String> strings, Acl acl, Acl acl1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String createFolder(String s, Properties properties, String s1, List<String> strings, Acl acl, Acl acl1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String createRelationship(String s, Properties properties, List<String> strings, Acl acl, Acl acl1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String createPolicy(String s, Properties properties, String s1, List<String> strings, Acl acl, Acl acl1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public AllowableActions getAllowableActions(String s, String s1, ExtensionsData extensionsData) {
    AllowableActions actions = new AllowableActionsImpl();
    return actions;
  }

  public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
            IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
            Boolean includeAcl, ExtensionsData extension) {

    log.debug("getObject() - entering ...");

    ObjectDataImpl objectData = new ObjectDataImpl();
    ObjectInfoImpl objectInfo = new ObjectInfoImpl();

    if (objectId == null) {
      throw new CmisInvalidArgumentException("Uuid cannot be null!");
    }

    try {

      ObjectBeanPersistenceManager cpm = getContentPersistenceManager(servletRequest);

      HippoBean hippoBean = (HippoBean) cpm.getObjectByUuid(objectId);

      if (hippoBean != null) {

        objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    log.debug("getObject() - leaving ...");

    return objectData;
  }

  public Properties getProperties(String s, String s1, String s2, ExtensionsData extensionsData) {
    Properties props = new PropertiesImpl();
    return props;
  }

  public List<RenditionData> getRenditions(String s, String s1, String s2, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    return new ArrayList<RenditionData>();
  }

  public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
                                    IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
                                    Boolean includeAcl, ExtensionsData extension) {

    if (path == null || path.isEmpty()) {
      throw new CmisInvalidArgumentException("Path cannot be null!");
    }

    try {
      ObjectBeanPersistenceManager cpm = getContentPersistenceManager(servletRequest);
      HippoBean hippoBean = (HippoBean) cpm.getObject(path);
      if (hippoBean == null) {
        throw new CmisInvalidArgumentException(String.format("Path '%1' cannot be resolved for repository ID '%2'", path, repositoryId));
      } else {
        ObjectInfoImpl objectInfo = new ObjectInfoImpl();
        ObjectDataImpl objectData = new ObjectDataImpl();
        objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));
        return objectData;
      }
    } catch (LoginException e) {
      throw new WebApplicationException(e);
    } catch (ObjectBeanManagerException e) {
      throw new WebApplicationException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new WebApplicationException(e);
    }
  }

  public ContentStream getContentStream(String s, String s1, String s2, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    ContentStream contentStream = new ContentStreamImpl();
    return contentStream;
  }

  public void updateProperties(String s, Holder<String> stringHolder, Holder<String> stringHolder1, Properties properties, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void moveObject(String s, Holder<String> stringHolder, String s1, String s2, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void deleteObject(String s, String s1, Boolean aBoolean, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public FailedToDeleteData deleteTree(String s, String s1, Boolean aBoolean, UnfileObject unfileObject, Boolean aBoolean1, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setContentStream(String s, Holder<String> stringHolder, Boolean aBoolean, Holder<String> stringHolder1, ContentStream contentStream, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void deleteContentStream(String s, Holder<String> stringHolder, Holder<String> stringHolder1, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * VersioningService implementation
   */

  public void checkOut(String s, Holder<String> stringHolder, ExtensionsData extensionsData, Holder<Boolean> booleanHolder) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void cancelCheckOut(String s, String s1, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void checkIn(String s, Holder<String> stringHolder, Boolean aBoolean, Properties properties, ContentStream contentStream, String s1, List<String> strings, Acl acl, Acl acl1, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public ObjectData getObjectOfLatestVersion(String s, String s1, String s2, Boolean aBoolean, String s3, Boolean aBoolean1, IncludeRelationships includeRelationships, String s4, Boolean aBoolean2, Boolean aBoolean3, ExtensionsData extensionsData) {
    ObjectData objectData = new ObjectDataImpl();
    return objectData;
  }

  public Properties getPropertiesOfLatestVersion(String s, String s1, String s2, Boolean aBoolean, String s3, ExtensionsData extensionsData) {
    Properties props = new PropertiesImpl();
    return props;
  }

  public List<ObjectData> getAllVersions(String s, String s1, String s2, String s3, Boolean aBoolean, ExtensionsData extensionsData) {
    return new ArrayList<ObjectData>();  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * DiscoveryService implementation
   */

  public ObjectList query(String s, String s1, Boolean aBoolean, Boolean aBoolean1, IncludeRelationships includeRelationships, String s2, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    log.debug("query() - entering ...");
      try {
          ObjectList objectList = new ObjectListImpl();
          ObjectBeanPersistenceManager cpm = getContentPersistenceManager(servletRequest);
          ObjectDataImpl objectData = new ObjectDataImpl();
          ObjectInfoImpl objectInfo = new ObjectInfoImpl();
          HippoBean hippoBean = (HippoBean) cpm.getObject("/content/documents/demosite/common/homepage/homepage/");
          objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));
          objectList.getObjects().add(objectData);
          log.debug("query() - leaving ...");
          return objectList;

      } catch (LoginException e) {
        throw new WebApplicationException(e);
      } catch (ObjectBeanManagerException e) {
        throw new WebApplicationException(e);
      } catch (javax.jcr.RepositoryException e) {
        throw new WebApplicationException(e);
      }
  }

  public ObjectList getContentChanges(String s, Holder<String> stringHolder, Boolean aBoolean, String s1, Boolean aBoolean1, Boolean aBoolean2, BigInteger bigInteger, ExtensionsData extensionsData) {
    ObjectList objectList = new ObjectListImpl();
    return objectList;
  }

  /**
   * MultiFilingService implementation
   */

  public void addObjectToFolder(String s, String s1, String s2, Boolean aBoolean, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void removeObjectFromFolder(String s, String s1, String s2, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * RelationshipService implementation
   */

  public ObjectList getObjectRelationships(String s, String s1, Boolean aBoolean, RelationshipDirection relationshipDirection, String s2, String s3, Boolean aBoolean1, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
    ObjectList objectList = new ObjectListImpl();
    return objectList;
  }

  /**
   * AclService implementation
   */

  public Acl getAcl(String s, String s1, Boolean aBoolean, ExtensionsData extensionsData) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Acl applyAcl(String repositoryId, String objectId, Acl addAces, Acl removeAces, AclPropagation aclPropagation,
            ExtensionsData extension) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * PolicyService implementation
   */

  public void applyPolicy(String s, String s1, String s2, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void removePolicy(String s, String s1, String s2, ExtensionsData extensionsData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public List<ObjectData> getAppliedPolicies(String s, String s1, String s2, ExtensionsData extensionsData) {
    return new ArrayList<ObjectData>();
  }



//  public HstCmisRepository getRepository() {
//    return repositoryMap.getRepository(getCallContext().getRepositoryId());
//  }
//
//  // --- repository service ---
//
//  public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
//    for (HstCmisRepository hcr : repositoryMap.getRepositories()) {
//      if (hcr.getRepositoryId().equals(repositoryId)) {
//        return hcr.getRepositoryInfo(getCallContext());
//      }
//    }
//    throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
//  }
//
//  public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
//    List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();
//
//    for (HstCmisRepository hcr : repositoryMap.getRepositories()) {
//      result.add(hcr.getRepositoryInfo(getCallContext()));
//    }
//    return result;
//  }
//
//  public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
//                                            BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
//    return getRepository().getTypesChildren(getCallContext(), typeId, includePropertyDefinitions, maxItems,
//        skipCount);
//  }
//
//  public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
//    return getRepository().getTypeDefinition(getCallContext(), typeId);
//  }
//
//  public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
//                                                          Boolean includePropertyDefinitions, ExtensionsData extension) {
//    return getRepository().getTypesDescendants(getCallContext(), typeId, depth, includePropertyDefinitions);
//  }
//
//  // --- navigation service ---
//
//  public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
//                                        Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
//                                        Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
//    return getRepository().getChildren(getCallContext(), folderId, filter, includeAllowableActions,
//        includePathSegment, maxItems, skipCount, this);
//  }
//
//  public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth,
//                                                      String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
//                                                      String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
//    return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions,
//        includePathSegment, this, false);
//  }
//
//  public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension) {
//    return getRepository().getFolderParent(getCallContext(), folderId, filter, this);
//  }
//
//  public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth,
//                                                     String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
//                                                     String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
//    return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions,
//        includePathSegment, this, true);
//  }
//
//  public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
//                                                 Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
//                                                 Boolean includeRelativePathSegment, ExtensionsData extension) {
//    return getRepository().getObjectParents(getCallContext(), objectId, filter, includeAllowableActions,
//        includeRelativePathSegment, this);
//  }
//
//  public ObjectList getCheckedOutDocs(String repositoryId, String folderId, String filter, String orderBy,
//                                      Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
//                                      BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
//    ObjectListImpl result = new ObjectListImpl();
//    result.setHasMoreItems(false);
//    result.setNumItems(BigInteger.ZERO);
//    List<ObjectData> emptyList = Collections.emptyList();
//    result.setObjects(emptyList);
//
//    return result;
//  }
//
//  public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {
//    return getRepository().getAllowableActions(getCallContext(), objectId);
//  }
//
//
//  public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
//                                        BigInteger length, ExtensionsData extension) {
//    return getRepository().getContentStream(getCallContext(), objectId, offset, length);
//  }
//
//
//  public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
//                              IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
//                              Boolean includeAcl, ExtensionsData extension) {
//    return getRepository().getObject(getCallContext(), objectId, null, filter, includeAllowableActions, includeAcl,
//        this);
//  }
//
//
//  public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
//                                    IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
//                                    Boolean includeAcl, ExtensionsData extension) {
//    return getRepository().getObjectByPath(getCallContext(), path, filter, includeAllowableActions, includeAcl,
//        this);
//  }
//
//
//  public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {
//    ObjectData object = getRepository().getObject(getCallContext(), objectId, null, filter, false, false, this);
//    return object.getProperties();
//  }
//
//  public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
//                                           BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
//    return Collections.emptyList();
//  }
//
//  // --- versioning service ---
//
//
//  public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
//                                         Boolean includeAllowableActions, ExtensionsData extension) {
//    ObjectData theVersion = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter,
//        includeAllowableActions, false, this);
//
//    return Collections.singletonList(theVersion);
//  }
//
//
//  public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
//                                             Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
//                                             String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {
//    return getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, includeAllowableActions,
//        includeAcl, this);
//  }
//
//  public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
//                                                 Boolean major, String filter, ExtensionsData extension) {
//    ObjectData object = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, false,
//        false, null);
//
//    return object.getProperties();
//  }
//
//  // --- ACL service ---
//
//  public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension) {
//    return getRepository().getAcl(getCallContext(), objectId);
//  }
}
