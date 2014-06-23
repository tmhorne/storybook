package ch.intertec.storybook.playground.swing.undo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * Application demonstrating how multiple components can share
 * an undo manager and have all undos and redos set focus to
 * the affected JComponent.
 */
@SuppressWarnings("all")
public class UndoDemo extends JFrame {
    UndoMediator mediator;

    public UndoDemo() {
        super("Undo Demo");
        UndoMediator mediator = new UndoMediator();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("One", new DemoPanel(mediator, tabbedPane, "One"));
        tabbedPane.addTab("Two", new DemoPanel(mediator, tabbedPane, "Two"));
        getContentPane().add(tabbedPane);
        addKeyListener(mediator);
    }

    public static void main(String[] args) {
        JFrame frame = new UndoDemo();
        frame.setSize(600, 400);
     	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

@SuppressWarnings("all")
class DemoPanel extends JPanel {

    JLabel helpLabel = new JLabel("Ctrl+Z to Undo, Ctrl+Y to Redo");
    JTextArea area = new JTextArea();
    JList list = new JList(new DefaultListModel());
    JButton addToListButton = new JButton("Add");
    JButton removeFromListButton = new JButton("Remove");
    JCheckBox checkBox = new JCheckBox("Hello");
    JTextPane textPane = new JTextPane();
    JButton boldButton = new JButton("Bold");
    JButton italicButton = new JButton("Italic");
    JTable table = new JTable();
    JButton addToTableButton = new JButton("Add");
    JButton removeFromTableButton = new JButton("Remove");

    /**
     *
     */
    
    public DemoPanel(final UndoMediator mediator,
                     final JTabbedPane tabbedPane,
                     final String tabName) {
        setLayout(new BorderLayout());
	    add(helpLabel, "North");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        // An example JTextArea with a PlainDocument

        panel.add(new JScrollPane(area));
	    mediator.registerDocument(area.getDocument(), area, tabbedPane, tabName);
	    area.addKeyListener(mediator);

	    // An example JList

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(new JScrollPane(list), "Center");
        panel2.add(addToListButton, "West");
        addToListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel)list.getModel();
                String newEntry = String.valueOf(Math.random()*100);
                mediator.addToListModel(model, model.getSize(), newEntry,
                    list, tabbedPane, tabName);
            }
        });
        panel2.add(removeFromListButton, "East");
        removeFromListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel)list.getModel();
                int index = list.getSelectedIndex();
                if (index != -1) {
                    mediator.removeFromListModel(model, index,
                        list, tabbedPane, tabName);
                }
            }
        });
        panel.add(panel2);

        // An example JTextPane

        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        panel3.add(new JScrollPane(textPane), "Center");
	    mediator.registerDocument(textPane.getDocument(), textPane, tabbedPane, tabName);
        panel3.add(boldButton, "West");
        boldButton.addActionListener(new StyledEditorKit.BoldAction());
        panel3.add(italicButton, "East");
        italicButton.addActionListener(new StyledEditorKit.ItalicAction());
        panel.add(panel3);

        // An example JTable

        JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout());
        panel4.add(new JScrollPane(table), "Center");
        panel4.add(addToTableButton, "West");
        addToTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)table.getModel();
                Vector data = new Vector(model.getColumnCount());
                for (int i = 0; i < model.getColumnCount(); i++) {
                    data.add(String.valueOf((int)(Math.random()*10000)));
                }
                mediator.addRowToTableModel(model, model.getRowCount(), data,
                    table, tabbedPane, tabName);
            }
        });
        panel4.add(removeFromTableButton, "East");
        removeFromTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel)table.getModel();
                ListSelectionModel selectionModel = table.getSelectionModel();
                int index = selectionModel.getMinSelectionIndex();
                if (index != -1) {
                    mediator.removeRowFromTableModel(model, index,
                        table, tabbedPane, tabName);
                }
            }
        });
        table.setModel(new DefaultTableModel(
            new String[]{"a", "bee", "sea", "dee", "eeeee"}, 0));
        panel.add(panel4);

	    add(panel, "Center");

	    // An example JCheckBox

	    add(checkBox, "South");
	    mediator.registerCheckBoxForUndoableSelects(checkBox, tabbedPane, tabName);
    }
}

