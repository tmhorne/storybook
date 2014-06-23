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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Gender;
import ch.intertec.storybook.model.GenderPeer;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;

@SuppressWarnings("serial")
public class TableDeleteAction extends AbstractTableAction {
	
	public TableDeleteAction(DbTable table) {
		super(getTitle(table), I18N.getIcon("icon.small.delete"), table);
	}

	public TableDeleteAction(DbTable table, boolean noName) {
		super(I18N.getIcon("icon.small.delete"), table);
	}

	public void actionPerformed(ActionEvent evt) {
		Container container = (Container) getValue(
				AbstractTableAction.ActionKey.CONTAINER.toString());
		int count = 0;
		StringBuffer buf = new StringBuffer();
		if (dbObj instanceof Scene) {
			Scene scene = (Scene) dbObj;
			String str = scene.getChapterAndSceneNumber() + ": "
					+ scene.getTitle(true, 100);
			buf.append(I18N.getMsg("msg.dlg.delete", str));
		} else if (dbObj instanceof Strand) {
			count = ScenePeer.doCountByStrandId(dbObj.getId());
			if (count > 0) {
				buf.append(I18N.getMsg("msg.dlg.delete.strand"));
			}
			buf.append(I18N.getMsg("msg.dlg.delete", dbObj));
		} else {
			buf.append(I18N.getMsg("msg.dlg.delete", dbObj));
		}
		final JOptionPane confirmPane = new JOptionPane();
		confirmPane.setMessage(buf.toString());
		JButton yesBtn = new JButton("Yes");
		JButton noBtn = new JButton("No");
		confirmPane.setOptions(new JButton[]{yesBtn, noBtn});
		final JDialog dialog = confirmPane.createDialog(getTitle(dbObj));
		yesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmPane.setValue(0);
				dialog.dispose();
			}
		});
		noBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmPane.setValue(1);
				dialog.dispose();
			}
		});
		if (count == 0) {
			yesBtn.requestFocusInWindow();
		} else {
			noBtn.requestFocusInWindow();
		}
		dialog.setVisible(true);
		dialog.dispose();
		Object value = confirmPane.getValue();
		int confirmValue = value == null ? JOptionPane.NO_OPTION
				: ((Integer) value).intValue();
		if (confirmValue == JOptionPane.NO_OPTION
				|| confirmValue == JOptionPane.CLOSED_OPTION) {
			if (container != null) {
				if (container instanceof IRefreshable) {
					((IRefreshable) container).refresh();
				}
			}
			return;
		}
		try {
			if (dbObj instanceof SbCharacter) {
				SbCharacterPeer.doDelete((SbCharacter) dbObj);
			} else if (dbObj instanceof Location) {
				LocationPeer.doDelete((Location) dbObj);
			} else if (dbObj instanceof Strand) {
				StrandPeer.doDelete((Strand) dbObj);
			} else if (dbObj instanceof Chapter) {
				ChapterPeer.doDelete((Chapter) dbObj);
			} else if (dbObj instanceof Scene) {
				ScenePeer.doDelete((Scene) dbObj);
			} else if (dbObj instanceof Part) {
				PartPeer.doDelete((Part) dbObj);
			} else if (dbObj instanceof Item) {
				ItemPeer.doDelete((Item) dbObj);
			} else if (dbObj instanceof Tag) {
				TagPeer.doDelete((Tag) dbObj);
			} else if (dbObj instanceof Gender) {
				GenderPeer.doDelete((Gender) dbObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SwingTools.showException(e);
		}
	}

	private static String getTitle(DbTable table) {
		if (table instanceof Chapter) {
			return I18N.getMsg("msg.common.chapter.remove");
		} else if (table instanceof Location) {
			return I18N.getMsg("msg.common.location.remove");
		} else if (table instanceof SbCharacter) {
			return I18N.getMsg("msg.common.person.remove");
		} else if (table instanceof Scene) {
			return I18N.getMsg("msg.common.scene.remove");
		} else if (table instanceof Strand) {
			return I18N.getMsg("msg.common.strand.remove");
		} else if (table instanceof Part) {
			return I18N.getMsg("msg.common.part.remove");
		} else if (table instanceof Item) {
			return I18N.getMsg("msg.item.remove");
		} else if (table instanceof Tag) {
			return I18N.getMsg("msg.tag.remove");
		}
		return I18N.getMsg("msg.common.delete");
	}
}
