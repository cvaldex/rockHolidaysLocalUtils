package cl.cvaldex.rockholiday.parser.musicbrainz;

import java.util.Collection;

import cl.cvaldex.rockholiday.vo.ReleaseVO;

public interface MusicBrainzParser {
	public Collection<ReleaseVO> parseReleases(String text);
	public int getReleaseGroupCount(String text);
}