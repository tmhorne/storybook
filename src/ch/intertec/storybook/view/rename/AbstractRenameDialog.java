package ch.intertec.storybook.view.rename;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.swing.SwingTools;

@SuppressWarnings("serial")
abstract public class AbstractRenameDialog extends JDialog implements
		ActionListener {

	private JTextField tfNewName;

	protected JComboBox combo;

	public AbstractRenameDialog(JFrame frame) {
		super(frame);
		initGUI();
	}

	abstract protected List<String> getList();

	abstract protected void rename(String oldValue, String newValue);

	abstract protected String getDlgTitle();

	protected void initGUI() {
		MigLayout layout = new MigLayout("wrap 4", "[]", "[]20[]");
		setLayout(layout);

		setTitle(getDlgTitle());

		List<String> list = getList();
		combo = createCategoryCombo(list);
		combo.addActionListener(this);

		JLabel lbRename = new JLabel(I18N.getMsg("msg.rename.rename"));
		JLabel lbTo = new JLabel(I18N.getMsg("msg.rename.to"));
		tfNewName = new JTextField(20);

		// OK button
		JButton btOk = new JButton();
		btOk.setAction(getOkAction());
		SwingTools.addEnterAction(btOk, getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));

		// cancel button
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		SwingTools.addEscAction(btCancel, getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setIcon(I18N.getIcon("icon.small.close"));

		add(lbRename);
		add(combo);
		add(lbTo);
		add(tfNewName);
		add(btOk, "sg,span,split 2,right");
		add(btCancel, "sg");
	}

	private JComboBox createCategoryCombo(List<String> list) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (String category : list) {
			model.addElement(category);
		}
		JComboBox cob = new JComboBox();
		cob.setModel(model);
		return cob;
	}

	protected AbstractAction getCancelAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				getThis().dispose();
			}
		};
	}

	private AbstractRenameDialog getThis() {
		return this;
	}

	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				String oldValue = (String) combo.getSelectedItem();
				String newValue = tfNewName.getText();
				rename(oldValue, newValue);
				getThis().dispose();
			}
		};
	}

	public void setValue(String value) {
		combo.setSelectedItem(value);
	}
	
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();
		String val = (String) cb.getSelectedItem();
		tfNewName.setText(val);
		Timer timer = new Timer(20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfNewName.requestFocusInWindow();
				tfNewName.selectAll();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
}
