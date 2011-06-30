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
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.PropertyId;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.exceptions.*;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.container.ContainerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class CmisHelper {

/*
  private static final Logger log = LoggerFactory.getLogger(CmisHelper.class);
*/

  public static final String CALL_CONTEXT_PARAM = "callContext";

  public static final String CREATED_BY = "hippostdpubwf:createdBy";

/*
  public static final String PUBLICATION_DATE = "hippostdpubwf:publicationDate";

  public static final String HOLDER = "hippostd:holder";
*/

  public static final String MODIFICATION_DATE = "hippostdpubwf:lastModificationDate";

  public static final String CREATION_DATE = "hippostdpubwf:creationDate";

  public static final String UUID = "jcr:uuid";

/*
  public static final String IS_CHECKED_OUT = "jcr:isCheckedOut";

  public static final String STATE = "hippostd:state";
*/

  public static final String LAST_MODIFIED_BY = "hippostdpubwf:lastModifiedBy";

/*
  public static final String STATE_SUMMARY = "hippostd:stateSummary";
*/

  public static final String TITLE = "demosite:title";

  public static final String CMIS_SERVICE_PARAM = "cmisService";

  public static void addPropertyString(PropertiesImpl props, String typeId, String id, String value) {
    addPropertyString(props, typeId, null, id, value);
  }

  public static void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {

    assert (value != null);

    if (checkAddProperty(props, typeId, filter, id)) {
      props.addProperty(new PropertyIdImpl(id, value));
    }
  }

  public static void addPropertyDateTime(PropertiesImpl props, String typeId, String id, GregorianCalendar value) {
    addPropertyDateTime(props, typeId, null, id, value);
  }

  public static void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id, GregorianCalendar value) {
    if (checkAddProperty(props, typeId, filter, id)) {
      props.addProperty(new PropertyDateTimeImpl(id, value));
    }
  }

  public static void addPropertyBoolean(PropertiesImpl props, String typeId, String id, boolean value) {
    addPropertyBoolean(props, typeId, null, id, value);
  }

  public static void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value) {
    if (checkAddProperty(props, typeId, filter, id)) {
      props.addProperty(new PropertyBooleanImpl(id, value));
    }
  }

  public static boolean checkAddProperty(Properties properties, String typeId, String id) {
    return checkAddProperty(properties, typeId, null, id);
  }

  public static boolean checkAddProperty(Properties properties, String typeId, Set<String> filter, String id) {

    assert (properties != null);
    assert (properties.getProperties() != null);

    //TODO implement type definitions

//        TypeDefinition type = fTypes.getType(typeId);
//        if (type == null) {
//            throw new IllegalArgumentException("Unknown type: " + typeId);
//        }
//        if (!type.getPropertyDefinitions().containsKey(id)) {
//            throw new IllegalArgumentException("Unknown property: " + id);
//        }


    //TODO implement type filters and the rest

//        String queryName = type.getPropertyDefinitions().get(id).getQueryName();
//
//        if ((queryName != null) && (filter != null)) {
//            if (!filter.contains(queryName)) {
//                return false;
//            } else {
//                filter.remove(queryName);
//            }
//        }

    return true;
  }

/*  public static Set<String> splitFilter(String filter) {
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
  }*/

