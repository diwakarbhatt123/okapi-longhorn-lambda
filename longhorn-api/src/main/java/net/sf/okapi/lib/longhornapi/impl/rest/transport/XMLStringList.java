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

package net.sf.okapi.lib.longhornapi.impl.rest.transport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to transform a collection of Strings to XML using JAXB
 * and for transforming that XML format back into an ArrayList of Strings.
 * 
 * TODO example
 */
@XmlRootElement(name="l")
public class XMLStringList {

	@XmlElement(name = "e")
    private ArrayList<String> elements;

    /**
     * Creates a new empty list.
     */
    public XMLStringList(){
    	elements = new ArrayList<String>();
    }

    /**
     * Creates a new list from all elements in the parameter list.
     * 
     * For every element in the parameter list the <code>toString()</code> method is used to get a
     * string representation of the element that will be used to save it's content in XML.
     * 
     * @param list A Collection of the objects to be transformed into XML
     */
    public XMLStringList(Collection<? extends Object> list) {
    	elements = new ArrayList<String>();
    	for (Object item : list) {
    		add(item);
    	}
    }
    
    /**
     * Add an item to the list. The <code>toString()</code> method is used to get a
     * string representation of the element that will be used to save it's content in XML.
     * 
     * @param item An Object which's string representation shall be added to this list
     */
    public void add(Object item) {
    	elements.add(item.toString());
    }
    
    /**
     * @return The string representations of all elements
     */
    public ArrayList<String> getElements() {
		return elements;
    }
    
	/**
	 * Transforms the XML representation of an <code>XMLStringList</code>
	 * back into an <code>ArrayList</code> of Strings.
	 * 
	 * TODO example
	 * 
	 * @param xml An XMLStringList as XML
	 * @return The list element's contents
	 * @throws JAXBException If an error occurred during the unmarshalling
	 */
	public static ArrayList<String> unmarshal(String xml) throws JAXBException {
		try {
			JAXBContext jc = JAXBContext.newInstance(XMLStringList.class);
			Unmarshaller u = jc.createUnmarshaller();
			XMLStringList list = (XMLStringList) u.unmarshal(new ByteArrayInputStream(xml.getBytes()));
			return list.getElements();
		}
		catch (JAXBException e) {
			throw new JAXBException(xml, e);
		}
    }

}
