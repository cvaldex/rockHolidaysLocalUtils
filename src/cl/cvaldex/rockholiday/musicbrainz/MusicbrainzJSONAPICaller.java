package cl.cvaldex.rockholiday.musicbrainz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MusicbrainzJSONAPICaller {
	public static String BASE_URL = "https://musicbrainz.org/ws/2/release-group?artist=$ARTIST_MBID&amp;limit=$LIMIT&amp;offset=$OFFSET&amp;fmt=json";
	private static int LIMIT = 100;
	
	public static String getReleaseJSON(String artistMBID , int page) throws IOException{
		String tmpURL = BASE_URL.replaceAll("\\$ARTIST_MBID", artistMBID);
		tmpURL = tmpURL.replaceAll("\\$LIMIT", String.valueOf(LIMIT));
		
		int offset = (page - 1) * 100;
		tmpURL = tmpURL.replaceAll("\\$OFFSET", String.valueOf(offset));
		
		System.out.println("URL: " + tmpURL);
		
		URL musicbrainzURL = new URL(tmpURL);
		
		BufferedReader in = new BufferedReader(
        new InputStreamReader(musicbrainzURL.openStream()));
        StringBuilder buffer = new StringBuilder();
        

        String inputLine;
        while ((inputLine = in.readLine()) != null){
            buffer.append(inputLine);
        }    
        
        in.close();
        
        return buffer.toString();
	}
}
