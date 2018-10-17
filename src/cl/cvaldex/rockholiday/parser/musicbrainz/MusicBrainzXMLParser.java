package cl.cvaldex.rockholiday.parser.musicbrainz;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cl.cvaldex.rockholiday.vo.ReleaseVO;

public class MusicBrainzXMLParser implements MusicBrainzParser {
	
	/**
	 * 
	 * @param json texto con estructura JSON
	 * @return
	 */
	public Collection<ReleaseVO> parseReleases(String json){
		Collection<ReleaseVO> releases = new ArrayList<ReleaseVO>();
		
		JsonParser parser = new JsonParser();

        // Obtain Array
		JsonObject releaseGroup = parser.parse(json).getAsJsonObject();
		
		JsonArray releaseGroups = releaseGroup.get("release-groups").getAsJsonArray();
		
		ReleaseVO releaseVO = null;
		
        for (JsonElement release : releaseGroups) {
            JsonObject gsonObj = release.getAsJsonObject();
            
            releaseVO = new ReleaseVO();
            releaseVO.setReleaseName(gsonObj.get("title").getAsString().trim());
            //releaseVO.setReleaseType(StringUtils.defaultString(gsonObj.get("primary-type").getAsString()));
            releaseVO.setReleaseType(gsonObj.get("primary-type").isJsonNull() ? "" : gsonObj.get("primary-type").getAsString().trim());
            releaseVO.setFirstReleaseDate(gsonObj.get("first-release-date").getAsString().trim());
            
            //filtrado
            releaseVO.setPrimaryTypeId(gsonObj.get("primary-type-id").isJsonNull() ? "" : gsonObj.get("primary-type-id").getAsString().trim());
            System.out.println("primary-type-id: " + gsonObj.get("primary-type-id") + " --- " + releaseVO.getReleaseName());
            
            
            releases.add(releaseVO);
        } 
		return releases;
	}
	
	public int getReleaseGroupCount(String json){
		JsonParser parser = new JsonParser();

		JsonObject releaseGroup = parser.parse(json).getAsJsonObject();
		
		return releaseGroup.get("release-group-count").getAsInt();
	}
}
