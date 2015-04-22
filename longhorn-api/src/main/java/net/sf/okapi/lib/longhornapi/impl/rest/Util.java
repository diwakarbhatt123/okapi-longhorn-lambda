/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.longhornapi.impl.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import net.sf.okapi.lib.longhornapi.impl.rest.transport.XMLStringList;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

public class Util {

	public static URI createProject(URI baseUri) throws URISyntaxException, HttpException, IOException {
		HttpClient client = new HttpClient();
		
		PostMethod postMethod = new PostMethod(baseUri + "/projects/new");
		client.executeMethod(postMethod);
		Header projectUri = postMethod.getResponseHeader("Location");
		postMethod.releaseConnection();
		URI projUri = new URI(projectUri.getValue());
		return projUri;
	}

	public static void put(String uri, Part[] params) throws IOException {
		HttpClient client = new HttpClient();
		
		PutMethod putMethod = new PutMethod(uri);
		putMethod.setRequestEntity(new MultipartRequestEntity(params, putMethod.getParams()));
		client.executeMethod(putMethod);
		putMethod.releaseConnection();
	}

	public static void delete(String uri) throws IOException {
		HttpClient client = new HttpClient();
		
		DeleteMethod delMethod = new DeleteMethod(uri);
		client.executeMethod(delMethod);
		delMethod.releaseConnection();
	}

	public static void post(String uri, Part[] parts) throws IOException {
		HttpClient client = new HttpClient();
		
		PostMethod postMethod = new PostMethod(uri);
		if (parts != null)
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
		
		int status = client.executeMethod(postMethod);
		
		if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			throw new RuntimeException(postMethod.getResponseBodyAsString());
		
		postMethod.releaseConnection();
	}

	public static ArrayList<String> getList(String uri) throws IOException, JAXBException {
		HttpClient client = new HttpClient();
		
		GetMethod getMethod = new GetMethod(uri);
		client.executeMethod(getMethod);
		String xmlList = getMethod.getResponseBodyAsString();
		getMethod.releaseConnection();
		
		return XMLStringList.unmarshal(xmlList);
	}
}
