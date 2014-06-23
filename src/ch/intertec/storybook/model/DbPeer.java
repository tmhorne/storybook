package ch.intertec.storybook.model;

import ch.intertec.storybook.toolkit.I18N;

public class DbPeer {
	public static String getCopyString(String str) {
		return str + " (" + I18N.getMsg("msg.common.copy") + ")";
	}

	public static String getInfoText(DbTable dbObj) {
		return getInfoText(dbObj, false);
	}

	public static String getInfoText(DbTable dbObj, boolean shorten) {
		String str = "";
		if (dbObj instanceof SbCharacter) {
			str = ((SbCharacter) dbObj).getInfo(null, shorten);
		} else if (dbObj instanceof Location) {
			str = ((Location) dbObj).getInfo(shorten);
		} else if (dbObj instanceof Strand) {
			str = ((Strand) dbObj).getInfo();
		} else if (dbObj instanceof Chapter) {
			str = ((Chapter) dbObj).getInfo();
		} else if (dbObj instanceof Scene) {
			str = ((Scene) dbObj).getInfo(shorten);
		} else if (dbObj instanceof Part) {
			str = ((Part) dbObj).getInfo();
		} else if (dbObj instanceof Item) {
			str = ((Item) dbObj).getInfo();
		} else if (dbObj instanceof Tag) {
			str = ((Tag) dbObj).getInfo();
		}
		return str;
	}
}
