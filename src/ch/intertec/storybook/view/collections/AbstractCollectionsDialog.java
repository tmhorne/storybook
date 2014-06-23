package ch.intertec.storybook.view.collections;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.PCSDispatcher;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
abstract public class AbstractCollectionsDialog extends JDialog {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(AbstractCollectionsDialog.class);

	private JList icList;

	private DefaultListModel listModel;
	
	public AbstractCollectionsDialog() {
		initGUI();
	}

	abstract protected void addData();
	
	abstract protected String getAddTextKey();
	
	private void initGUI() {
		MigLayout layout = new MigLayout("wrap,fill", "[]", "[]");
		setLayout(layout);
		setPreferredSize(new Dimension(600, 400));
		ImageIcon icon = (ImageIcon) I18N.getIcon("icon.sb");
		setIconImage(icon.getImage());

		// collection list
		listModel = new DefaultListModel();
		addData();
		
		icList = new JList(listModel);
		icList.setCellRenderer(new IcListCellRenderer());
		icList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		icList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scroller = new JScrollPane(icList);

		// add button
		JButton btAdd = new JButton();
		btAdd.setAction(getAddAction());
		btAdd.setText(I18N.getMsg(getAddTextKey()));

		// cancel button
		JButton btCancel = new JButton();
		btCancel.setAction(getCloseAction());
		SwingTools.addEscAction(btCancel, getCloseAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));

		// layout
		this.add(scroller, "grow");
		this.add(btAdd, "sg,split 2");
		this.add(btCancel, "sg,gap push");
	}

	protected void add(String titleKey, String[] tags, String iconKey) {
		TagCollection ic = new TagCollection(titleKey, tags, iconKey);
		listModel.addElement(ic);
	}
	
	private AbstractAction getCloseAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
			}
		};
	}

	private AbstractAction getAddAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				TagCollection ic = (TagCollection) icList.getSelectedValue();
				if (ic == null) {
					return;
				}
				try {
					if (getThis() instanceof TagCollectionsDialog) {
						for (Tag tag : ic.getTags()) {
							tag.save();
							PCSDispatcher.getInstance().firePropertyChange(
									PCSDispatcher.Property.TAG, null, tag);
						}
					} else if (getThis() instanceof ItemCollectionsDialog) {
						for (Item item : ic.getItems()) {
							item.save();
							PCSDispatcher.getInstance().firePropertyChange(
									PCSDispatcher.Property.ITEM, null, item);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				getThis().dispose();
			}
		};
	}

	private AbstractCollectionsDialog getThis() {
		return this;
	}

	private class IcListCellRenderer implements
			ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(
				JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JPanel panel = new JPanel(new MigLayout("flowx,fill"));
			panel.setOpaque(false);
			TagCollection tc = (TagCollection) value;
			JLabel lb = new JLabel(tc.getText());
			lb.setIcon(tc.getIcon());
			lb.setOpaque(true);
			lb.setIconTextGap(40);
			lb.setBorder(new EmptyBorder(10, 16, 10, 16));
			if (isSelected) {
				lb.setBackground(SwingTools.getTableSelectionBackgroundColor());
			} else {
				lb.setBackground(SwingTools.getBackgroundColor());
			}
			panel.add(lb, "grow");
			return panel;
		}
	}
	
	private class TagCollection {
		private String category;
		private String[] names;
		private Icon icon;

		public TagCollection(String category, String[] names, String icon) {
			this.category = I18N.getMsg(category);
			this.names = names;
			this.icon = I18N.getIcon(icon);
		}

		public String getText() {
			String ret = "<html><b>"+getCategory() + "</b><br>";
			ret+="<table width='400'><tr><td>";
			int i = 0;
			for (String s : names) {
				ret += I18N.getMsg(s);
				if (i < names.length - 1) {
					ret += ", ";
				}
				++i;
			}
			ret +="</td></tr></table>";
			return ret;
		}

		public String getCategory() {
			return category;
		}

		public ArrayList<Tag> getTags() {
			ArrayList<Tag> tags = new ArrayList<Tag>();
			for (String s : names) {
				Tag item = new Tag();
				item.setCategory(getCategory());
				item.setName(I18N.getMsg(s));
				tags.add(item);
			}
			return tags;
		}

		public ArrayList<Item> getItems() {
			ArrayList<Item> items = new ArrayList<Item>();
			for (String s : names) {
				Item item = new Item();
				item.setCategory(getCategory());
				item.setName(I18N.getMsg(s));
				items.add(item);
			}
			return items;
		}

		public Icon getIcon() {
			return icon;
		}
	}
}
