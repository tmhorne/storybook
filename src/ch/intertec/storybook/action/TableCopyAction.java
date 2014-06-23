package ch.intertec.storybook.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class TableCopyAction extends AbstractAction {

	DbTable dbObj;

	public TableCopyAction(DbTable dbObj) {
		super(I18N.getMsg("msg.common.copy"), I18N.getIcon("icon.small.copy"));
		this.dbObj = dbObj;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (dbObj instanceof Location) {
			LocationPeer.makeCopy((Location) dbObj);
		} else if (dbObj instanceof Item) {
			ItemPeer.makeCopy((Item) dbObj);
		} else if (dbObj instanceof Tag) {
			TagPeer.makeCopy((Tag) dbObj);
		} else if (dbObj instanceof SbCharacter) {
			SbCharacterPeer.makeCopy((SbCharacter) dbObj);
		} else if (dbObj instanceof Chapter) {
			ChapterPeer.makeCopy((Chapter) dbObj);
		} else if (dbObj instanceof Part) {
			PartPeer.makeCopy((Part) dbObj);
		} else if (dbObj instanceof Strand) {
			StrandPeer.makeCopy((Strand) dbObj);
		} else if (dbObj instanceof Gender) {
			GenderPeer.makeCopy((Gender) dbObj);
		}
	}
}
