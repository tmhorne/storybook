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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.net.BrowserDialog;

public class Updater {

	public static boolean checkForUpdate() {
		try {
			// get version
			// URL url = new URL("http://localhost/storybook/version.txt");
			URL url;
			if (Constants.Application.IS_PRO_VERSION.toBoolean()) {
				url = new URL(Constants.Application.URL + "/version_donors.txt");
			} else {
				url = new URL(Constants.Application.URL + "/version.txt");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String inputLine = "";
			String versionStr = "";
			int c = 0;
			while ((inputLine = in.readLine()) != null) {
				versionStr = inputLine;
				if (c == 0) {
					// currently only the first line is read
					break;
				}
			}
			in.close();

			// compare version
			int remoteVersion = calculateVersion(versionStr);
			int localVersion = 0;
			if (Boolean
					.parseBoolean(Constants.Application.IS_PRO_VERSION
							.toString())) {
				localVersion = calculateVersion(Constants.Application.VERSION_PRO
						.toString());
			} else {
				localVersion = calculateVersion(Constants.Application.VERSION
						.toString());
			}
			if (localVersion < remoteVersion) {
				// String updateUrl =
				// "http://localhost/storybook/update?g_lang="
				// + Locale.getDefault().getLanguage();
				String lang = "en";
				if (Locale.getDefault().getLanguage().equals("de")) {
					lang = "de";
				}
				String updateUrl = "";
				if (Constants.Application.IS_PRO_VERSION.toBoolean()) {
					updateUrl = Constants.Application.URL + "/update_donors";
				} else {
					updateUrl = Constants.Application.URL + "/update?g_lang="
							+ lang;
				}
				BrowserDialog dlg = new BrowserDialog(
						I18N.getMsg("msg.update.title"), updateUrl, 600, 500,
						true);
				SwingTools.showModalDialog(dlg, MainFrame.getInstance());
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static int calculateVersion(String str) {
		String[] s = str.split("\\.");
		if (s.length != 3) {
			return -1;
		}
		int ret = 0;
		ret += Integer.parseInt(s[0]) * 1000000;
		ret += Integer.parseInt(s[1]) * 1000;
		ret += Integer.parseInt(s[2]);
		return ret;
	}
}
