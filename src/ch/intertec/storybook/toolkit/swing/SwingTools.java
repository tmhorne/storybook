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

package ch.intertec.storybook.toolkit.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import ch.intertec.storybook.action.TableCopyAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.PersistenceManager;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.StrandPeer;
import ch.intertec.storybook.model.thin.ThinLocation;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.FlashThread;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.PrefManager;
import ch.intertec.storybook.toolkit.Constants.LookAndFeel;
import ch.intertec.storybook.view.lists.AbstractListFrame;
import ch.intertec.storybook.view.navigation.FindChapterDialog;
import ch.intertec.storybook.view.navigation.FindDateDialog;

import com.toedter.calendar.JDateChooser;

public class SwingTools {

	private static Boolean flashIsRunning = false;
	
	private static boolean messageReadonlyShown = false;
	private static boolean messageInUse = false;
	
	public static void expandRectangle(Rectangle rect) {
		Point p = rect.getLocation();
		p.translate(-5, -5);
		rect.setLocation(p);
		rect.grow(10, 10);
	}
	
	public static Font getFontBold(int size) {
		return new Font("DialogInput", Font.BOLD, size);
	}
	
	/**
	 * Creates and returns a strand combination box
	 * 
	 * @return a {@link JComboBox} containing a list of {@link Strand} objects.
	 */
	public static JComboBox createStrandComboBox(int strandId) {
		try {
			JComboBox cb;
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			List<Strand> list = StrandPeer.doSelectAll();
			int c = 0;
			int index = 0;
			for (DbTable t : list) {
				if (strandId > -1 && t.getId() == strandId) {
					index = c;
				}
				model.addElement(t);
				++c;
			}
			cb = new JComboBox();
			cb.setModel(model);
			if (index > 0) {
				cb.setSelectedIndex(index);
			}
			
			Map<Object, Icon> icons = new HashMap<Object, Icon>();
			for (DbTable t : list) {
				Strand strand = (Strand) t;
				icons.put(strand, new ColorIcon(strand.getColor()));
			}
			cb.setRenderer(new IconListRenderer(icons));
			
			return cb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JComboBox();
	}
	
	public static void setAccelerator(JMenuItem menuItem, int key, int mask) {
		menuItem.setAccelerator(KeyStroke.getKeyStroke(key, mask));
	}
	
	public static void addCopyPasteToMenu(JMenu menu, JComponent comp) {
		boolean enableCut = false;
		boolean enableCopy = false;
		boolean enablePaste = false;
		HashMap<Object, Action> actions = null;
		JMenuItem miCut = new JMenuItem();
		JMenuItem miCopy = new JMenuItem();
		JMenuItem miPaste = new JMenuItem();
		if (comp != null) {
			if (comp instanceof JTextArea) {
				actions = SwingTools.createActionTable((JTextArea) comp);
				enableCut = true;
				enableCopy = true;
				enablePaste = true;
				miCut.setAction(actions.get(DefaultEditorKit.cutAction));
				miCopy.setAction(actions.get(DefaultEditorKit.copyAction));
				miPaste.setAction(actions.get(DefaultEditorKit.pasteAction));
			}
			if (comp instanceof JTree) {
				JTree tree = (JTree) comp;
				TreePath selectedPath = tree.getSelectionPath();
				DefaultMutableTreeNode node = null;
				try {
					node = (DefaultMutableTreeNode) selectedPath
							.getLastPathComponent();
				} catch (Exception e) {
					// ignore
				}
				if (node != null && node.isLeaf()) {
					Object dbObj = node.getUserObject();
					AbstractAction action = null;
					if (dbObj instanceof DbTable) {
						if (!(dbObj instanceof Scene)) {
							action = new TableCopyAction((DbTable) dbObj);
						}
					} else if (dbObj instanceof ThinLocation) {
						action = new TableCopyAction(
								((ThinLocation) dbObj).getLocation());
					}
					if (action != null) {
						miCopy.setAction(action);
						enableCopy = true;
					}
				}
			}
		}
		
		miCut.setText(I18N.getMsg("msg.common.cut"));
		miCut.setIcon(I18N.getIcon("icon.small.cut"));
		I18N.setMnemonic(miCut, KeyEvent.VK_T);
		SwingTools.setAccelerator(miCut, KeyEvent.VK_X, Event.CTRL_MASK);
		miCut.setEnabled(enableCut);
		menu.add(miCut);
		
		miCopy.setText(I18N.getMsg("msg.common.copy"));
		miCopy.setIcon(I18N.getIcon("icon.small.copy"));
		I18N.setMnemonic(miCopy, KeyEvent.VK_C);
		SwingTools.setAccelerator(miCopy, KeyEvent.VK_C, Event.CTRL_MASK);
		miCopy.setEnabled(enableCopy);
		menu.add(miCopy);
		
		miPaste.setText(I18N.getMsg("msg.common.paste"));
		miPaste.setIcon(I18N.getIcon("icon.small.paste"));
		I18N.setMnemonic(miPaste, KeyEvent.VK_P);
		SwingTools.setAccelerator(miPaste, KeyEvent.VK_V, Event.CTRL_MASK);
		miPaste.setEnabled(enablePaste);
		menu.add(miPaste);
	}
	
	public static void addCopyPasteToPopupMenu(JPopupMenu menu, JComponent comp) {
		HashMap<Object, Action> actions = SwingTools
				.createActionTable((JTextComponent) comp);
		Action cutAction = actions.get(DefaultEditorKit.cutAction);
		JMenuItem miCut = new JMenuItem(cutAction);
		miCut.setText(I18N.getMsg("msg.common.cut"));
		miCut.setIcon(I18N.getIcon("icon.small.cut"));
		menu.add(miCut);
		Action copyAction = actions.get(DefaultEditorKit.copyAction);
		JMenuItem miCopy = new JMenuItem(copyAction);
		miCopy.setText(I18N.getMsg("msg.common.copy"));
		miCopy.setIcon(I18N.getIcon("icon.small.copy"));
		menu.add(miCopy);
		Action pasteAction = actions.get(DefaultEditorKit.pasteAction);
		JMenuItem miPaste = new JMenuItem(pasteAction);
		miPaste.setText(I18N.getMsg("msg.common.paste"));
		miPaste.setIcon(I18N.getIcon("icon.small.paste"));
		menu.add(miPaste);
	}

	public static void addCopyToPopupMenu(JPopupMenu menu, JComponent comp) {
		HashMap<Object, Action> actions = SwingTools
				.createActionTable((JTextComponent) comp);
		Action copyAction = actions.get(DefaultEditorKit.copyAction);
		JMenuItem miCopy = new JMenuItem(copyAction);
		miCopy.setText(I18N.getMsg("msg.common.copy"));
		miCopy.setIcon(I18N.getIcon("icon.small.copy"));
		menu.add(miCopy);
	}

	public static HashMap<Object, Action> createActionTable(
			JTextComponent textComponent) {
		HashMap<Object, Action> actions = new HashMap<Object, Action>();
		Action[] actionsArray = textComponent.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
		return actions;
	}
	
	public static KeyStroke getKeyStrokeCopy() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK,
				false);
	}
	
	public static KeyStroke getKeyStrokeDelete() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
	}
	
	public static void showProfOnlyDialog() {		
		JOptionPane.showMessageDialog(MainFrame.getInstance(),
				I18N.getMsg("msg.pro.dlg.msg"),
				I18N.getMsg("msg.pro.dlg.title"),
				JOptionPane.WARNING_MESSAGE);
	}
	
	public static Dimension getPreferredDimension() {
		return new Dimension(getPreferredWidth(), getPreferredHeight());
	}

	public static int getPreferredHeight(Component comp) {
		return new Double(comp.getPreferredSize().getHeight()).intValue();
	}

	public static int getPreferredWidth(Component comp) {
		return new Double(comp.getPreferredSize().getWidth()).intValue();
	}

	public static int getPreferredWidth() {
		Dimension dim = getMainFrameSize();
		return dim.width - 20 > 750 ? 750 : dim.width - 20;
	}

	public static int getPreferredHeight() {
		return getPreferredHeight(0);
	}

	public static int getPreferredHeight(int maxHeight) {
		Dimension dim = SwingTools.getMainFrameSize();
		int calcHeight = dim.height - 80;
		if (maxHeight == 0) {
			return calcHeight;
		}
		return calcHeight > maxHeight ? maxHeight : calcHeight;
	}

	public static void printFreeMemory() {
		long memory = Runtime.getRuntime().freeMemory();
		System.out.println("Free Memory (KB): " + memory / 1024);
	}

	public static Color getTableSelectionBackgroundColor() {
		return UIManager.getColor("Table.selectionBackground");
	}

	public static Color getTableBackgroundColor() {
		return getTableBackgroundColor(false);
	}

	public static Color getTableBackgroundColor(boolean colored) {
		if (colored) {
			return new Color(0xf4f4f4);
		}
		return UIManager.getColor("Table.background");
	}

	public static Color getTableHeaderColor() {
		return UIManager.getColor("TableHeader.background");
	}

	public static void setForcedSize(Component comp, Dimension dim) {
		comp.setMinimumSize(dim);
		comp.setPreferredSize(dim);
		comp.setMaximumSize(dim);
	}

