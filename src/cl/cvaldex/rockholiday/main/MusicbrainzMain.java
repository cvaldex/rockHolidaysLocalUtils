package cl.cvaldex.rockholiday.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

import cl.cvaldex.rockholiday.musicbrainz.MusicbrainzJSONAPICaller;
import cl.cvaldex.rockholiday.parser.JSONParser;
import cl.cvaldex.rockholiday.vo.ReleaseVO;

public class MusicbrainzMain {

	public static void main(String[] args) throws IOException {
		int page = 1;
		String artistMBID = "66c662b6-6e2f-4930-8610-912e24c63ed1";
		
		String [] releaseTypeFilter = {"single" , "ep" , "other"}; //estas categorías no interesan
		
		String json = MusicbrainzJSONAPICaller.getReleaseJSON(artistMBID, page);
		
		int releaseGroupCount = JSONParser.getReleaseGroupCount(json);
		int recordFetched = 0;
		
		System.out.println("Cantidad de Releases: " + releaseGroupCount);
		
		Collection<ReleaseVO> releases = new ArrayList<ReleaseVO>();
		
		do{
			System.out.println("Page: " + page);
			releases.addAll(JSONParser.parseReleaseJSON(json));
			recordFetched = releases.size();
			
			if(recordFetched < releaseGroupCount){
				json = MusicbrainzJSONAPICaller.getReleaseJSON(artistMBID, ++page);
			}
			
		}while(recordFetched < releaseGroupCount);
		
		//filtrar Collection por tipo
		for(String filter : releaseTypeFilter){
			releases.removeIf(r -> r.getReleaseType().equalsIgnoreCase(filter));
		}
		
		//filtrar Collection por fechas vacías
		releases.removeIf(r -> r.getFirstReleaseDate().trim().length() == 0);
		
		//ordenar por fecha de lanzamiento
		Collections.sort((List<ReleaseVO>)releases, (new MusicbrainzMain()).new ReleaseDateComparator());
		
		printFile(releases ,artistMBID);
		
	}
	
	public static void printFile(Collection<ReleaseVO> releases, String artistMBID) throws IOException{
		if(releases.size() > 0){
			StringBuilder filePath = new StringBuilder(); 
			
			filePath.append(System.getProperty("user.home"));
			
			if(!System.getProperty("user.home").endsWith("/")){
				filePath.append("/");
			}
			
			filePath.append("MusicBrainz/");
			filePath.append(artistMBID);
			filePath.append(".txt");
		
			System.out.println("Ruta archivo de salida: " + filePath.toString());
			
			PrintWriter writer = new PrintWriter(new FileWriter(new File(filePath.toString())));
			
			for(ReleaseVO release : releases){
				writer.println(release.getReleaseName() + "\t" + release.getFirstReleaseDate() + "\t" + release.getReleaseType());
			}
			
			writer.close();
		}
	}
	
	public static void printSysOut(Collection<ReleaseVO> releases){
		int i = 1;
		for(ReleaseVO releaseVO : releases){
			System.out.println(i++ + " - "+ releaseVO.toString());
		}
	}
	
