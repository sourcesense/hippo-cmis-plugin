package com.sourcesense.opencmis.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.hippoecm.hst.jaxrs.model.content.NodeRepresentation;
import org.hippoecm.hst.jaxrs.services.content.AbstractContentResource;

public class ProductContentResource extends AbstractContentResource {

    @GET
    @Path("/")
    public NodeRepresentation getProductResource(@Context HttpServletRequest servletRequest, @Context HttpServletResponse servletResponse) {
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
    	System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
        return null;
    }


}