package cl.cvaldex.rockholiday.parser;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cl.cvaldex.rockholiday.vo.ReleaseVO;

public class JSONParser {
	
	/**
	 * 
	 * @param json texto con estructura JSON
	 * @return
	 */
	public static Collection<ReleaseVO> parseReleaseJSON(String json){
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
	
	public static int getReleaseGroupCount(String json){
		JsonParser parser = new JsonParser();

		JsonObject releaseGroup = parser.parse(json).getAsJsonObject();
		
		return releaseGroup.get("release-group-count").getAsInt();
	}
}
