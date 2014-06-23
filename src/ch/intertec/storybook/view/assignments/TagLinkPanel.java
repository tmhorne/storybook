package ch.intertec.storybook.view.assignments;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.ScenePeer.Order;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
public class TagLinkPanel extends JPanel implements ActionListener {
	protected TagLink link;
	private int number;

	private JComboBox cobTag;
	private JComboBox cobStartScene;
	private JComboBox cobEndScene;
	private JComboBox cobCharacter;
	private JComboBox cobLocation;

	public TagLinkPanel(TagLink link, int number) {
		super();
		this.link = link;
		this.number = number;
		initGUI();
	}

	private void initGUI() {
		try {
			MigLayout layout = new MigLayout(
					"wrap 3",
					"[]10[grow]10[]",
					"[]20[]20[][][][][]20");
			setLayout(layout);
			setBorder(SwingTools.getBorderDefault());
			setBackground(new Color(255, 255, 255));

			// link number
			JLabel lbLink = new JLabel(I18N.getMsgColon("msg.tag.link.no"));

			// delete link
			JButton btDeleteLink = new JButton();
			btDeleteLink.setAction(getDeleteAction());
			btDeleteLink.setText(I18N.getMsg("msg.common.delete"));
			btDeleteLink.setIcon(I18N.getIcon("icon.small.delete"));

			// tag
			JLabel lbTag = new JLabel();
			lbTag.setText(I18N.getMsgColon("msg.tag"));
			cobTag = createTagCombo();

			// start scene
			JLabel lbStartScene = new JLabel(
					I18N.getMsgColon("msg.tag.start.scene"));
			cobStartScene = createStartSceneCombo();
			cobStartScene.addActionListener(this);
			JButton btUnlinkStartScene = createUnlinkButton(cobStartScene);

			// end scene
			JLabel lbEndScene = new JLabel(
					I18N.getMsgColon("msg.tag.end.scene"));
			cobEndScene = new JComboBox(new DefaultComboBoxModel());
			refreshEndSceneCombo();
			cobEndScene.setSelectedItem(link.getEndScene());

			JButton btUnlinkEndScene = createUnlinkButton(cobEndScene);

			// character
			JLabel lbCharacter = new JLabel(
					I18N.getMsgColon("msg.common.person"));
			cobCharacter = createCharacterCombo();
			JButton btUnlinkCharacter = createUnlinkButton(cobCharacter);

			// location
			JLabel lbLocation = new JLabel(
					I18N.getMsgColon("msg.common.location"));
			cobLocation = createLocationCombo();
			JButton btUnlinkLocation = createUnlinkButton(cobLocation);

			// layout
			add(lbLink);
			add(new JLabel(Integer.toString(this.number)));
			add(btDeleteLink);
			JSeparator hsep = new JSeparator(SwingConstants.HORIZONTAL);
			hsep.setBorder(new LineBorder(new Color(0, 0, 0)));

			add(hsep, "grow,span 3");

			add(lbTag);
			add(cobTag);
			add(new JLabel(""));

			add(lbCharacter);
			add(cobCharacter);
			add(btUnlinkCharacter);

			add(lbLocation);
			add(cobLocation);
			add(btUnlinkLocation);

			add(lbStartScene);
			add(cobStartScene);
			add(btUnlinkStartScene);

			add(lbEndScene);
			add(cobEndScene);
			add(btUnlinkEndScene);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TagLinkPanel getThis() {
		return this;
	}

	private JButton createUnlinkButton(JComboBox combo) {
		JButton bt = new JButton();
		bt.setAction(getUnlinkAction(combo));
		bt.setText(I18N.getMsg("msg.common.unlink"));
		bt.setIcon(I18N.getIcon("icon.small.unlink"));
		return bt;
	}

	private AbstractAction getUnlinkAction(
			final JComboBox combo) {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				combo.setSelectedIndex(0);
			}
		};
	}