/*
  public static Properties compileProperties(String path, Set<String> orgfilter, ObjectInfoImpl objectInfo) {

    assert (path != null);

    // copy filter
    Set<String> filter = (orgfilter == null ? null : new HashSet<String>(orgfilter));

    // find base type
    String typeId = null;

    PropertiesImpl result = new PropertiesImpl();

    // id
    String id = "myId";
    addPropertyString(result, typeId, filter, PropertyIds.OBJECT_ID, id);
    objectInfo.setId(id);

    // name
    String name = "myName";
    addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
    objectInfo.setName(name);

    if (filter != null && !filter.isEmpty()) {
      log.warn("Unknown filter properties: " + filter.toString());
    }

    return result;
  }
*/

  @Deprecated
  public static Properties compileHippoProperties(HippoBean hippoBean, ObjectInfoImpl objectInfo) {

    assert (hippoBean != null);

    PropertiesImpl properties = new PropertiesImpl();
    Map<String, Object> beanProperties = hippoBean.getProperties();

    addPropertyString(properties, null, PropertyIds.OBJECT_ID, (String) beanProperties.get(UUID));
    objectInfo.setId((String) beanProperties.get(UUID));

    addPropertyString(properties, null, PropertyIds.NAME, (String) beanProperties.get(TITLE));
    objectInfo.setName((String) beanProperties.get(TITLE));

    addPropertyString(properties, null, PropertyIds.CREATED_BY, (String) beanProperties.get(CREATED_BY));
    objectInfo.setCreatedBy((String) beanProperties.get(CREATED_BY));

    addPropertyString(properties, null, PropertyIds.LAST_MODIFIED_BY, (String) beanProperties.get(LAST_MODIFIED_BY));
    // todo what setter has to be used here for the objectInfo?
    //objectInfo.set((String)beanProperties.get(LAST_MODIFIED_BY));

    addPropertyDateTime(properties, null, PropertyIds.CREATION_DATE, (GregorianCalendar) beanProperties.get(CREATION_DATE));
    objectInfo.setCreationDate((GregorianCalendar) beanProperties.get(CREATION_DATE));

    addPropertyDateTime(properties, null, PropertyIds.LAST_MODIFICATION_DATE, (GregorianCalendar) beanProperties.get(MODIFICATION_DATE));
    objectInfo.setCreationDate((GregorianCalendar) beanProperties.get(MODIFICATION_DATE));

    addPropertyString(properties, null, PropertyIds.BASE_TYPE_ID, "cmis:document");
    addPropertyString(properties, null, PropertyIds.OBJECT_TYPE_ID, "cmis:document");
    addPropertyBoolean(properties, null, PropertyIds.IS_IMMUTABLE, false);
    addPropertyBoolean(properties, null, PropertyIds.IS_LATEST_VERSION, true);
    addPropertyBoolean(properties, null, PropertyIds.IS_MAJOR_VERSION, true);
    addPropertyBoolean(properties, null, PropertyIds.IS_LATEST_MAJOR_VERSION, true);

    return properties;

//            addPropertyString(result, typeId, filter, PropertyIds.VERSION_LABEL, file.getName());
//            addPropertyId(result, typeId, filter, PropertyIds.VERSION_SERIES_ID, fileToId(file));
//            addPropertyString(result, typeId, filter, PropertyIds.CHECKIN_COMMENT, "");
//            addPropertyInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, file.length());
//            addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, MIMETypes
//                    .getMIMEType(file));
//            addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, file.getName());

  }

  public static String[] splitPath(HttpServletRequest request) {
    String p = request.getPathInfo();
    if (p == null) {
      return new String[0];
    }

    return p.substring(1).split("/");
  }

  public static void sendContainerError(Exception exc, HttpServletResponse servletResponse) throws ContainerException {
    try {
      if (exc instanceof CmisUnauthorizedException) {
        servletResponse.setHeader("WWW-Authenticate", "Basic realm=\"CMIS\"");
      } else if (exc instanceof CmisBaseException) {
        servletResponse.sendError(getErrorCode((CmisBaseException) exc), exc.getMessage());
      } else throw new ContainerException(exc);
    } catch (Exception e) {
      throw new ContainerException(e);
    }
  }

  public static void sendCmisServiceError(Exception exc, HttpServletResponse servletResponse) throws WebApplicationException {
    try {
      if (exc instanceof CmisUnauthorizedException) {
        servletResponse.setHeader("WWW-Authenticate", "Basic realm=\"CMIS\"");
      } else if (exc instanceof CmisBaseException) {
        servletResponse.sendError(getErrorCode((CmisBaseException) exc), exc.getMessage());
      } else throw new WebApplicationException(exc);
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }

  private static int getErrorCode(CmisBaseException ex) {
    if (ex instanceof CmisConstraintException) {
      return 409;
    } else if (ex instanceof CmisContentAlreadyExistsException) {
      return 409;
    } else if (ex instanceof CmisFilterNotValidException) {
      return 400;
    } else if (ex instanceof CmisInvalidArgumentException) {
      return 400;
    } else if (ex instanceof CmisUnauthorizedException) {
      return 401;
    } else if (ex instanceof CmisNameConstraintViolationException) {
      return 409;
    } else if (ex instanceof CmisNotSupportedException) {
      return 405;
    } else if (ex instanceof CmisObjectNotFoundException) {
      return 404;
    } else if (ex instanceof CmisPermissionDeniedException) {
      return 403;
    } else if (ex instanceof CmisStorageException) {
      return 500;
    } else if (ex instanceof CmisStreamNotSupportedException) {
      return 403;
    } else if (ex instanceof CmisUpdateConflictException) {
      return 409;
    } else if (ex instanceof CmisVersioningException) {
      return 409;
    }

    return 500;
  }

  /**
   * Converts milliseconds into a calendar object.
   */
  public static GregorianCalendar millisToCalendar(long millis) {
    GregorianCalendar result = new GregorianCalendar();
    result.setTimeZone(TimeZone.getTimeZone("GMT"));
    result.setTimeInMillis(millis);

    return result;
  }

  public static boolean isEmptyProperty(PropertyData<?> prop) {
    if ((prop == null) || (prop.getValues() == null)) {
      return true;
    }
    return prop.getValues().isEmpty();
  }


  public static String getTypeId(Properties properties) {
    PropertyData<?> typeProperty = properties.getProperties().get(PropertyIds.OBJECT_TYPE_ID);
    if (!(typeProperty instanceof PropertyId)) {
      throw new CmisInvalidArgumentException("Type id must be set!");
    }

    String typeId = ((PropertyId) typeProperty).getFirstValue();
    if (typeId == null) {
      throw new CmisInvalidArgumentException("Type id must be set!");
    }

    return typeId;
  }

  public static boolean addPropertyDefault(PropertiesImpl props, PropertyDefinition<?> propDef) {
    if ((props == null) || (props.getProperties() == null)) {
      throw new IllegalArgumentException("Props must not be null!");
    }

    if (propDef == null) {
      return false;
    }

    List<?> defaultValue = propDef.getDefaultValue();
    if ((defaultValue != null) && (!defaultValue.isEmpty())) {
      switch (propDef.getPropertyType()) {
        case BOOLEAN:
          props.addProperty(new PropertyBooleanImpl(propDef.getId(), (List<Boolean>) defaultValue));
          break;
        case DATETIME:
          props.addProperty(new PropertyDateTimeImpl(propDef.getId(), (List<GregorianCalendar>) defaultValue));
          break;
        case DECIMAL:
          props.addProperty(new PropertyDecimalImpl(propDef.getId(), (List<BigDecimal>) defaultValue));
          break;
        case HTML:
          props.addProperty(new PropertyHtmlImpl(propDef.getId(), (List<String>) defaultValue));
          break;
        case ID:
          props.addProperty(new PropertyIdImpl(propDef.getId(), (List<String>) defaultValue));
          break;
        case INTEGER:
          props.addProperty(new PropertyIntegerImpl(propDef.getId(), (List<BigInteger>) defaultValue));
          break;
        case STRING:
          props.addProperty(new PropertyStringImpl(propDef.getId(), (List<String>) defaultValue));
          break;
        case URI:
          props.addProperty(new PropertyUriImpl(propDef.getId(), (List<String>) defaultValue));
          break;
        default:
          throw new RuntimeException("Unknown datatype! Spec change?");
      }

      return true;
    }

    return false;
  }

  public static PermissionDefinition createPermission(String permission, String description) {
    PermissionDefinitionDataImpl pd = new PermissionDefinitionDataImpl();
    pd.setPermission(permission);
    pd.setDescription(description);

    return pd;
  }

  public static PermissionMapping createMapping(String key, String permission) {
    PermissionMappingDataImpl pm = new PermissionMappingDataImpl();
    pm.setKey(key);
    pm.setPermissions(Collections.singletonList(permission));

    return pm;
  }

}
