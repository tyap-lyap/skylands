package skylands.data;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

import java.util.ArrayList;

public interface AbstractPlayerData extends ComponentV3 {

	ArrayList<String> getIslands();
	void setIslands(ArrayList<String> islands);
}
