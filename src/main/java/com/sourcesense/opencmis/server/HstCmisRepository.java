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

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.*;
import org.apache.chemistry.opencmis.commons.exceptions.*;
import org.apache.chemistry.opencmis.commons.impl.Converter;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisObjectType;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisProperty;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectBeanPersistenceManager;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.jaxrs.AbstractJaxrsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.*;

//services.support.jaxrs.content.BaseHstContentService;

public class HstCmisRepository {

  private static final String HST_CMIS_VERSION_SUPPORTED = "1.0";
  private static final String HST_VENDOR_NAME = "Hippo HST Vendor Name";
  private static final String HST_PRODUCT_NAME = "Hippo HST Product Name";
  private static final String HST_PRODUCT_VERSION = "Hippo HST Product Version";

  private static final String ROOT_ID = "@root@";
  private static final String SHADOW_EXT = ".cmis.xml";
  private static final String SHADOW_FOLDER = "cmis.xml";

  private static final String USER_UNKNOWN = "<unknown>";

  private static final String CMIS_READ = "cmis:read";
  private static final String CMIS_WRITE = "cmis:write";
  private static final String CMIS_ALL = "cmis:all";

  private static final Log log = LogFactory.getLog(HstCmisRepository.class);

  /**
   * Repository id
   */
  private String fRepositoryId;
  /**
   * Root directory
   */
  private HippoBean fRoot;
  /**
   * Types
   */
  private TypeManager fTypes;
  /**
   * User table
   */
  private Map<String, Boolean> fUserMap;
  /**
   * Repository info
   */
  private RepositoryInfoImpl fRepositoryInfo;

  private HttpServletRequest servletRequest;
  private HttpServletResponse servletResponse;
  private ObjectBeanPersistenceManager contentPersistenceManager;

  public static final String HIPPO_MODIFICATION_DATE = "hippostdpubwf:lastModificationDate";
  public static final String HIPPO_CREATION_DATE = "hippostdpubwf:creationDate";


  public void init(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    this.servletRequest = servletRequest;
    this.servletResponse = servletResponse;
//    try {
//      this.contentPersistenceManager = getContentPersistenceManager(servletRequest);
//    } catch (RepositoryException e) {
//      sendCmisServiceError(e);
//    }
  }

  public HippoBean getObjectByUuid(String objectId) {
    HippoBean ret = null;
    try {
      ret = (HippoBean)this.contentPersistenceManager.getObjectByUuid(objectId);
    } catch (ObjectBeanManagerException e) {
      sendCmisServiceError(e);
    }
    return ret;
  }

  public HippoBean getObjectByPath(String path) {
    HippoBean ret = null;
    try {
      ret = (HippoBean)this.contentPersistenceManager.getObject(path);
    } catch (ObjectBeanManagerException e) {
      sendCmisServiceError(e);
    }
    return ret;
  }