/**
 * A mediator object that coordinates undo and redo requests among
 * multiple components; it also provides direct methods for modifying
 * components that record the effects in edit objects which are
 * automatically registered for undo/redo.  Use this class as
 * follows:
 *
 * 1. For components whose models are documents, such as JTextFields,
 *    JTextAreas, JTextPanes, etc. call
 *
 *    registerDocument(document, focusInfo)
 *
 *    Swing already has documents generating their own undoable edit
 *    events, therefore all one has to do is register a document.
 *    For other objects, we can't get away with this.  We need a way
 *    for these other objects to generate UndoableEditEvents.
 *
 * 2. For JLists, we can't just register a model, since list models
 *    don't automatically fire UndoableEditEvents.  Instead, clients
 *    have to call special methods to update the model that will
 *    then fire these events.
 *
 *    addToListModel(model, index, data, focusInfo)
 *    removeFromListModel(model, index, focusInfo)
 *
 * 3. JTables work like JLists:
 *
 *    addRowToTableModel(model, index, data, focusInfo)
 *    removeRowFromTableModel(model, index, focusInfo)
 *
 * 4. Checkboxes are a little different since it's not the model
 *    that we need to examine events on; it's the checkbox itself.
 *    Fortunately, we can register the checkbox with the mediator and
 *    examine the model on the fly.  Clients call:
 *
 *    registerCheckBoxForUndoableSelects(checkbox, focusInfo)
 */
@SuppressWarnings("all")
class UndoMediator implements KeyListener {

    /**
     * The one and only undo manager.  This is the manager to which
     * you add edit objects when actions occur, and on which you
     * actually call undo and redo.
     */
    private UndoManager manager = new UndoManager();

    /**
     * Ugh - hard code the undo limit since this is a throw away demo.
     */
    public UndoMediator() {
        manager.setLimit(1000);
    }

    /**
     * Super convenient method for taking advantage of the fact
     * that documents always fire undoable edit events: this
     * method simply registers a document with the mediator
     * so that all undoable edit events fired by the
     * document will be handled here automatically.
     */
    public void registerDocument(Document document,
                                 final JComponent component,
                                 final JTabbedPane tabbedPane,
                                 final String tabName) {
        document.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                System.out.println(e.getEdit());
            	manager.addEdit(new DocumentEdit(e.getEdit(),
            	    new FocusInfo(component, tabbedPane, tabName)));
            }
        });
    }

    /**
     *
     */
    public void registerCheckBoxForUndoableSelects(
            final JCheckBox checkBox,
            final JTabbedPane tabbedPane,
            final String tabName) {
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonModel model = checkBox.getModel();
                manager.addEdit(new ButtonToggleEdit(
                    model, model.isSelected(),
                    new FocusInfo(checkBox, tabbedPane, tabName)));
            }
        });
    }

    // Utilities for lists and comboboxes.

    /**
     * Adds an item to a DefaultListModel and automatically records the
     * edit so that it can be undone or redone.
     */
    public void addToListModel(DefaultListModel model,
                               int index, Object element,
                               final JComponent component,
                               final JTabbedPane tabbedPane,
                               final String tabName) {
        manager.addEdit(new ListAddEdit(
            model, index, element,
            new FocusInfo(component, tabbedPane, tabName)));
        model.add(index, element);
    }

    /**
     * Removes an item to a DefaultListModel and automatically records the
     * edit so that it can be undone or redone.
     */
    public void removeFromListModel(DefaultListModel model,
                               int index,
                               final JComponent component,
                               final JTabbedPane tabbedPane,
                               final String tabName) {
        Object element = model.get(index);
        manager.addEdit(new ListRemoveEdit(
            model, index, element,
            new FocusInfo(component, tabbedPane, tabName)));
        model.remove(index);
    }

    public void addRowToTableModel(DefaultTableModel model,
                                   int index, Vector data,
            JTable table, JTabbedPane tabbedPane, String tabName) {
        manager.addEdit(new TableAddRowEdit(model, index, data,
            new FocusInfo(table, tabbedPane, tabName)));
        model.insertRow(index, data);
    }

    public void removeRowFromTableModel(DefaultTableModel model, int index,
            JTable table, JTabbedPane tabbedPane, String tabName) {
        Vector data = new Vector(model.getColumnCount());
        // Believe it or not, there is NO method in the DefaultTableModel
        // class to obtain the value of a row!  We have to get it the
        // hard way.
        for (int i = 0; i < model.getColumnCount(); i++) {
            data.add(model.getValueAt(index, i));
        }
        manager.addEdit(new TableRemoveRowEdit(model, index, data,
            new FocusInfo(table, tabbedPane, tabName)));
        model.removeRow(index);
    }

    // KeyListener methods.  Allows Ctrl+Z and Ctrl+Y to be handled.

    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_Z) && (e.isControlDown())) {
            try {
                manager.undo();
	        } catch(CannotUndoException cue) {
	            Toolkit.getDefaultToolkit().beep();
	        }
 	    }
  	    if ((e.getKeyCode() == KeyEvent.VK_Y) && (e.isControlDown())) {
 	        try {
	            manager.redo();
	        } catch(CannotRedoException cue) {
	            Toolkit.getDefaultToolkit().beep();
	        }
	    }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    /**
     * An inner class for collecting together information necessary to
     * focus a component that may be on a different tabbed pane than
     * the one that is currently visible.  The class is fairly simple:
     * it contains only (1) a component that you want to give focus to,
     * and (2) the tabbed pane and tab name, if any, that it is on.
     * Note that it is not necessary to have a tabbed pane at all,
     * but if you do, everything will be taken care of.
     */
    public static class FocusInfo {
        private JComponent component;
        private JTabbedPane tabbedPane;
        private String tabName;

        public FocusInfo(JComponent c, JTabbedPane p, String s) {
            this.component = c;
            this.tabbedPane = p;
            this.tabName = s;
        }

        public FocusInfo(JComponent c) {
            this(c, null, null);
        }

        /**
         * Sets the focus to the desired component, after selecting the
         * desired tab, if necessary.
         */
        public void doFocus() {
            if (tabbedPane != null) {
                int index = tabbedPane.indexOfTab(tabName);
                if (index != -1) {
                    if (index != tabbedPane.getSelectedIndex()) {
                        tabbedPane.setSelectedIndex(index);
                    }
                }
            }
            if (component != null) {
                component.requestFocus();
            }
        }
    }
}

