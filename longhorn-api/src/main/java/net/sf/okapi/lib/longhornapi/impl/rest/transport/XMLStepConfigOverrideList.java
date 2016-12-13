package net.sf.okapi.lib.longhornapi.impl.rest.transport;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
	 * back into an <code>ArrayList</code> of Strings.
	 * 
	 * TODO example
	 * 
	 * @param xml An XMLStringList as XML
	 * @return The list element's contents
	 * @throws JAXBException If an error occurred during the unmarshalling
	 */
	public static ArrayList<StepConfigOverride> unmarshal(String xml) throws JAXBException {
		if(null==xml) {
			return null;
		}
		try {
			JAXBContext jc = JAXBContext.newInstance(XMLStepConfigOverrideList.class);
			Unmarshaller u = jc.createUnmarshaller();
			XMLStepConfigOverrideList list = (XMLStepConfigOverrideList) u.unmarshal(new ByteArrayInputStream(xml.getBytes()));
			return list.getElements();
		}
		catch (JAXBException e) {
			throw new JAXBException(xml, e);
		}
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