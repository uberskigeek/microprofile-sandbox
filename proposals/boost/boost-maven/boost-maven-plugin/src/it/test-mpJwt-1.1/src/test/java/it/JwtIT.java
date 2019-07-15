// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::test[]
package it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;
import org.junit.Before;
import jwt.util.TestUtils;
import jwt.util.JwtVerifier;
import jwt.util.JwtBuilder;
import java.net.URL;
import java.net.URLClassLoader;

public class JwtIT {

  private final String TESTNAME = "TESTUSER";
  private final String INV_JWT = "/inventory/jwt";

  String baseUrl = "http://localhost:9000";

  String authHeader;
  
  

  @Before
  public void setup() throws Exception {
	      JwtVerifier jwtvf = new JwtVerifier();
	      authHeader = "Bearer " + jwtvf.createJwt(TESTNAME,"groups=user");
	      String publicKeyLocation = System.getProperty("mp.jwt.verify.publickey.location");
	      if(publicKeyLocation != null )
	         JwtBuilder.storePublicKey(publicKeyLocation);
	      else 
	    	     throw new Exception("public key location is null");
  }

  @Test
  public void testSuite() {
	//this.testPublicKeyLocation();
    this.testJwtGetName();
    this.testJwtGetCustomClaim();
  }

  public void testPublicKeyLocation() {
	    String jwtUrl = baseUrl + INV_JWT + "/mpJwtProp";
	    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

	    assertEquals(
	        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
	        Status.OK.getStatusCode(), jwtResponse.getStatus());

	    String responseName = jwtResponse.readEntity(String.class);

	    assertNotEquals("The mp.jwt.verify.publickey.location property not set", "public key location property not set",
	        responseName);
  }
  
  public void testJwtGetName() {
    String jwtUrl = baseUrl + INV_JWT + "/username";
    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

    assertEquals(
        "HTTP response code should have been " + Status.OK.getStatusCode() + ".",
        Status.OK.getStatusCode(), jwtResponse.getStatus());

    String responseName = jwtResponse.readEntity(String.class);

    assertEquals("The test name and jwt token name should match", TESTNAME,
        responseName);
  }

  public void testJwtGetCustomClaim() {
    String jwtUrl = baseUrl + INV_JWT + "/customClaim";
    Response jwtResponse = TestUtils.processRequest(jwtUrl, "GET", null, authHeader);

    assertEquals("HTTP response code should have been "
        + Status.FORBIDDEN.getStatusCode() + ".", Status.FORBIDDEN.getStatusCode(),
        jwtResponse.getStatus());
  }

}
// end::test[]