	private AbstractAction getDeleteAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				// show confirm dialog
				int n = JOptionPane.showConfirmDialog(getThis(),
						I18N.getMsg("msg.tags.links.delete"),
						I18N.getMsg("msg.common.delete"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION
						|| n == JOptionPane.CLOSED_OPTION) {
					return;
				}
				TagLink link = getThis().getTagLink();
				// mark the link for deletion
				// (deleted in TagLinkPeer.makeOrUpdateTagLink()
				// after OK was pressed)
				link.markForDeletion(true);
				SwingTools.enableContainerChildren(getThis(), false);
			}
		};
	}

	public JComboBox getCharacterCombo() {
		return cobCharacter;
	}

	public JComboBox getTagCombo() {
		return cobTag;
	}

	public JComboBox getLocationCombo() {
		return cobLocation;
	}

	public JComboBox getStartSceneCombo() {
		return cobStartScene;
	}

	public JComboBox getEndSceneCombo() {
		return cobEndScene;
	}

	public TagLink getTagLink() {
		return (TagLink) link;
	}

	private JComboBox createCharacterCombo() {
		try {
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			SbCharacter character = new SbCharacter();
			model.addElement(character);
			DbTable selected = null;
			TagLink link = (TagLink) this.link;
			for (SbCharacter t : SbCharacterPeer.doSelectAll()) {
				if (link.hasCharacter() && t.getId() == link.getCharacterId()) {
					selected = t;
				}
				model.addElement(t);
			}
			JComboBox cb = new JComboBox();
			cb.setModel(model);
			if (selected != null) {
				cb.setSelectedItem(selected);
			} else {
				cb.setSelectedIndex(0);
			}
			return cb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JComboBox createLocationCombo() {
		try {
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			Location location = new Location();
			model.addElement(location);
			DbTable selected = null;
			TagLink link = (TagLink) this.link;
			for (Location t : LocationPeer.doSelectAll()) {
				if (link.hasLocation() && t.getId() == link.getLocationId()) {
					selected = t;
				}
				model.addElement(t);
			}
			JComboBox cb = new JComboBox();
			cb.setModel(model);
			if (selected != null) {
				cb.setSelectedItem(selected);
			} else {
				cb.setSelectedIndex(0);
			}
			return cb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addEmtpyScene(DefaultComboBoxModel model) {
		Scene scene = new Scene();
		scene.setSceneNo(-1);
		model.addElement(scene);
	}

	private void refreshEndSceneCombo() {
		try {
			DefaultComboBoxModel model = (DefaultComboBoxModel) cobEndScene
					.getModel();
			model.removeAllElements();
			addEmtpyScene(model);
			Scene startScene = (Scene) cobStartScene.getSelectedItem();
			List<Scene> scenes = ScenePeer.doSelectByStrandId(startScene
					.getStrandId());
			for (Scene t : scenes) {
				Scene s = (Scene) t;
				s.setToStringShowDate(true);
				s.setToStringTruncateLength(60);
				model.addElement(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JComboBox createStartSceneCombo() {
		try {
			JComboBox cb = new JComboBox();
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			addEmtpyScene(model);
			DbTable selected = null;
			TagLink link = (TagLink) this.link;
			List<Scene> list = ScenePeer
					.doSelectAll(Order.BY_CHAPTER_AND_SCENE_NUMBER);
			for (Scene t : list) {
				if (link.hasStartScene() && t.getId() == link.getStartSceneId()) {
					selected = t;
				}
				Scene s = (Scene) t;
				s.setToStringShowDate(true);
				s.setToStringTruncateLength(60);
				model.addElement(t);
			}
			cb.setModel(model);
			if (selected != null) {
				cb.setSelectedItem(selected);
			} else {
				cb.setSelectedIndex(0);
			}
			return cb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected JComboBox createTagCombo() {
		try {
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			DbTable selected = null;
			TagLink link = (TagLink) this.link;
			for (Tag t : TagPeer.doSelectAll()) {
				if (t.getId() == link.getTagId()) {
					selected = t;
				}
				model.addElement(t);
			}
			JComboBox combo = new JComboBox();
			combo.setModel(model);
			if (selected != null) {
				combo.setSelectedItem(selected);
			}
			return combo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		refreshEndSceneCombo();
	}
}
