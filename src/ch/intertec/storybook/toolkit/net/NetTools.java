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

package ch.intertec.storybook.toolkit.net;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import ch.intertec.storybook.toolkit.Constants;

public class NetTools {

	private static String googleMapUrl;
	
	public static void openBrowser(String path) {
		try {
			Desktop.getDesktop().browse(new URI(path));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void openGoogleMap(String query){
		try {
			String queryEnc = URLEncoder.encode(query, "UTF-8");
			String path = getGoogleMapsUrl() + "/?q=" + queryEnc;
			openBrowser(path);			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void openBrowserSBPage(String page) {
//		String lang = "";
		StringBuffer buf = new StringBuffer();
//		if (Locale.getDefault().getLanguage().equals("es")) {
//			lang = "en";
//		} else if (Locale.getDefault().getLanguage().equals("da")) {
//			lang = "en";
//		} else {
//			lang = Locale.getDefault().getLanguage();
//		}
		buf.append(Constants.Application.URL + page);
//		buf.append("&g_lang=" + lang);
		openBrowser(buf.toString());
	}

	public static String getGoogleMapsUrl() {
		return googleMapUrl;
	}

	public static void setGoogleMapUrl(String googleMapUrl) {
		NetTools.googleMapUrl = googleMapUrl;
	}

}
