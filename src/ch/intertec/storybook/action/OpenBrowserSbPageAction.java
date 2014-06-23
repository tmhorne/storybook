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

import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.net.NetTools;

@SuppressWarnings("serial")
public class OpenBrowserSbPageAction extends AbstractAction {

	public static final String ACTION_KEY_PAGE = "page";
	
	public OpenBrowserSbPageAction(){
		super(I18N.getMsg("msg.pro.version.button"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String page = (String) getValue(ACTION_KEY_PAGE);
		NetTools.openBrowserSBPage(page);
	}
}