//	public static void setForcedHeight(Component comp, int height) {
//		Dimension dim = new 
//		comp.setMinimumSize(dim);
//		comp.setPreferredSize(dim);
//		comp.setMaximumSize(dim);
//	}

	public static void printUIDefaults() {
		UIDefaults uiDefaults = UIManager.getDefaults();
		Enumeration<Object> e = uiDefaults.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object val = uiDefaults.get(key);
			System.out.println("[" + key.toString() + "]:["
					+ (null != val ? val.toString() : "(null)") + "]");
		}
	}

	public static JLabel createTimestampLabel() {
		Date date = new Date();
		String dateStr = FastDateFormat.getDateInstance(FastDateFormat.MEDIUM)
				.format(date);
		String timeStr = FastDateFormat.getTimeInstance(FastDateFormat.MEDIUM)
				.format(date);
		return new JLabel(dateStr + " - " + timeStr);
	}

	public static void addCtrlEnterAction(JComponent comp, AbstractAction action) {
		InputMap inputMap = comp.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				InputEvent.CTRL_DOWN_MASK), action);
	}

	public static JPanel createNotesPanel(JTextArea taNotes) {
		MigLayout layout = new MigLayout("fill", "", "[top]");
		JPanel panel = new JPanel(layout);
		taNotes.setLineWrap(true);
		taNotes.setWrapStyleWord(true);
		taNotes.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		JScrollPane scroller = new JScrollPane(taNotes);
		scroller.setPreferredSize(new Dimension(400, 400));
		panel.add(scroller, "grow");
		return panel;
	}

	/**
	 * Creates a JSlider and ensure the given value is between min and max.
	 * 
	 * @param orientation
	 *            the orientation of the slider
	 * @param min
	 *            the minimum value of the slider
	 * @param max
	 *            the maximum value of the slider
	 * @param value
	 *            the initial value of the slider
	 * @return the JSlider
	 */
	public static JSlider createSafeSlider(int orientation, int min, int max,
			int value) {
		if (value < min) {
			value = min;
		} else if (value > max) {
			value = max;
		}
		return new JSlider(orientation, min, max, value);
	}

	/**
	 * Select all text in a {@link JComponent} if it is a {@link JTextField} or
	 * {@link JTextArea}.
	 * 
	 * @param comp
	 *            the component
	 */
	public static void selectAllText(JComponent comp) {
		if (comp instanceof JTextField) {
			JTextField tf = (JTextField) comp;
			tf.setSelectionStart(0);
			tf.setSelectionEnd(tf.getText().length());
		} else if (comp instanceof JTextArea) {
			JTextArea ta = (JTextArea) comp;
			ta.setSelectionStart(0);
			ta.setSelectionEnd(ta.getText().length());
		}
	}

	/**
	 * Flashes the given {@link Component} for 250 milliseconds.
	 * 
	 * @param comp
	 *            the component to flash
	 */
	public static void flashComponent(JComponent comp) {
		synchronized (flashIsRunning) {
			if (flashIsRunning) {
				return;
			}
			flashIsRunning = true;
			FlashThread flash = new FlashThread(comp);
			SwingUtilities.invokeLater(flash);
			FlashThread flash2 = new FlashThread(comp, true);
			Timer timer = new Timer(500, flash2);
			timer.setRepeats(false);
			timer.start();
		}
	}

	public static void flashEnded() {
		synchronized (flashIsRunning) {
			flashIsRunning = false;
		}
	}

	/**
	 * Gets a text file chooser. Only files with the extension ".txt" and
	 * directories are shown.
	 * 
	 * @return the file chooser
	 */
	public static JFileChooser getTextFileChooser() {
		final JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Text files", "txt");
		chooser.setFileFilter(filter);
		return chooser;
	}

	/**
	 * Gets the dimension of the screen.
	 * 
	 * @return the dimension of the screen
	 */
	public static Dimension getScreenSize() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		return d;
	}

	public static Dimension getMainFrameSize() {
		return MainFrame.getInstance().getSize();
	}

	/**
	 * Enables or disables all children of the given container.
	 * 
	 * @param container
	 *            the container
	 * @param enable
	 *            if true, the components are enabled, otherwise they are
	 *            disabled
	 */
	public static void enableContainerChildren(Container container,
			boolean enable) {
		for (Component comp : container.getComponents()) {
			try {
				comp.setEnabled(enable);
				((JComponent) comp).setOpaque(enable);
				if (comp instanceof Container) {
					enableContainerChildren((Container) comp, enable);
				}
			} catch (ClassCastException e) {
				// ignore component
				continue;
			}
		}
	}

	public static String getNiceFontName(Font font) {
		if (font == null) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		buf.append(font.getName());
		buf.append(", ");
		switch (font.getStyle()) {
		case Font.BOLD:
			buf.append("bold");
			break;
		case Font.ITALIC:
			buf.append("italic");
			break;
		case Font.PLAIN:
			buf.append("plain");
			break;
		}
		buf.append(", ");
		buf.append(font.getSize());
		return buf.toString();
	}

	public static String shortenString(String str, int length) {
		if (str.length() > length) {
			return StringUtils.left(str, length) + " ...";
		}
		return str;
	}

	public static Color getBackgroundColor() {
		return Color.white;
	}

	public static String getDayName(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		return sdf.format(date);
	}

	public static String getTimestamp(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return sdf.format(date);
	}

	public static Stroke getStorke() {
		return new BasicStroke(1);
	}

	public static Stroke getDotStroke() {
		int w = 1;
		float[] dash = { 1, 3 };
		float dash_phase = 1;
		return new BasicStroke(w, BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER, 10, dash, dash_phase);
	}

	public static Stroke getDotStroke2() {
		int w = 1;
		float[] dash = { 6, 3 };
		float dash_phase = 2;
		return new BasicStroke(w, BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER, 10, dash, dash_phase);
	}

	public static Stroke getDefaultStorke() {
		return new BasicStroke();
	}

	public static void addEnterAction(JComponent comp, Action action) {
		comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ENTER"), action);
		comp.getActionMap().put(action, action);
	}

	public static void addEscAction(JComponent comp, Action action) {
		comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ESCAPE"), action);
		comp.getActionMap().put(action, action);
	}
	
	public static Border getEtchedBorder() {
		return BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}

	public static void setLookAndFeel() {
		try {
			// get saved look and feel
			String lafStr = PrefManager.getInstance().getStringValue(
					Constants.Preference.LAF);
			LookAndFeel laf = LookAndFeel.valueOf(lafStr);
			setLookAndFeel(laf);
		} catch (java.lang.IllegalArgumentException e) {
			setLookAndFeel(LookAndFeel.system);
		}
	}

	/**
	 * Sets the look and feel.
	 * 
	 * @param lookAndFeel
	 *            The new look and feel to set.
	 * @see Constants.LookAndFeel
	 */
	public static void setLookAndFeel(LookAndFeel lookAndFeel) {
		try {
			String lafClassName = UIManager
					.getCrossPlatformLookAndFeelClassName();
			switch (lookAndFeel) {
			case cross:
				lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
				break;
			case system:
				lafClassName = UIManager.getSystemLookAndFeelClassName();
				break;
			case tiny:
				lafClassName = "de.muntjak.tinylookandfeel.TinyLookAndFeel";
				break;
			case tonic:
				lafClassName = "com.digitprop.tonic.TonicLookAndFeel";
				break;
			case substance:
				lafClassName = "org.jvnet.substance.SubstanceLookAndFeel";
				break;
//			case motif:
//				lafClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
//				break;
//			case nimbus:
//				lafClassName = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
//				break;
			default:
				lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
				break;
			}
			PrefManager.getInstance().setValue(Constants.Preference.LAF,
					lookAndFeel.name());
			UIManager.setLookAndFeel(lafClassName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets the default font for all Swing components.
	 * 
	 * eg. setUIFont(new javax.swing.plaf.FontUIResource
	 * ("Serif",Font.ITALIC,12));
	 * 
	 * @param f
	 */
	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	public static JPanel createMenuBarSpacer() {
		return createMenuBarSpacer(false);
	}

	public static JPanel createMenuBarSpacer(boolean linie) {
		MigLayout layout = new MigLayout("insets 0", "[1]");
		JPanel panel = new JPanel(layout);
		panel.setOpaque(false);
		JLabel label = new JLabel(" ");
		if (linie) {
			Border border = BorderFactory.createMatteBorder(0, 1, 0, 0,
					Color.gray);
			label.setBorder(border);
			panel.add(label, "center");
		}
		panel.setMaximumSize(new Dimension(2, 10));
		return panel;
	}

	public static boolean checkInputComponents(List<JComponent> componentlist) {
		boolean valid = true;
		for (JComponent comp : componentlist) {
			if (!checkInputComponent(comp)) {
				valid = false;
			}
		}
		return valid;
	}

	public static boolean checkInputComponent(JComponent comp) {
		if (comp.getInputVerifier().verify(comp)) {
			comp.setBackground(getWarningGreen());
			return true;
		}
		if (!(comp instanceof JDateChooser)) {
			comp.requestFocus();
		}
		comp.setBackground(getWarningRed());
		return false;
	}

	public static Color getWarningGreen() {
		return new Color(0xCCFFCC);
	}

	public static Color getWarningRed() {
		return new Color(0xFFD9BF);
	}

	public static Color getNiceRed() {
		return new Color(0xefe0e0);
	}

	public static Color getNiceGreen() {
		return new Color(0xa6ebaf);
	}

	public static Color getCharacterDefaultColor() {
		return new Color(0xFFc0c0);
	}

	public static Color getNiceYellow() {
		return new Color(0xefefe0);
	}

	public static Color getNiceBlue() {
		return new Color(0xC8C8FF);
	}

	public static Color getNiceDarkGray() {
		return new Color(0xdbdbdb);
	}

	public static Color getNiceGray() {
		return new Color(0xefefef);
	}

	public static void setWaitCursor() {
		MainFrame.getInstance().setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public static void setDefaultCursor() {
		MainFrame.getInstance().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public static void setWaitCursor(Component comp) {
		comp.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public static void setDefaultCursor(Component comp) {
		comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public static JLabel createHorizontalLineLabel() {
		MatteBorder mb = new MatteBorder(0, 0, 1, 0, Color.black);
		JLabel label = new JLabel();
		label.setBorder(mb);
		return label;
	}

	public static JLabel createVerticalLineLabel() {
		MatteBorder mb = new MatteBorder(0, 1, 0, 0, Color.black);
		JLabel label = new JLabel();
		label.setBorder(mb);
		return label;
	}

	public static CompoundBorder getCompoundBorder(String text) {
		return BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(text), BorderFactory.createEmptyBorder(5,
				5, 5, 5));
	}

	public static List<Component> findComponentsByClass(
			Container rootComponent, Class<? extends Component> cname,
			List<Component> res) {
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent)
					.getComponents();
			for (Component comp : components) {
				if (cname.isInstance(comp)) {
					res.add(comp);
				}
				if (comp instanceof Container) {
					findComponentsByClass((Container) comp, cname, res);
				}
			}
		}
		return res;
	}
	
	public static Component findComponentByName(Component rootComponent,
			String name) {
		if (rootComponent.getName() != null) {
			if (rootComponent.getName().equals(name)) {
				return rootComponent;
			}
		}
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent)
					.getComponents();
			for (int i = 0; i < components.length; ++i) {
				Component comp = findComponentByName(components[i], name);
				if (comp != null) {
					return comp;
				}
			}
		}
		return null;
	}

	public static Component printComponentHierarchy(Component rootComponent) {
		return printComponentHierarchy(rootComponent, -1);
	}

	private static Component printComponentHierarchy(Component rootComponent,
			int level) {
		++level;
		System.out.println(StringUtils.repeat("    ", level) + level + ":"
				+ formateComponentInfosToPrint(rootComponent));
		if (rootComponent instanceof Container) {
			Component[] components = ((Container) rootComponent)
					.getComponents();
			for (int i = 0; i < components.length; ++i) {
				Component comp = printComponentHierarchy(components[i], level);
				if (comp != null) {
					return comp;
				}
			}
		}
		return null;
	}

	public static String formateComponentInfosToPrint(Component comp) {
		StringBuffer buf = new StringBuffer();
		buf.append(comp.getClass().getSimpleName());
		buf.append(" [");
		buf.append(comp.getName());
		if (comp instanceof JLabel) {
			buf.append(",\"" + ((JLabel) comp).getText() + "\"");
		}
		buf.append(",");
		buf.append(comp.getClass().getName());
		buf.append(",");
		buf.append(comp.isVisible() ? "visible" : "not visible");
		buf.append(",");
		buf.append(comp.isValid() ? "valid" : "invalid");
		buf.append("]");
		return buf.toString();
	}

	public static int getComponentIndex(Container container, Component component) {
		int index = 0;
		for (Component comp : container.getComponents()) {
			if (comp == component) {
				return index;
			}
			++index;
		}
		return -1;
	}

	public static Border getBorderDefault() {
		return getBorderDefault(1);
	}

	public static Border getBorderDefault(int thickness) {
		return BorderFactory.createLineBorder(Color.black, thickness);
	}

	public static Border getBorderRed() {
		return BorderFactory.createLineBorder(Color.red);
	}

	public static Border getBorderBlue() {
		return getBorderBlue(1);
	}

	public static Border getBorderBlue(int thickness) {
		return BorderFactory.createLineBorder(Color.blue, thickness);
	}

	public static Border getBorderGray() {
		return BorderFactory.createLineBorder(Color.gray);
	}

	public static Border getBorderLightGray() {
		return BorderFactory.createLineBorder(Color.lightGray);
	}

	public static Border getBorderDot() {
		return new DotBorder();
	}

	/**
	 * Disposes all opened dialogs and reset their state.
	 * 
	 * @see FindDateDialog
	 * @see FindChapterDialog
	 */
	public static void disposeOpenedDialogs() {
		Window[] owned = MainFrame.getInstance().getOwnedWindows();
		Window[] ownerless = MainFrame.getOwnerlessWindows();
		Window[] all = (Window[]) org.apache.commons.lang3.ArrayUtils.addAll(owned, ownerless);
		for (Window window : all) {
			if (window instanceof MainFrame) {
				// don't close myself
				continue;
			}
			if (window instanceof FindChapterDialog) {
				((FindChapterDialog) window).reset();
				window.dispose();
				continue;
			}
			if (window instanceof FindDateDialog) {
				((FindDateDialog) window).reset();
				window.dispose();
				continue;
			}
			if (window instanceof JFrame || window instanceof JDialog) {
				try {
					((AbstractListFrame) window).refresh();
				} catch (ClassCastException e) {
					// ignore
				}
				window.dispose();
				continue;
			}
		}
	}

	public static void disposeWindows() {
		Window[] owned = MainFrame.getInstance().getOwnedWindows();
		Window[] ownerless = MainFrame.getOwnerlessWindows();
		Window[] all = (Window[]) ArrayUtils.addAll(owned, ownerless);
		for (Window window : all) {
			if (window instanceof MainFrame) {
				// don't close myself
				continue;
			}
			window.dispose();
		}
	}

	public static Frame getFrame(String frameName) {
		Frame[] frames = Frame.getFrames();
		for (Frame frame : frames) {
			if (frame.getName() == frameName) {
				return frame;
			}
		}
		return null;
	}
	
	public static boolean showWindowIfOpened(Class<? extends Window> c) {
		Window[] owned = MainFrame.getInstance().getOwnedWindows();
		Window[] ownerless = MainFrame.getOwnerlessWindows();
		Window[] all = (Window[]) ArrayUtils.addAll(owned, ownerless);
		for (Window window : all) {
			try {
				if (Class.forName(c.getName()).isInstance(window)) {
					window.setVisible(true);
					return true;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static JFrame findParentFrame(JComponent c) {
		Component parent = c.getParent();
		while (!(parent instanceof JFrame) && (parent != null)) {
			parent = parent.getParent();
		}
		return (JFrame) parent;
	}

	public static void showFrame(JFrame frame, JFrame parent) {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocation(parent.getX()
				+ (parent.getWidth() - frame.getWidth()) / 2, parent.getY()
				+ (parent.getHeight() - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public static void showDialog(JDialog dlg, Component parent) {
		showDialog(dlg, parent, true);
	}

	public static void showDialog(JDialog dlg, Component parent,
			boolean resizable) {
		dlg.setResizable(resizable);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
	}

	public static void showModalDialog(JDialog dlg, Component parent) {
		showModalDialog(dlg, parent, true);
	}

	public static void showModalDialog(JDialog dlg, Component parent,
			boolean resizable) {
		dlg.setResizable(resizable);
		dlg.setModal(true);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
	}

	public static void showException(Exception e) {
		MigLayout layout = new MigLayout("wrap", "[grow,fill]",
				"[][grow,fill][]");

		boolean exit = false;
		JTextArea errorTA = new JTextArea();
		String msg = e.getMessage();
		if (msg != null) {
			if (msg.contains("Database may be already in use")) {
				if (messageInUse) {
					return;
				}
				try {
					errorTA.setText(I18N.getMsg("msg.error.db.in.use"));
				} catch (MissingResourceException e1) {
					errorTA.setText("File is already in use.");
				}
				messageInUse = true;
				exit = true;
			} else if (msg.contains("Value too long for column")) {
				try {
					errorTA.setText(I18N.getMsg("msg.error.db.value.too.long"));
				} catch (MissingResourceException e1) {
					errorTA.setText("Value too long for column. Shorten the text.");
				}
			} else if (msg.contains("The database is read only")) {
				if (messageReadonlyShown) {
					return;
				}
				try {
					errorTA.setText(I18N.getMsg("msg.error.db.read.only"));
				} catch (MissingResourceException e1) {
					errorTA.setText("File is read only. Some functions may not work correctly.");
				}
				messageReadonlyShown = true;
			} else {
				errorTA.setText(e.getLocalizedMessage());
			}
		} else {
			errorTA.setText(e.toString());
		}
		errorTA.setEditable(false);
		errorTA.setLineWrap(true);
		errorTA.setWrapStyleWord(true);

		JScrollPane errorSP = new JScrollPane(errorTA);

		final JDialog dlg = new JDialog();
		JLabel titleLb = new JLabel("An exception has occured:");
		JButton closeBt = new JButton();
		closeBt.setAction(new CloseAction(dlg, exit));
		if (exit) {
			try{
				closeBt.setText(I18N.getMsg("msg.common.exit"));
			}catch(MissingResourceException e3){
				closeBt.setText("Exit");
			}
		} else {
			try{
				closeBt.setText(I18N.getMsg("msg.common.close"));
			}catch(MissingResourceException e4){
				closeBt.setText("Close");
			}
		}

		dlg.setLayout(layout);
		dlg.setTitle("Exception");
		dlg.add(titleLb);
		dlg.add(errorSP);
		dlg.add(closeBt);
		dlg.setPreferredSize(new Dimension(400, 300));
		errorTA.setCaretPosition(0);

		if (MainFrame.getInstance() == null) {
			// the main frame may not be ready at this point in time
			JFrame frame = new JFrame("Exception");
			frame.setPreferredSize(new Dimension(100, 100));
			frame.pack();
			frame.setVisible(true);
			showModalDialog(dlg, frame, true);
		} else {
			showModalDialog(dlg, MainFrame.getInstance(), true);
		}
	}
}

@SuppressWarnings("serial")
class CloseAction extends AbstractAction {
	JDialog dlg;
	boolean exit = false;

	CloseAction(JDialog dlg, boolean exit) {
		this.dlg = dlg;
		this.exit = exit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dlg.dispose();
		if (this.exit) {
			PersistenceManager.getInstance().closeConnection();
			System.exit(0);
		}
	}
}
