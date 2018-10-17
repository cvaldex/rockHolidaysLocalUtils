package cl.cvaldex.rockholiday.parser.musicbrainz;

public class MusicBrainzParserFactory {
	public static String JSON_PARSER = "json";
	public static String XML_PARSER = "xml";
	
	public static MusicBrainzParser getParser(String parserId){
		MusicBrainzParser parser = null;
		
		if(JSON_PARSER.equalsIgnoreCase(parserId)){
			parser = new MusicBrainzJSONParser();
		}
		
		if(XML_PARSER.equalsIgnoreCase(parserId)){
			//@to-do 
			parser = new MusicBrainzXMLParser();
		}
		
		if(parser == null){
			throw new RuntimeException("Parser type must be json or xml");
		}
		
		return parser;
	}
}
