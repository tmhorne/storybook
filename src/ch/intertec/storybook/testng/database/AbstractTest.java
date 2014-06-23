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

package ch.intertec.storybook.testng.database;

import java.io.File;

import org.apache.log4j.BasicConfigurator;

import ch.intertec.storybook.model.PersistenceManager;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;

public abstract class AbstractTest {
	private PersistenceManager persistenceManager;
	private PrefManager preferenceManager;

	public AbstractTest() {
		init();
	}

	public AbstractTest(File file) {
		try {
			PersistenceManager instance = PersistenceManager.getInstance();
			instance.create(file);
			instance.initDbModel();
			instance.getConnection();
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		I18N.initResourceBundles();
		persistenceManager = PersistenceManager.getInstance();
		preferenceManager = PrefManager.getInstance();
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	public PrefManager getPreferenceManager() {
		return preferenceManager;
	}
}
