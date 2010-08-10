package com.sourcesense.opencmis.server;

import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
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

public class MyCmisServiceImpl extends BaseHstContentService implements CmisService {

  private static final Logger log = LoggerFactory.getLogger(MyCmisServiceImpl.class);

  private static final String MY_REPOSITORY_ID = "my-repo-id";
  private static final String MY_ROOT_FOLDER_ID = "my-root-folder-id";
  private static final String MY_REPOSITORY_NAME = "My CMIS Repository";
  private static final String MY_REPOSITORY_DESCRIPTION = "My CMIS Description";
  private static final String MY_CMIS_VERSION_SUPPORTED = "1.0";
  private static final String MY_PRINCIPAL_ANONYMOUS = "anonymous";
  private static final String MY_PRINCIPAL_ANYONE = "anyone";
  private static final String MY_VENDOR_NAME = "My Vendor Name";
  private static final String MY_PRODUCT_NAME = "My Product Name";
  private static final String MY_PRODUCT_VERSION = "My Product Version";

  private HttpServletRequest serviceRequest;

  private HttpServletResponse serviceResponse;

  public MyCmisServiceImpl() {
    super();
  }

  public MyCmisServiceImpl(HttpServletRequest request, HttpServletResponse response) {
    super();
    serviceRequest = request;
    serviceResponse = response;
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

    if (objectId == null || objectId.isEmpty()) {
      throw new CmisInvalidArgumentException("Uuid cannot be null!");
    }

    try {
      ObjectBeanPersistenceManager cpm = getContentPersistenceManager(serviceRequest);
      HippoBean hippoBean = (HippoBean) cpm.getObjectByUuid(objectId);
      if (hippoBean == null) {
        throw new CmisInvalidArgumentException(String.format("Object with the given uuid '%1' cannot be resolved for repository ID '%2'", objectId, repositoryId));
      } else {
        ObjectInfoImpl objectInfo = new ObjectInfoImpl();
        ObjectDataImpl objectData = new ObjectDataImpl();
        objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));
        return objectData;
      }
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
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
      ObjectBeanPersistenceManager cpm = getContentPersistenceManager(serviceRequest);
      HippoBean hippoBean = (HippoBean) cpm.getObject(path);
      if (hippoBean == null) {
        throw new CmisInvalidArgumentException(String.format("Path '%1' cannot be resolved for repository ID '%2'", path, repositoryId));
      } else {
        ObjectInfoImpl objectInfo = new ObjectInfoImpl();
        ObjectDataImpl objectData = new ObjectDataImpl();
        objectData.setProperties(CmisHelper.compileHippoProperties(hippoBean, objectInfo));
        return objectData;
      }
    } catch (Exception e) {
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
          ObjectBeanPersistenceManager cpm = getContentPersistenceManager(serviceRequest);
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

}