/*===========================================================================
  Copyright (C) 2011-2017 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
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