//
// The edit objects.
//

/**
 * An edit object that stores a component for refocusing to.
 * Make subclasses of this class and arrange for their undo
 * and redo methods to call super.undo() and super.redo().
 */
@SuppressWarnings("serial")
class ComponentAwareEdit extends AbstractUndoableEdit {
    private UndoMediator.FocusInfo focusInfo;

    public ComponentAwareEdit(UndoMediator.FocusInfo focusInfo) {
        this.focusInfo = focusInfo;
    }

    public void undo() throws CannotUndoException {
        focusInfo.doFocus();
    }

    public void redo() throws CannotRedoException {
        focusInfo.doFocus();
    }

    public boolean canUndo() {return true;}
    public boolean canRedo() {return true;}
}

/**
 * An edit object for adding an item to a DefaultListModel.
 */
@SuppressWarnings("serial")
class ListAddEdit extends ComponentAwareEdit {
    private DefaultListModel model;
    private int index;
    private Object element;

    public ListAddEdit(DefaultListModel model,
                       int index, Object element,
                       UndoMediator.FocusInfo focusInfo) {
        super(focusInfo);
        this.model = model;
        this.index = index;
        this.element = element;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        model.removeElementAt(index);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        model.insertElementAt(element, index);
    }
}

/**
 * An edit object for removing an item from a DefaultListModel.
 */
@SuppressWarnings("serial")
class ListRemoveEdit extends ComponentAwareEdit {
    private DefaultListModel model;
    private int index;
    private Object element;

    public ListRemoveEdit(DefaultListModel model,
                       int index, Object element,
                       UndoMediator.FocusInfo focusInfo) {
        super(focusInfo);
        this.model = model;
        this.index = index;
        this.element = element;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        model.insertElementAt(element, index);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        model.removeElementAt(index);
    }
}

/**
 * An edit object for adding an item to a DefaultTableModel.
 */
@SuppressWarnings("all")
class TableAddRowEdit extends ComponentAwareEdit {
    private DefaultTableModel model;
    private int index;
    private Vector data;

    public TableAddRowEdit(DefaultTableModel model,
                           int index, Vector data,
                           UndoMediator.FocusInfo focusInfo) {
        super(focusInfo);
        this.model = model;
        this.index = index;
        this.data = data;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        model.removeRow(index);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        model.insertRow(index, data);
    }
}

/**
 * An edit object for removing an item from a DefaultTableModel.
 */
@SuppressWarnings("all")
class TableRemoveRowEdit extends ComponentAwareEdit {
    private DefaultTableModel model;
    private int index;
    private Vector data;

    public TableRemoveRowEdit(DefaultTableModel model,
                              int index, Vector data,
                              UndoMediator.FocusInfo focusInfo) {
        super(focusInfo);
        this.model = model;
        this.index = index;
        this.data = data;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        model.insertRow(index, data);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        model.removeRow(index);
    }
}

/**
 * An edit object for toggling the selected state of a button,
 * checkbox, etc.
 */
@SuppressWarnings("serial")
class ButtonToggleEdit extends ComponentAwareEdit {
    private ButtonModel model;
    private boolean selected;

    public ButtonToggleEdit(ButtonModel model,
                            boolean selected,
                            UndoMediator.FocusInfo focusInfo) {
        super(focusInfo);
        this.model = model;
        this.selected = selected;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        model.setSelected(!selected);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        model.setSelected(selected);
    }
}

/**
 * A wrapper edit object that encapsulates another edit.  The idea is
 * that whenever this edit is asked to be undone or redone, it first
 * sets focus as directed by a FocusInfo object then undoes or redos by
 * delegating to the internal edit object.
 */
@SuppressWarnings("serial")
class DocumentEdit extends ComponentAwareEdit {
    private UndoableEdit edit;

    public DocumentEdit(UndoableEdit edit,
                        UndoMediator.FocusInfo focusInfo) {
        super(focusInfo);
        this.edit = edit;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        edit.undo();
    }

    public void redo() throws CannotRedoException {
        super.redo();
        edit.redo();
    }
}