	public static String getJSON(){
		return "{\"release-group-count\":7,\"release-group-offset\":0,\"release-groups\":[{\"first-release-date\":\"2013-05-07\",\"primary-type\":\"Single\",\"id\":\"07f21f0e-8e95-4c31-a1ba-239c948dfba9\",\"secondary-type-ids\":[],\"secondary-types\":[],\"disambiguation\":\"original version\",\"title\":\"Highway Tune\",\"primary-type-id\":\"d6038452-8ee0-3f68-affc-2de9a1ede0b9\"},{\"disambiguation\":\"\",\"title\":\"Black Smoke Rising\",\"secondary-types\":[],\"primary-type-id\":\"6d0c5bf6-7a33-3420-a519-44fc63eedebf\",\"primary-type\":\"EP\",\"first-release-date\":\"2017-04-21\",\"secondary-type-ids\":[],\"id\":\"34c4f248-2609-49a7-bda8-1be6337a74e7\"},{\"secondary-types\":[],\"disambiguation\":\"\",\"title\":\"When the Curtain Falls\",\"primary-type-id\":\"d6038452-8ee0-3f68-affc-2de9a1ede0b9\",\"first-release-date\":\"2018-07-17\",\"primary-type\":\"Single\",\"id\":\"4dd950aa-55df-48c8-af87-ff4ea12b09ec\",\"secondary-type-ids\":[]},{\"primary-type\":\"Album\",\"first-release-date\":\"2017-11-10\",\"secondary-type-ids\":[],\"id\":\"870a817f-ce8d-403f-98be-9ce16f9a291b\",\"title\":\"From the Fires\",\"disambiguation\":\"\",\"secondary-types\":[],\"primary-type-id\":\"f529b476-6e62-324f-b0aa-1f3e33d313fc\"},{\"secondary-types\":[],\"title\":\"Anthem of the Peaceful Army\",\"disambiguation\":\"\",\"primary-type-id\":\"f529b476-6e62-324f-b0aa-1f3e33d313fc\",\"first-release-date\":\"2018-10-19\",\"primary-type\":\"Album\",\"id\":\"b65d1b6b-0704-490e-b371-3601ca522476\",\"secondary-type-ids\":[]},{\"id\":\"c7fdf518-cad6-4993-8492-a2f9f95174e2\",\"secondary-type-ids\":[],\"first-release-date\":\"2017-03-31\",\"primary-type\":\"Single\",\"primary-type-id\":\"d6038452-8ee0-3f68-affc-2de9a1ede0b9\",\"secondary-types\":[],\"title\":\"Highway Tune\",\"disambiguation\":\"\"},{\"first-release-date\":\"2018-01-24\",\"primary-type\":\"Single\",\"id\":\"ecfa9851-6e78-4d2d-86ac-5b3f5f804c37\",\"secondary-type-ids\":[\"6fd474e2-6b58-3102-9d17-d6f7eb7da0a0\"],\"secondary-types\":[\"Live\"],\"title\":\"Spotify Singles\",\"disambiguation\":\"\",\"primary-type-id\":\"d6038452-8ee0-3f68-affc-2de9a1ede0b9\"}]}";
	}
	
	class ReleaseDateComparator implements Comparator<ReleaseVO> {
		
		/*
		 * Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
	    public int compare(ReleaseVO a, ReleaseVO b) {
			String [] aReleaseDate = a.getFirstReleaseDate().split("-");
	    	String [] bReleaseDate = b.getFirstReleaseDate().split("-");
	    	
	    	if(aReleaseDate.length > bReleaseDate.length){
	    		return 1;
	    	}
	    	
	    	if(aReleaseDate.length < bReleaseDate.length){
	    		return -1;
	    	}
	    	
	    	if(aReleaseDate.length == 0){
	    		return 0;
	    	}
	    	
	    	int flag = 0;
	    	
	    	if(aReleaseDate.length == 3){
	    		int aYear = getInt(aReleaseDate[0]);
	    		int bYear = getInt(bReleaseDate[0]);
	    		
	    		flag = aYear - bYear;
	    	}
	    	
	    	if(flag == 0){
	    		if(aReleaseDate.length >= 2){
		    		int aMonth = getInt(aReleaseDate[aReleaseDate.length - 2]);
		    		int bMonth = getInt(bReleaseDate[aReleaseDate.length - 2]);
		    		
		    		flag = aMonth - bMonth;
		    	}
	    	}
	    	
	    	if(flag == 0){
	    		if(aReleaseDate.length >= 1){
		    		int aDay = getInt(aReleaseDate[aReleaseDate.length - 1]);
		    		int bDay = getInt(bReleaseDate[aReleaseDate.length - 1]);
		    		
		    		flag = aDay - bDay;
		    	}
	    	}
	    	
	    	return flag;
	    }
		
		public int getInt(String aNumber){
			int aInteger = 0;
			
			try{
				aInteger = Integer.parseInt(aNumber);
			}
			catch(NumberFormatException e){} //mala practica pero no me interesa hacer nada si no es numero
			
			return aInteger;
		}
	}
}

