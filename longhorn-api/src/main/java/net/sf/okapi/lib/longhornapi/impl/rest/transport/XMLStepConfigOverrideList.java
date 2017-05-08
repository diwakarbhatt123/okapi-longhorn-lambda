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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Helper class to transform a collection of Strings pairs to XML using JAXB
 * and for transforming that XML format back into an Map of the string pairs. 
 * Each pair is the fully qualified class name of an Okapi pipeline step and the 
 * string representation of the step's parameters.
 * 
 * Example:
 * <pre>
 * {@code
 *  
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <l>
 * 	<e>
 * 		<stepClassName>abcd</stepClassName>
 * 		<stepParams>def</stepParams>
 * 	</e>
 * 	<e>
 * 		<stepClassName>net.sf.okapi.steps.textmodification.TextModificationStep</stepClassName>
 * 		<stepParams>#v1
 * type.i=0
 * addPrefix.b=false
 * prefix={START_
 * addSuffix.b=false
 * suffix=_END}
 * applyToExistingTarget.b=false
 * addName.b=false
 * addID.b=false
 * markSegments.b=false
 * applyToBlankEntries.b=false
 * expand.b=false
 * script.i=0</stepParams>
 * 	</e>
 * </l>
 * }
 */
@XmlRootElement(name="l")
public class XMLStepConfigOverrideList {

	@XmlElement(name = "e")
    private ArrayList<StepConfigOverride> elements;

    /**
     * Creates a new empty list.
     */
    public XMLStepConfigOverrideList(){
    	elements = new ArrayList<StepConfigOverride>();
    }

    /**
     * Creates a new list from all elements in the parameter list.
     * 
     * For every element in the parameter list the <code>toString()</code> method is used to get a
     * string representation of the element that will be used to save it's content in XML.
     * 
     * @param list A Collection of the objects to be transformed into XML
     */
    public XMLStepConfigOverrideList(Collection<? extends StepConfigOverride> list) {
    	elements = new ArrayList<StepConfigOverride>();
    	for (StepConfigOverride item : list) {
    		add(item);
    	}
    }
    
    /**
     * Add an item to the list. The <code>toString()</code> method is used to get a
     * string representation of the element that will be used to save it's content in XML.
     * 
     * @param item An Object which's string representation shall be added to this list
     */
    public void add(StepConfigOverride item) {
    	elements.add(item);
    }
    
    /**
     * @return The string representations of all elements
     */
    public ArrayList<StepConfigOverride> getElements() {
		return elements;
    }
    
	/**
	 * Transforms the XML representation of an <code>XMLStringList</code>
	 * back into an <code>Map</code> of Step class name to step params. For example XML
	 * refer to the class description above.
	 * 
	 * @param xml An XMLStringList as XML
	 * @return The map of pipeline step class names to the corresponding override params
	 * @throws JAXBException If an error occurred during the unmarshalling
	 */
	public static Map<String, String> unmarshal(String xml) throws JAXBException {
		if(null==xml) {
			return new HashMap<>();
		}
		try {
			JAXBContext jc = JAXBContext.newInstance(XMLStepConfigOverrideList.class);
			Unmarshaller u = jc.createUnmarshaller();
			XMLStepConfigOverrideList list = (XMLStepConfigOverrideList) u.unmarshal(new ByteArrayInputStream(xml.getBytes()));
			return convertToMap(list.getElements());
		}
		catch (JAXBException e) {
			throw new JAXBException(xml, e);
		}
    }

	private static Map<String, String> convertToMap(ArrayList<StepConfigOverride> unmarshal) {
		HashMap<String, String> overrideParams = new HashMap<String, String>(unmarshal.size());
		for(StepConfigOverride sco : unmarshal) {
			if(overrideParams.get(sco.getStepClassName())!=null) {
				throw new IllegalArgumentException("Duplicate step class name in override params not allowed.");
			}
			overrideParams.put(sco.getStepClassName(), sco.getStepParams());
		}
		return overrideParams;
	}

	public static String marshal(XMLStepConfigOverrideList list) throws JAXBException {
		if(null==list) {
			return null;
		}
		JAXBContext jc = JAXBContext.newInstance(XMLStepConfigOverrideList.class);
		Marshaller m = jc.createMarshaller();
		StringWriter w = new StringWriter();
		m.marshal(list, w);
		w.flush();
		return w.toString();
    }
}