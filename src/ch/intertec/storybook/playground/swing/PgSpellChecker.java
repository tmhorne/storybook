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

package ch.intertec.storybook.playground.swing;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.inet.jortho.SpellChecker;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PgSpellChecker extends JFrame {

	public static void main(String[] args) {
		new PgSpellChecker();
	}

	public PgSpellChecker() {
		super();
		try {
			setSize(400, 400);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			initGUI();
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initGUI() throws Exception {
		setLayout(new MigLayout());

		JEditorPane text = new JEditorPane();
        text.setText( "This is a simppler textt with spellingg errors." );
        add( text );

		File dir = new File (".");
		System.out.println("Current dir : " + dir.getCanonicalPath());

		File file = new File(
				dir.getCanonicalPath()
				+ File.separator
				+ "dict");
		System.out.println("file: " + file);
		URI uri = file.toURI();
		URL url = uri.toURL();
		System.out.println("url: " + url);
		
		JTextArea ta = new JTextArea();
		
		SpellChecker.registerDictionaries(url, "en,de", "en");
        SpellChecker.register( text );
        SpellChecker.register( ta );
	}
}
