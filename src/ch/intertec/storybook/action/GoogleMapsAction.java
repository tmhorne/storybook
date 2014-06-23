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

package ch.intertec.storybook.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.net.NetTools;

@SuppressWarnings("serial")
public class GoogleMapsAction extends AbstractAction {
	private Location location;

	public GoogleMapsAction(Location location) {
		super("Google Maps", I18N.getIcon("icon.small.map"));
		this.location = location;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		StringBuffer buf = new StringBuffer();
		buf.append(location.getCity());
		buf.append(",");
		buf.append(location.getAddress());
		buf.append(",");
		buf.append(location.getCountry());
		NetTools.openGoogleMap(buf.toString());
	}
}
