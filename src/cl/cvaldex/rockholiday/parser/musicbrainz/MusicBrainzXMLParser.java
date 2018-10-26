package cl.cvaldex.rockholiday.parser.musicbrainz;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.beanutils.BeanUtils;

import cl.cvaldex.rockholiday.vo.ReleaseVO;

public class MusicBrainzXMLParser implements MusicBrainzParser {
	
	public Collection<ReleaseVO> parseReleases(String text){
		Collection<ReleaseVO> releases = new ArrayList<ReleaseVO>();
		
		Reader reader = new StringReader(text);
		XMLInputFactory factory = XMLInputFactory.newInstance(); // Or newFactory()
		
		try {
			XMLEventReader eventReader = factory.createXMLEventReader(reader);
			ReleaseVO releaseVO = null;
			
			while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    
                    if (startElement.getName().getLocalPart().equals("release-group")) {
                    	releaseVO = new ReleaseVO();
                    	
                    	setAttribute(releaseVO , "releaseType" , "type" , startElement);
                    	setAttribute(releaseVO , "typeId" , "type-id" , startElement);
                    }
        
                    if (startElement.getName().getLocalPart().equals("title")) {
                        event = eventReader.nextEvent();
                        
                        releaseVO.setReleaseName(event.asCharacters().getData());
                        
                        continue;
                    }
                    
	                if (startElement.getName().getLocalPart().equals("first-release-date")) {
	                    event = eventReader.nextEvent();
	                    
	                    try{
	                    	releaseVO.setFirstReleaseDate(event.asCharacters().getData());
	                    }
	                    catch(java.lang.ClassCastException ex){
	                    	releaseVO.setFirstReleaseDate("");
	                    }
	                    continue;
	                }
                
	                if (startElement.getName().getLocalPart().equals("primary-type")) {
	                	setAttribute(releaseVO , "primaryTypeId" , "id" , startElement);
	                }
	                
	                if (startElement.getName().getLocalPart().equals("secondary-type")) {
	                	setAttribute(releaseVO , "secondaryTypeId" , "id" , startElement);
                    }
                }
                else{
                	if(event.isEndElement()){
                		EndElement endElement = event.asEndElement();
                		
                		if("release-group".equals(endElement.getName().getLocalPart())){
                			releases.add(releaseVO);
                		}
                	}
                		
                }
			}
        } 
		catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		return releases;
	}
	
	private static void setAttribute(ReleaseVO release, String field, String xmlAttribute , StartElement element){
		Iterator<Attribute> attributes = element.getAttributes();
    	
		while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (attribute.getName().toString().equals(xmlAttribute)) {
            	try{
            		BeanUtils.setProperty(release, field, attribute.getValue());
            	}
            	catch(IllegalAccessException iae){
            		throw new RuntimeException(iae);
            	}
            	catch(InvocationTargetException ite){
            		throw new RuntimeException(ite);
            	}
            	
            	break;
            }
    	}
	}
	
	public int getReleaseGroupCount(String text){
		int count = 0;
		Reader reader = new StringReader(text);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		try {
			XMLEventReader eventReader = factory.createXMLEventReader(reader);
			
			while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                   
                    if (startElement.getName().getLocalPart().equals("release-group-list")) {
                    	Iterator<Attribute> attributes = startElement.getAttributes();
                
                    	while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            if (attribute.getName().toString().equals("count")) {
                            	count = Integer.parseInt(attribute.getValue());
                            	break;
                            }
                    	}
                    	
                    	break;
                    }
                }
			}
		}catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		return count;
	}
}
