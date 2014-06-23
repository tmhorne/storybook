/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package ch.intertec.storybook.toolkit.swing.table;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.SbCharacter;

public class WiwwContainer {

	private Location location;
	private List<SbCharacter> inCharacterList;
	private List<SbCharacter> outCharacterList;
	private Date date;
	private boolean found;

	public WiwwContainer(Date date, Location location,
			List<SbCharacter> characterList) {
		this.location = location;
		this.inCharacterList = characterList;
		this.date = date;
		this.outCharacterList = new ArrayList<SbCharacter>();
		init();
	}

	private void init(){
		for (SbCharacter character : inCharacterList) {
			int count = LocationPeer.doCountByCharacterLocationDate(
					character, location, date);
			if (count == 0) {
				continue;
			}
			outCharacterList.add(character);
		}
		if (outCharacterList.isEmpty()) {
			found = false;
		} else {
			found = true;
		}
	}

	public List<SbCharacter> getCharacterList(){
		return outCharacterList;
	}

	public boolean isFound() {
		return this.found;
	}
}