  /**
   * Constructor.
   *
   * @param repId CMIS repository id
   * @param root  root folder
   */
  public HstCmisRepository(String repId, String root) {
    // check repository id
    if ((repId == null) || (repId.trim().length() == 0)) {
      throw new IllegalArgumentException("Invalid repository id!");
    }

    fRepositoryId = repId;

    // check root folder
    if ((root == null) || (root.trim().length() == 0)) {
      throw new IllegalArgumentException("Invalid root folder!");
    }

    // set up user table
    fUserMap = new HashMap<String, Boolean>();

    // compile repository info
    fRepositoryInfo = new RepositoryInfoImpl();

    fRepositoryInfo.setId(fRepositoryId);
    fRepositoryInfo.setName(fRepositoryId);
    fRepositoryInfo.setDescription(fRepositoryId);

    fRepositoryInfo.setCmisVersionSupported(HST_CMIS_VERSION_SUPPORTED);

    fRepositoryInfo.setProductName(HST_PRODUCT_NAME);
    fRepositoryInfo.setProductVersion(HST_PRODUCT_VERSION);
    fRepositoryInfo.setVendorName(HST_VENDOR_NAME);

    fRepositoryInfo.setRootFolder(ROOT_ID);

    fRepositoryInfo.setThinClientUri("");

    RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
    capabilities.setCapabilityAcl(CapabilityAcl.DISCOVER);
    capabilities.setAllVersionsSearchable(false);
    capabilities.setCapabilityJoin(CapabilityJoin.NONE);
    capabilities.setSupportsMultifiling(false);
    capabilities.setSupportsUnfiling(false);
    capabilities.setSupportsVersionSpecificFiling(false);
    capabilities.setIsPwcSearchable(false);
    capabilities.setIsPwcUpdatable(false);
    capabilities.setCapabilityQuery(CapabilityQuery.NONE);
    capabilities.setCapabilityChanges(CapabilityChanges.NONE);
    capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
    capabilities.setSupportsGetDescendants(true);
    capabilities.setSupportsGetFolderTree(true);
    capabilities.setCapabilityRendition(CapabilityRenditions.NONE);

    fRepositoryInfo.setCapabilities(capabilities);

    AclCapabilitiesDataImpl aclCapability = new AclCapabilitiesDataImpl();
    aclCapability.setSupportedPermissions(SupportedPermissions.BASIC);
    aclCapability.setAclPropagation(AclPropagation.OBJECTONLY);

    // @TODO permissions
    //List<PermissionDefinition> permissions = new ArrayList<PermissionDefinition>();
    //permissions.add(CmisHelper.createPermission(CMIS_READ, "Read"));
    //permissions.add(CmisHelper.createPermission(CMIS_WRITE, "Write"));
    //permissions.add(CmisHelper.createPermission(CMIS_ALL, "All"));
    //aclCapability.setPermissionDefinitionData(permissions);

    // mapping
    List<PermissionMapping> list = new ArrayList<PermissionMapping>();
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, CMIS_WRITE));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_DELETE_OBJECT, CMIS_ALL));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, CMIS_ALL));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_MOVE_OBJECT, CMIS_WRITE));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_MOVE_SOURCE, CMIS_READ));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_MOVE_TARGET, CMIS_WRITE));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, CMIS_WRITE));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, CMIS_WRITE));
    list.add(CmisHelper.createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, CMIS_READ));
    Map<String, PermissionMapping> map = new LinkedHashMap<String, PermissionMapping>();
    for (PermissionMapping pm : list) {
      map.put(pm.getKey(), pm);
    }
    aclCapability.setPermissionMappingData(map);

    fRepositoryInfo.setAclCapabilities(aclCapability);
  }

  /**
   * Returns the repository id.
   */
  public String getRepositoryId() {
    return fRepositoryId;
  }

  /**
   * CMIS getRepositoryInfo.
   */
  public RepositoryInfo getRepositoryInfo(CallContext context) {
    debug("getRepositoryInfo");
    checkUser(context, false);

    return fRepositoryInfo;
  }

  public TypeDefinitionList getTypesChildren(CallContext context, String typeId, boolean includePropertyDefinitions,
                                             BigInteger maxItems, BigInteger skipCount) {
    debug("getTypesChildren");
    checkUser(context, false);

    return fTypes.getTypesChildren(context, typeId, includePropertyDefinitions, maxItems, skipCount);
  }

  public TypeDefinition getTypeDefinition(CallContext context, String typeId) {
    debug("getTypeDefinition");
    checkUser(context, false);

    return fTypes.getTypeDefinition(context, typeId);
  }

  public List<TypeDefinitionContainer> getTypesDescendants(CallContext context, String typeId, BigInteger depth,
                                                           Boolean includePropertyDefinitions) {
    debug("getTypesDescendants");
    checkUser(context, false);

    return fTypes.getTypesDescendants(context, typeId, depth, includePropertyDefinitions);
  }

  public AllowableActions getAllowableActions(CallContext context, String objectId) {
    debug("getAllowableActions");
    boolean userReadOnly = checkUser(context, false);

    HippoBean bean = getObjectByUuid(objectId);

    if (bean == null) {
      throw new CmisObjectNotFoundException("Object not found!");
    }

    return compileAllowableActions(bean, userReadOnly);
  }

  /**
   * CMIS getContentStream.
   */
  public ContentStream getContentStream(CallContext context, String objectId, BigInteger offset, BigInteger length) {
    debug("getContentStream");
    checkUser(context, false);

    if ((offset != null) || (length != null)) {
      throw new CmisInvalidArgumentException("Offset and Length are not supported!");
    }

    HippoBean bean = getObjectByUuid(objectId);

    if (!bean.isHippoDocumentBean()) {
      throw new CmisStreamNotSupportedException("Not a Hippo Bean!");
    }

    try {
      File tempFile = File.createTempFile("cmis",".temp");
      FileOutputStream fos = new FileOutputStream(tempFile);
      String path = bean.getPath();

      contentPersistenceManager.getSession().exportDocumentView(path,fos, false, false);
      fos.flush();

      // compile data
      ContentStreamImpl result = new ContentStreamImpl();
      result.setFileName(bean.getName());
      result.setLength(BigInteger.valueOf(tempFile.length()));
      result.setMimeType(MIMETypes.getMIMEType("text/xml"));
      result.setStream(new FileInputStream(tempFile));

      return result;

    } catch (Exception e) {
      sendCmisServiceError(e);
      return null;
    }
  }

  /**
   * CMIS getChildren.
   */
  public ObjectInFolderList getChildren(CallContext context, String folderId, String filter,
                                        Boolean includeAllowableActions, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount,
                                        ObjectInfoHandler objectInfos) {
    debug("getChildren");
    boolean userReadOnly = checkUser(context, false);

    // split filter
    Set<String> filterCollection = splitFilter(filter);

    // set defaults if values not set
    boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
    boolean ips = (includePathSegment == null ? false : includePathSegment.booleanValue());

    // skip and max
    int skip = (skipCount == null ? 0 : skipCount.intValue());
    if (skip < 0) {
      skip = 0;
    }

    int max = (maxItems == null ? Integer.MAX_VALUE : maxItems.intValue());
    if (max < 0) {
      max = Integer.MAX_VALUE;
    }

    // get the folder
    HippoFolderBean folder = (HippoFolderBean)getObjectByUuid(folderId);

    // set object info of the the folder
    if (context.isObjectInfoRequired()) {
      compileObjectType(context, folder, null, false, false, userReadOnly, objectInfos);
    }

    // prepare result
    ObjectInFolderListImpl result = new ObjectInFolderListImpl();
    result.setObjects(new ArrayList<ObjectInFolderData>());
    result.setHasMoreItems(false);
    int count = 0;

    // iterate through children

    List children = folder.getFolders();
    children.addAll(folder.getDocuments());

    while(children.iterator().hasNext()) {
      HippoBean child = (HippoBean)children.iterator().next();

      count++;
      if (skip > 0) {
        skip--;
        continue;
      }

      if (result.getObjects().size() >= max) {
        result.setHasMoreItems(true);
        continue;
      }

      // build and add child object
      ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
      objectInFolder.setObject(compileObjectType(context, child, filterCollection, iaa, false, userReadOnly,
          objectInfos));
      if (ips) {
        objectInFolder.setPathSegment(child.getName());
      }

      result.getObjects().add(objectInFolder);
    }

    result.setNumItems(BigInteger.valueOf(count));

    return result;
  }

  /**
   * CMIS getDescendants.
   */
  public List<ObjectInFolderContainer> getDescendants(CallContext context, String folderId, BigInteger depth,
                                                      String filter, Boolean includeAllowableActions, Boolean includePathSegment, ObjectInfoHandler objectInfos,
                                                      boolean foldersOnly) {
    debug("getDescendants or getFolderTree");
    boolean userReadOnly = checkUser(context, false);

    // check depth
    int d = (depth == null ? 2 : depth.intValue());
    if (d == 0) {
      throw new CmisInvalidArgumentException("Depth must not be 0!");
    }
    if (d < -1) {
      d = -1;
    }

    // split filter
    Set<String> filterCollection = splitFilter(filter);

    // set defaults if values not set
    boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
    boolean ips = (includePathSegment == null ? false : includePathSegment.booleanValue());

    // get the folder
    // get the folder
    HippoFolderBean folder = (HippoFolderBean)getObjectByUuid(folderId);

    // set object info of the the folder
    if (context.isObjectInfoRequired()) {
      compileObjectType(context, folder, null, false, false, userReadOnly, objectInfos);
    }

    // get the tree
    List<ObjectInFolderContainer> result = new ArrayList<ObjectInFolderContainer>();
    gatherDescendants(context, folder, result, foldersOnly, d, filterCollection, iaa, ips, userReadOnly,
        objectInfos);

    return result;
  }

  /**
   * CMIS getFolderParent.
   */
  public ObjectData getFolderParent(CallContext context, String folderId, String filter, ObjectInfoHandler objectInfos) {
    List<ObjectParentData> parents = getObjectParents(context, folderId, filter, false, false, objectInfos);

    if (parents.size() == 0) {
      throw new CmisInvalidArgumentException("The root folder has no parent!");
    }

    return parents.get(0).getObject();
  }

  /**
   * CMIS getObjectParents.
   */
  public List<ObjectParentData> getObjectParents(CallContext context, String objectId, String filter,
                                                 Boolean includeAllowableActions, Boolean includeRelativePathSegment, ObjectInfoHandler objectInfos) {
    debug("getObjectParents");
    boolean userReadOnly = checkUser(context, false);

    // split filter
    Set<String> filterCollection = splitFilter(filter);

    // set defaults if values not set
    boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
    boolean irps = (includeRelativePathSegment == null ? false : includeRelativePathSegment.booleanValue());

    HippoBean bean = getObjectByUuid(objectId);

    // don't climb above the root folder
    if (isRootBean(bean)) {
      return Collections.emptyList();
    }

    // set object info of the the object
    if (context.isObjectInfoRequired()) {
      compileObjectType(context, bean, null, false, false, userReadOnly, objectInfos);
    }

    // get parent folder
    HippoBean parent = bean.getParentBean();
    ObjectData object = compileObjectType(context, parent, filterCollection, iaa, false, userReadOnly, objectInfos);

    ObjectParentDataImpl result = new ObjectParentDataImpl();
    result.setObject(object);
    if (irps) {
      result.setRelativePathSegment(bean.getName());
    }

    return Collections.singletonList((ObjectParentData) result);
  }

  public TypeDefinitionList getTypeChildren(String typeId, Boolean includePropertyDefinitions, BigInteger maxItems, BigInteger skipCount) {
    return new TypeDefinitionListImpl();
  }

  // --- helper methods ---

  /**
   * Gather the children of a folder.
   */
  private void gatherDescendants(CallContext context, HippoBean bean, List<ObjectInFolderContainer> list,
                                 boolean foldersOnly, int depth, Set<String> filter, boolean includeAllowableActions,
                                 boolean includePathSegments, boolean userReadOnly, ObjectInfoHandler objectInfos) {

    List<? extends HippoBean> children = new ArrayList<HippoBean>();

    if (bean instanceof HippoFolderBean) {
      HippoFolderBean folder = (HippoFolderBean)bean;
      if (foldersOnly) {
        children = folder.getFolders();
      } else {
        children = folder.getDocuments();
      }
    }

    // iterate through children
    for (HippoBean child : children) {

      // add to list
      ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
      objectInFolder.setObject(compileObjectType(context, child, filter, includeAllowableActions, false,
          userReadOnly, objectInfos));
      if (includePathSegments) {
        objectInFolder.setPathSegment(child.getName());
      }

      ObjectInFolderContainerImpl container = new ObjectInFolderContainerImpl();
      container.setObject(objectInFolder);

      list.add(container);

      // move to next level
      if (depth != 1) {
        container.setChildren(new ArrayList<ObjectInFolderContainer>());
        gatherDescendants(context, child, container.getChildren(), foldersOnly, depth - 1, filter,
            includeAllowableActions, includePathSegments, userReadOnly, objectInfos);
      }
    }
  }

  /**
   * Checks if a folder is empty. A folder is considered as empty if no files
   * or only the shadow file reside in the folder.
   *
   * @param folder
   *            the folder
   *
   * @return <code>true</code> if the folder is empty.
   */

  /**
   * Compiles an object type object from a file or folder.�
   */
  private ObjectData compileObjectType(CallContext context, HippoBean bean, Set<String> filter,
                                       boolean includeAllowableActions, boolean includeAcl, boolean userReadOnly, ObjectInfoHandler objectInfos) {
    ObjectDataImpl result = new ObjectDataImpl();
    ObjectInfoImpl objectInfo = new ObjectInfoImpl();

    result.setProperties(compileProperties(bean, filter, objectInfo));

    if (includeAllowableActions) {
      result.setAllowableActions(compileAllowableActions(bean, userReadOnly));
    }

    if (includeAcl) {
      result.setAcl(compileAcl(bean));
      result.setIsExactAcl(true);
    }

    if (context.isObjectInfoRequired()) {
      objectInfo.setObject(result);
      objectInfos.addObjectInfo(objectInfo);
    }

    return result;
  }

  /**
   * Gathers all base properties of a file or folder.
   */
  private Properties compileProperties(HippoBean bean, Set<String> orgfilter, ObjectInfoImpl objectInfo) {
    if (bean == null) {
      throw new IllegalArgumentException("File must not be null!");
    }

    // we can gather properties if the file or folder doesn't exist
    if (!bean.isHippoDocumentBean()) {
      throw new CmisObjectNotFoundException(String.format("Object %1 is not a document",bean));
    }

    // copy filter
    Set<String> filter = (orgfilter == null ? null : new HashSet<String>(orgfilter));

    // find base type
    String typeId = null;

    if (bean.isHippoFolderBean()) {
      typeId = TypeManager.FOLDER_TYPE_ID;
      objectInfo.setBaseType(BaseTypeId.CMIS_FOLDER);
      objectInfo.setTypeId(typeId);
      objectInfo.setContentType(null);
      objectInfo.setFileName(null);
      objectInfo.setHasAcl(true);
      objectInfo.setHasContent(false);
      objectInfo.setVersionSeriesId(null);
      objectInfo.setIsCurrentVersion(true);
      objectInfo.setRelationshipSourceIds(null);
      objectInfo.setRelationshipTargetIds(null);
      objectInfo.setRenditionInfos(null);
      objectInfo.setSupportsDescendants(true);
      objectInfo.setSupportsFolderTree(true);
      objectInfo.setSupportsPolicies(false);
      objectInfo.setSupportsRelationships(false);
      objectInfo.setWorkingCopyId(null);
      objectInfo.setWorkingCopyOriginalId(null);
    } else {
      typeId = TypeManager.DOCUMENT_TYPE_ID;
      objectInfo.setBaseType(BaseTypeId.CMIS_DOCUMENT);
      objectInfo.setTypeId(typeId);
      objectInfo.setHasAcl(true);
      objectInfo.setHasContent(true);
      objectInfo.setHasParent(true);
      objectInfo.setVersionSeriesId(null);
      objectInfo.setIsCurrentVersion(true);
      objectInfo.setRelationshipSourceIds(null);
      objectInfo.setRelationshipTargetIds(null);
      objectInfo.setRenditionInfos(null);
      objectInfo.setSupportsDescendants(false);
      objectInfo.setSupportsFolderTree(false);
      objectInfo.setSupportsPolicies(false);
      objectInfo.setSupportsRelationships(false);
      objectInfo.setWorkingCopyId(null);
      objectInfo.setWorkingCopyOriginalId(null);
    }

    // let's do it
    try {
      PropertiesImpl result = new PropertiesImpl();

      // id
      String id = getId(bean);
      addPropertyId(result, typeId, filter, PropertyIds.OBJECT_ID, id);
      objectInfo.setId(id);

      // name
      String name = bean.getName();
      addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
      objectInfo.setName(name);

      Map<String, Object> beanProperties = bean.getProperties();

      // created and modified by
      addPropertyString(result, typeId, filter, PropertyIds.CREATED_BY, USER_UNKNOWN);
      addPropertyString(result, typeId, filter, PropertyIds.LAST_MODIFIED_BY, USER_UNKNOWN);
      objectInfo.setCreatedBy(USER_UNKNOWN);

      // creation date
      GregorianCalendar creationDate = (GregorianCalendar)beanProperties.get(HIPPO_CREATION_DATE);
      addPropertyDateTime(result, typeId, filter, PropertyIds.CREATION_DATE, creationDate);
      objectInfo.setCreationDate(creationDate);

      // modification date
      GregorianCalendar lastModificationDate = (GregorianCalendar)beanProperties.get(HIPPO_MODIFICATION_DATE);
      addPropertyDateTime(result, typeId, filter, PropertyIds.LAST_MODIFICATION_DATE, lastModificationDate);
      objectInfo.setLastModificationDate(lastModificationDate);

      // directory or file
      if (bean.isHippoFolderBean()) {
        // base type and type name
        addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
        addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, TypeManager.FOLDER_TYPE_ID);
        String path = bean.getPath();
        addPropertyString(result, typeId, filter, PropertyIds.PATH, (path.length() == 0 ? "/" : path));

        // folder properties
        if (!isRootBean(bean)) {
          addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID,
              (isRootBean(bean.getParentBean()) ? ROOT_ID : bean.getParentBean().getCanonicalUUID()));
          objectInfo.setHasParent(true);
        } else {
          objectInfo.setHasParent(false);
        }
      } else {
        // base type and type name
        addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
        addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, TypeManager.DOCUMENT_TYPE_ID);

        // file properties
        addPropertyBoolean(result, typeId, filter, PropertyIds.IS_IMMUTABLE, false);
        addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_VERSION, true);
        addPropertyBoolean(result, typeId, filter, PropertyIds.IS_MAJOR_VERSION, true);
        addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_MAJOR_VERSION, true);
        addPropertyString(result, typeId, filter, PropertyIds.VERSION_LABEL, bean.getName());
        addPropertyId(result, typeId, filter, PropertyIds.VERSION_SERIES_ID, bean.getCanonicalUUID());
        addPropertyString(result, typeId, filter, PropertyIds.CHECKIN_COMMENT, "");
        addPropertyInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, 300);
        addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, "text/xml");
        addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, bean.getName());

        objectInfo.setContentType("text/xml");
        objectInfo.setFileName(bean.getName());
      }

      // read custom properties
      readCustomProperties(bean, result, filter, objectInfo);

      if (filter != null) {
        if (!filter.isEmpty()) {
          debug("Unknown filter properties: " + filter.toString(), null);
        }
      }

      return result;
    } catch (Exception e) {
      if (e instanceof CmisBaseException) {
        throw (CmisBaseException) e;
      }
      throw new CmisRuntimeException(e.getMessage());
    }
  }

  /**
   * Reads and adds properties.
   */
  @SuppressWarnings("unchecked")
  private void readCustomProperties(HippoBean bean, PropertiesImpl properties, Set<String> filter,
                                    ObjectInfoImpl objectInfo) {

    //@TODO : Fill it in
    CmisObjectType cmisObjectType = null;

    // add it to properties
    for (CmisProperty cmisProp : cmisObjectType.getProperties().getProperty()) {
      PropertyData<?> prop = Converter.convert(cmisProp);

      // overwrite object info
      if (prop instanceof PropertyString) {
        String firstValueStr = ((PropertyString) prop).getFirstValue();
        if (PropertyIds.NAME.equals(prop.getId())) {
          objectInfo.setName(firstValueStr);
        } else if (PropertyIds.OBJECT_TYPE_ID.equals(prop.getId())) {
          objectInfo.setTypeId(firstValueStr);
        } else if (PropertyIds.CREATED_BY.equals(prop.getId())) {
          objectInfo.setCreatedBy(firstValueStr);
        } else if (PropertyIds.CONTENT_STREAM_MIME_TYPE.equals(prop.getId())) {
          objectInfo.setContentType(firstValueStr);
        } else if (PropertyIds.CONTENT_STREAM_FILE_NAME.equals(prop.getId())) {
          objectInfo.setFileName(firstValueStr);
        }
      }

      if (prop instanceof PropertyDateTime) {
        GregorianCalendar firstValueCal = ((PropertyDateTime) prop).getFirstValue();
        if (PropertyIds.CREATION_DATE.equals(prop.getId())) {
          objectInfo.setCreationDate(firstValueCal);
        } else if (PropertyIds.LAST_MODIFICATION_DATE.equals(prop.getId())) {
          objectInfo.setLastModificationDate(firstValueCal);
        }
      }

      // check filter
      if (filter != null) {
        if (!filter.contains(prop.getId())) {
          continue;
        } else {
          filter.remove(prop.getId());
        }
      }

      // don't overwrite id
      if (PropertyIds.OBJECT_ID.equals(prop.getId())) {
        continue;
      }

      // don't overwrite base type
      if (PropertyIds.BASE_TYPE_ID.equals(prop.getId())) {
        continue;
      }

      // add it
      properties.addProperty(prop);
    }
  }

  /**
   * Checks and compiles a property set that can be written to disc.
   */
  private Properties compileProperties(String typeId, String creator, GregorianCalendar creationDate,
                                       String modifier, Properties properties) {
    PropertiesImpl result = new PropertiesImpl();
    Set<String> addedProps = new HashSet<String>();

    if ((properties == null) || (properties.getProperties() == null)) {
      throw new CmisConstraintException("No properties!");
    }

    // get the property definitions
    TypeDefinition type = fTypes.getType(typeId);
    if (type == null) {
      throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
    }

    // check if all required properties are there
    for (PropertyData<?> prop : properties.getProperties().values()) {
      PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

      // do we know that property?
      if (propType == null) {
        throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
      }

      // skip type id
      if (propType.getId().equals(PropertyIds.OBJECT_TYPE_ID)) {
        continue;
      }

      // can it be set?
      if ((propType.getUpdatability() == Updatability.READONLY)) {
        throw new CmisConstraintException("Property '" + prop.getId() + "' is readonly!");
      }

      // empty properties are invalid
      if (CmisHelper.isEmptyProperty(prop)) {
        throw new CmisConstraintException("Property '" + prop.getId() + "' must not be empty!");
      }

      // add it
      result.addProperty(prop);
      addedProps.add(prop.getId());
    }

    // check if required properties are missing
    for (PropertyDefinition<?> propDef : type.getPropertyDefinitions().values()) {
      if (!addedProps.contains(propDef.getId()) && (propDef.getUpdatability() != Updatability.READONLY)) {
        if (!CmisHelper.addPropertyDefault(result, propDef) && propDef.isRequired()) {
          throw new CmisConstraintException("Property '" + propDef.getId() + "' is required!");
        }
      }
    }

    addPropertyId(result, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
    addPropertyString(result, typeId, null, PropertyIds.CREATED_BY, creator);
    addPropertyDateTime(result, typeId, null, PropertyIds.CREATION_DATE, creationDate);
    addPropertyString(result, typeId, null, PropertyIds.LAST_MODIFIED_BY, modifier);

    return result;
  }

  private void addPropertyId(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
    if (!checkAddProperty(props, typeId, filter, id)) {
      return;
    }

    if (value == null) {
      throw new IllegalArgumentException("Value must not be null!");
    }

    props.addProperty(new PropertyIdImpl(id, value));
  }

  private void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
    if (!checkAddProperty(props, typeId, filter, id)) {
      return;
    }

    props.addProperty(new PropertyStringImpl(id, value));
  }

  private void addPropertyInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, long value) {
    if (!checkAddProperty(props, typeId, filter, id)) {
      return;
    }

    props.addProperty(new PropertyIntegerImpl(id, BigInteger.valueOf(value)));
  }

  private void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value) {
    if (!checkAddProperty(props, typeId, filter, id)) {
      return;
    }

    props.addProperty(new PropertyBooleanImpl(id, value));
  }

  private void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id,
                                   GregorianCalendar value) {
    if (!checkAddProperty(props, typeId, filter, id)) {
      return;
    }

    props.addProperty(new PropertyDateTimeImpl(id, value));
  }

  private boolean checkAddProperty(Properties properties, String typeId, Set<String> filter, String id) {
    if ((properties == null) || (properties.getProperties() == null)) {
      throw new IllegalArgumentException("Properties must not be null!");
    }

    if (id == null) {
      throw new IllegalArgumentException("Id must not be null!");
    }

    TypeDefinition type = fTypes.getType(typeId);
    if (type == null) {
      throw new IllegalArgumentException("Unknown type: " + typeId);
    }
    if (!type.getPropertyDefinitions().containsKey(id)) {
      throw new IllegalArgumentException("Unknown property: " + id);
    }

    String queryName = type.getPropertyDefinitions().get(id).getQueryName();

    if ((queryName != null) && (filter != null)) {
      if (!filter.contains(queryName)) {
        return false;
      } else {
        filter.remove(queryName);
      }
    }

    return true;
  }

  /**
   * Adds the default value of property if defined.
   */
  @SuppressWarnings("unchecked")

  /**
   * Compiles the allowable actions for a file or folder.
   */
  private AllowableActions compileAllowableActions(HippoBean bean, boolean userReadOnly) {
    if (bean == null) {
      throw new IllegalArgumentException("HippoBean must not be null!");
    }

    //@TODO - implement
    boolean isReadOnly = false;
    boolean isFolder = bean.isHippoFolderBean();
    boolean isRoot = isRootBean(bean);

    Set<Action> aas = new HashSet<Action>();

    addAction(aas, Action.CAN_GET_OBJECT_PARENTS, !isRoot);
    addAction(aas, Action.CAN_GET_PROPERTIES, true);
    addAction(aas, Action.CAN_UPDATE_PROPERTIES, !userReadOnly && !isReadOnly);
    addAction(aas, Action.CAN_MOVE_OBJECT, !userReadOnly);
    addAction(aas, Action.CAN_DELETE_OBJECT, !userReadOnly && !isReadOnly);
    addAction(aas, Action.CAN_GET_ACL, true);

    if (isFolder) {
      addAction(aas, Action.CAN_GET_DESCENDANTS, true);
      addAction(aas, Action.CAN_GET_CHILDREN, true);
      addAction(aas, Action.CAN_GET_FOLDER_PARENT, !isRoot);
      addAction(aas, Action.CAN_GET_FOLDER_TREE, true);
      addAction(aas, Action.CAN_CREATE_DOCUMENT, !userReadOnly);
      addAction(aas, Action.CAN_CREATE_FOLDER, !userReadOnly);
      addAction(aas, Action.CAN_DELETE_TREE, !userReadOnly && !isReadOnly);
    } else {
      addAction(aas, Action.CAN_GET_CONTENT_STREAM, true);
      addAction(aas, Action.CAN_SET_CONTENT_STREAM, !userReadOnly && !isReadOnly);
      addAction(aas, Action.CAN_DELETE_CONTENT_STREAM, !userReadOnly && !isReadOnly);
      addAction(aas, Action.CAN_GET_ALL_VERSIONS, true);
    }

    AllowableActionsImpl result = new AllowableActionsImpl();
    result.setAllowableActions(aas);

    return result;
  }

  //@TODO - implement
  private boolean isRootBean(HippoBean bean) {
    return false;
  }

  private void addAction(Set<Action> aas, Action action, boolean condition) {
    if (condition) {
      aas.add(action);
    }
  }

  /**
   * Compiles the ACL for a file or folder.
   */
  private Acl compileAcl(HippoBean bean) {
    AccessControlListImpl result = new AccessControlListImpl();
    result.setAces(new ArrayList<Ace>());

    for (Map.Entry<String, Boolean> ue : fUserMap.entrySet()) {
      // create principal
      AccessControlPrincipalDataImpl principal = new AccessControlPrincipalDataImpl();
      principal.setPrincipalId(ue.getKey());

      // create ACE
      AccessControlEntryImpl entry = new AccessControlEntryImpl();
      entry.setPrincipal(principal);
      entry.setPermissions(new ArrayList<String>());
      entry.getPermissions().add(CMIS_READ);
      if (!ue.getValue().booleanValue()) {
        entry.getPermissions().add(CMIS_WRITE);
        entry.getPermissions().add(CMIS_ALL);
      }

      entry.setDirect(true);

      // add ACE
      result.getAces().add(entry);
    }

    return result;
  }

  /**
   * Writes the properties for a document or folder.
   */
  private void writePropertiesFile(File file, Properties properties) {
    // create object
    CmisObjectType object = new CmisObjectType();
    object.setProperties(Converter.convert(properties));

    //@TODO : write it
  }

  // --- internal stuff ---


  /**
   * Splits a filter statement into a collection of properties. If
   * <code>filter</code> is <code>null</code>, empty or one of the properties
   * is '*' , an empty collection will be returned.
   */
  private Set<String> splitFilter(String filter) {
    if (filter == null) {
      return null;
    }

    if (filter.trim().length() == 0) {
      return null;
    }

    Set<String> result = new HashSet<String>();
    for (String s : filter.split(",")) {
      s = s.trim();
      if (s.equals("*")) {
        return null;
      } else if (s.length() > 0) {
        result.add(s);
      }
    }

    // set a few base properties
    // query name == id (for base type properties)
    result.add(PropertyIds.OBJECT_ID);
    result.add(PropertyIds.OBJECT_TYPE_ID);
    result.add(PropertyIds.BASE_TYPE_ID);

    return result;
  }

  /**
   * Returns the first value of an id property.
   */
  private String getIdProperty(Properties properties, String name) {
    PropertyData<?> property = properties.getProperties().get(name);
    if (!(property instanceof PropertyId)) {
      return null;
    }

    return ((PropertyId) property).getFirstValue();
  }

  /**
   * Returns the first value of an string property.
   */
  private String getStringProperty(Properties properties, String name) {
    PropertyData<?> property = properties.getProperties().get(name);
    if (!(property instanceof PropertyString)) {
      return null;
    }

    return ((PropertyString) property).getFirstValue();
  }

  /**
   * Returns the first value of an datetime property.
   */
  private GregorianCalendar getDateTimeProperty(Properties properties, String name) {
    PropertyData<?> property = properties.getProperties().get(name);
    if (!(property instanceof PropertyDateTime)) {
      return null;
    }

    return ((PropertyDateTime) property).getFirstValue();
  }

  /**
   * Checks if the user in the given context is valid for this repository and
   * if the user has the required permissions.
   */
  private boolean checkUser(CallContext context, boolean writeRequired) {

    //@TODO - reimplement
    return true;

/*
    if (context == null) {
      throw new CmisPermissionDeniedException("No user context!");
    }

    Boolean readOnly = fUserMap.get(context.getUsername());
    if (readOnly == null) {
      throw new CmisPermissionDeniedException("Unknown user!");
    }

    if (readOnly.booleanValue() && writeRequired) {
      throw new CmisPermissionDeniedException("No write permission!");
    }

    return readOnly.booleanValue();
*/
  }


  /**
   * Returns the id of a File object or throws an appropriate exception.
   */
  private String getId(HippoBean bean) {
    if (bean == null) {
      throw new IllegalArgumentException(String.format("Bean %1 is null", bean));
    } else if (isRootBean(bean)) {
      return ROOT_ID;
    }

    try {
      String id = bean.getCanonicalUUID();
      return new String(Base64.encodeBase64(id.getBytes("UTF-8")), "ISO-8859-1");
    } catch (Exception e) {
      sendCmisServiceError(e);
      return null;
    }
  }

  private String getRepositoryPath(HippoBean bean) {
    return bean.getCanonicalUUID();
  }

  public void sendCmisServiceError(Exception exc) throws WebApplicationException {
    CmisHelper.sendCmisServiceError(exc,this.servletResponse);
  }

  private void warn(String msg, Throwable t) {
    log.warn("<" + fRepositoryId + "> " + msg, t);
  }

  private void debug(String msg) {
    debug(msg, null);
  }

  private void debug(String msg, Throwable t) {
    log.debug("<" + fRepositoryId + "> " + msg, t);
  }
}
