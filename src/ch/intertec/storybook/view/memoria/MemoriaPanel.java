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

package ch.intertec.storybook.view.memoria;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import ch.intertec.storybook.action.ActionManager.SbAction;
import ch.intertec.storybook.action.ActionRegistry;
import ch.intertec.storybook.action.TableNewAction;
import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.EmptyScene;
import ch.intertec.storybook.model.Internal;
import ch.intertec.storybook.model.InternalPeer;
import ch.intertec.storybook.model.Item;
import ch.intertec.storybook.model.ItemLink;
import ch.intertec.storybook.model.ItemLinkPeer;
import ch.intertec.storybook.model.ItemPeer;
import ch.intertec.storybook.model.Location;
import ch.intertec.storybook.model.LocationPeer;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.Period;
import ch.intertec.storybook.model.SbCharacter;
import ch.intertec.storybook.model.SbCharacterPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.SceneLinkLocation;
import ch.intertec.storybook.model.SceneLinkLocationPeer;
import ch.intertec.storybook.model.SceneLinkSbCharacter;
import ch.intertec.storybook.model.SceneLinkSbCharacterPeer;
import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.model.Strand;
import ch.intertec.storybook.model.Tag;
import ch.intertec.storybook.model.TagLink;
import ch.intertec.storybook.model.TagLinkPeer;
import ch.intertec.storybook.model.TagPeer;
import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ScreenImage;
import ch.intertec.storybook.toolkit.Constants.IconSize;
import ch.intertec.storybook.toolkit.filefilter.PNGFileFilter;
import ch.intertec.storybook.toolkit.swing.EmptyIcon;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.view.IRefreshable;
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

@SuppressWarnings("serial")
public class MemoriaPanel extends JPanel implements ActionListener,
		ChangeListener, IRefreshable, ItemListener {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(MemoriaPanel.class);
	
	private DelegateForest<DbTable, Integer> graph;
	private VisualizationViewer<DbTable, Integer> vv;
	private TreeLayout<DbTable, Integer> treeLayout;
	private BalloonLayout<DbTable, Integer> balloonLayout;
	private GraphZoomScrollPane graphPanel;
	private Map<DbTable, String> labelMap;
	private Map<DbTable, Icon> iconMap;
	private String povSourceName;
	private int povId;
	private DbTable povDbObj;
	private int graphIndex;
	private ScalingControl scaler;

	private Scene sceneVertex;
	private String sceneVertexTitle;
	private SbCharacter characterVertex;
	private Location locationVertex;
	private String locationVertexTitle;
	private Tag tagVertex;
	private boolean showTagVertex = true;
	private Tag involvedTagVertex;

	private List<Scene> sceneList;
	private List<Integer> sceneIds;
	private Set<Tag> involvedTags;

	boolean showBalloonLayout = true;

	private JComboBox sceneCombo;
	private JComboBox characterCombo;
	private JComboBox locationCombo;
	private JComboBox tagCombo;
	private JComboBox itemCombo;

	private enum ComboType {
		SCENE, CHARACTER, LOCATION, TAG, ITEM
	}
	
	private Date chosenDate;
	private Date oldDate;
	private DateSlider dateSlider;
	private JButton btDecDate;
	private JButton btIncDate;
	private JComboBox dateCombo;
	
	private JCheckBox cbAutoRefresh;

	private Icon womanIconMedium;
	private Icon womanIconLarge;
	private Icon manIconMedium;
	private Icon manIconLarge;
	private Icon alienIconMedium;
	private Icon characterIconLarge;
	private Icon locationIconMedium;
	private Icon locationIconLarge;
	private Icon sceneIconMedium;
	private Icon sceneIconLarge;
	private Icon itemIconMedium;
	private Icon itemIconLarge;
	private Icon tagIconMedium;
	private Icon tagIconLarge;
	private Icon emptyIcon;
	
	private boolean processActionListener = true;
	
	public MemoriaPanel() {
		super();
		// PCSDispatcher pcs = PCSDispatcher.getInstance();
		// pcs.addPropertyChangeListener(Property.SCENE, this);
		// pcs.addPropertyChangeListener(Property.CHAPTER, this);

		manIconMedium = I18N.getIcon("icon.medium.man");
		womanIconMedium = I18N.getIcon("icon.medium.woman");
		alienIconMedium = I18N.getIcon("icon.medium.alien");
		sceneIconMedium = I18N.getIcon("icon.medium.scene");
		locationIconMedium = I18N.getIcon("icon.medium.location");
		tagIconMedium = I18N.getIcon("icon.medium.tag");
		itemIconMedium = I18N.getIcon("icon.medium.item");

		characterIconLarge = I18N.getIcon("icon.large.character");
		manIconLarge = I18N.getIcon("icon.large.man");
		womanIconLarge = I18N.getIcon("icon.large.woman");
		sceneIconLarge = I18N.getIcon("icon.large.scene");
		locationIconLarge = I18N.getIcon("icon.large.location");
		tagIconLarge = I18N.getIcon("icon.large.tag");
		itemIconLarge = I18N.getIcon("icon.large.item");

		emptyIcon = new EmptyIcon();

		chosenDate = new Date(0);
		povSourceName = "";

		sceneList = new ArrayList<Scene>();
		sceneIds = new ArrayList<Integer>();
		involvedTags = new HashSet<Tag>();
		
		scaler = new CrossoverScalingControl();
		
		initGUI();
	}

	public void initGUI() {
		MigLayout layout = new MigLayout(
				"flowy,fill",
				"[]",
				"[][grow]");
		setLayout(layout);
		setBackground(SwingTools.getBackgroundColor());
		
		// layout
		add(createControlPanel(), "grow");
		initGraph();
		add(graphPanel, "grow");
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel();

		MigLayout layout = new MigLayout(
				"flowx,fill",
				"[]",
				"[][]20[]");
		panel.setLayout(layout);

		// table object combos
		JLabel lbScene = new JLabel(I18N.getIcon("icon.small.scene"));
		sceneCombo = createComboBox(
				"SceneCombo",
				new EmptyScene(),
				ScenePeer.doSelectAll(ScenePeer.Order.BY_CHAPTER_AND_SCENE_NUMBER),
				isChosen(ComboType.SCENE));
		JLabel lbCharachter = new JLabel(I18N.getIcon("icon.small.character"));
		characterCombo = createComboBox("CharacterCombo", new SbCharacter(),
				SbCharacterPeer.doSelectAll(false),
				isChosen(ComboType.CHARACTER));
		JLabel lbLocation = new JLabel(I18N.getIcon("icon.small.location"));
		locationCombo = createComboBox("LocationCombo", new Location(),
				LocationPeer.doSelectAll(), isChosen(ComboType.LOCATION));
		JLabel lbTag = new JLabel(I18N.getIcon("icon.small.tag"));
		tagCombo = createComboBox("TagCombo", new Tag(), TagPeer.doSelectAll(),
				isChosen(ComboType.TAG));
		JLabel lbItem = new JLabel(I18N.getIcon("icon.small.item"));
		itemCombo = createComboBox("ItemCombo", new Item(),
				ItemPeer.doSelectAll(), isChosen(ComboType.ITEM));
		
		// auto refresh
		cbAutoRefresh = new JCheckBox();
		cbAutoRefresh.setText(I18N.getMsg("msg.graph.auto.refresh"));
		
		// presentation
		ButtonGroup bgPresentation = new ButtonGroup();
		JRadioButton rbBallon = new JRadioButton(
				I18N.getMsg("msg.graph.pres.balloon"));
		rbBallon.addItemListener(this);
		bgPresentation.add(rbBallon);
		JRadioButton rbTree = new JRadioButton(
				I18N.getMsg("msg.graph.pres.tree"));
		bgPresentation.add(rbTree);
		try {
			Internal internal = InternalPeer
					.doSelectByKey(Constants.ProjectSetting.MEMORIA_TREE);
			boolean presTree = internal.getBooleanValue();
			if (presTree) {
				rbTree.setSelected(true);
			} else {
				rbBallon.setSelected(true);
			}
		} catch (Exception e) {
			rbBallon.setSelected(true);
		}
		showBalloonLayout = rbBallon.isSelected();
		makeLayoutTransition();
		
		// date slider
		dateSlider = new DateSlider(JSlider.HORIZONTAL);
		TreeSet<Date> dateSet = ScenePeer.doSelectDistinctDate(false);
		dateSlider.setDates(new ArrayList<Date>(dateSet));
		dateSlider.addChangeListener(this);
		btIncDate = new JButton(I18N.getIcon("icon.small.arrow.right"));
		btIncDate.setToolTipText(I18N.getMsg("msg.graph.inc.date"));
		btIncDate.addActionListener(getIncDateAction());
		btDecDate = new JButton(I18N.getIcon("icon.small.arrow.left"));
		btDecDate.setToolTipText(I18N.getMsg("msg.graph.dec.date"));
		btDecDate.addActionListener(getDecDateAction());

		// date combo
		dateCombo = new JComboBox();
		for (Date date : dateSet) {
			dateCombo.addItem(date);
		}
		dateCombo.setName("DateCombo");
		dateCombo.setMaximumRowCount(15);
		dateCombo.addActionListener(this);
		
		// layout
		panel.add(lbScene);
		panel.add(sceneCombo, "push");
		panel.add(new JLabel(I18N.getMsgColon("msg.graph.presentation")));
		panel.add(rbBallon);
		panel.add(rbTree);
		panel.add(cbAutoRefresh);

		panel.add(lbCharachter, "newline,span,split 9");
		panel.add(characterCombo);
		panel.add(lbLocation, "gap 20");
		panel.add(locationCombo);
		panel.add(lbTag, "gap 20");
		panel.add(tagCombo);
		panel.add(lbItem, "gap 20");
		panel.add(itemCombo);
		panel.add(dateCombo, "gap push");
		
		panel.add(btDecDate, "newline,span,split 3");
		panel.add(dateSlider, "growx");
		panel.add(btIncDate);

		return panel;
	}

	private void refreshControlPanel() {
		// refresh combo boxes
		List<Scene> sceneList = ScenePeer
				.doSelectAll(ScenePeer.Order.BY_CHAPTER_AND_SCENE_NUMBER);
		refreshComboModel(sceneCombo, new EmptyScene(), sceneList,
				isChosen(ComboType.SCENE));

		List<SbCharacter> chList = SbCharacterPeer.doSelectAll(false);
		refreshComboModel(characterCombo, new SbCharacter(), chList,
				isChosen(ComboType.CHARACTER));

		List<Location> locList = LocationPeer.doSelectAll();
		refreshComboModel(locationCombo, new Location(), locList,
				isChosen(ComboType.LOCATION));

		List<Tag> tagList = TagPeer.doSelectAll();
		refreshComboModel(tagCombo, new Tag(), tagList, isChosen(ComboType.TAG));

		List<Item> itemList = ItemPeer.doSelectAll();
		refreshComboModel(itemCombo, new Item(), itemList,
				isChosen(ComboType.ITEM));
		
		disableActionListener();
		
		// refresh date combo
		DefaultComboBoxModel model = (DefaultComboBoxModel) dateCombo
				.getModel();
		model.removeAllElements();
		TreeSet<Date> dateSet = ScenePeer.doSelectDistinctDate(false);
		for (Date date : dateSet) {
			dateCombo.addItem(date);
		}
		// refresh date slider
		dateSlider.setDates(new ArrayList<Date>(dateSet));
		
		enableActionListener();
	}
	
	private JPanel getThis() {
		return this;
	}

	private void makeLayoutTransition() {
		if (vv == null) {
			return;
		}

		LayoutTransition<DbTable, Integer> lt;
		if (showBalloonLayout) {
			lt = new LayoutTransition<DbTable, Integer>(vv, treeLayout,
					balloonLayout);
		} else {
			lt = new LayoutTransition<DbTable, Integer>(vv, balloonLayout,
					treeLayout);
		}
		Animator animator = new Animator(lt);
		animator.start();
		vv.repaint();
	}

	private void clearGraph() {
		try {
			if (graph == null) {
				graph = new DelegateForest<DbTable, Integer>();
				return;
			}
			Collection<DbTable> col = graph.getRoots();
			for (DbTable obj : col) {
				if (obj == null) {
					continue;
				}
				graph.removeVertex(obj);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			graph = new DelegateForest<DbTable, Integer>();
		}
	}

	private AbstractAction getIncDateAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if(dateSlider.isIncrementAvailable()){
					oldDate = dateSlider.getDate();
					dateSlider.inc();
					dateSlider.refresh(true);
				}
			}
		};
	}

	private AbstractAction getDecDateAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if(dateSlider.isDecrementAvailable()){
					oldDate = dateSlider.getDate();
					dateSlider.dec();
					dateSlider.refresh(false);
				}
			}
		};
	}

	public void zoomIn(){
		scaler.scale(vv, 1.1f, vv.getCenter());
	}

	public void zoomOut(){
		scaler.scale(vv, 1 / 1.1f, vv.getCenter());
	}

	public void export(){
//		try {
//			File file = new File("/home/martin/tmp/exp.png");
//			BufferedImage bi = new BufferedImage(1000,
//					1000, BufferedImage.TYPE_INT_RGB);
//			Graphics2D graphics = bi.createGraphics();
////			vv.paint(graphics);
//			graphPanel.paintAll(graphics);
//			graphics.dispose();
//			ImageIO.write(bi, "png", file);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		try {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new PNGFileFilter());
			int ret = fc.showOpenDialog(getThis());
			if (ret == JFileChooser.CANCEL_OPTION) {
				return;
			}
			File file = fc.getSelectedFile();
			if (!file.getName().endsWith(".png")) {
				file = new File(file.getPath() + ".png");
			}
			ScreenImage.createImage(graphPanel, file.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JComboBox createComboBox(String objName, DbTable emptyObj,
			List<? extends DbTable> list, boolean isChoosen) {
		try {
			JComboBox cob = new JComboBox();
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			cob.setModel(model);
			refreshComboModel(cob, emptyObj, list, isChoosen);
			
			Dimension prefDim;
			int h = SwingTools.getPreferredHeight(cob);
			if (emptyObj instanceof Scene) {
				prefDim = new Dimension(600, h);
			} else {
				prefDim = new Dimension(160, h);
			}
			cob.setPreferredSize(prefDim);
			cob.setName(objName);
			cob.setMaximumRowCount(15);
			cob.addActionListener(this);
			return cob;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void refreshComboModel(JComboBox cob, DbTable emptyObj,
			List<? extends DbTable> list, boolean isChoosen) {
		try {
			processActionListener = false;
			DefaultComboBoxModel model = (DefaultComboBoxModel) cob
					.getModel();
			model.removeAllElements();
			model.addElement(emptyObj);
			DbTable selected = null;
			int i = 0;
			for (DbTable t : list) {
				if (isChoosen && t.getId() == this.povId) {
					selected = t;
				}
				t.setToStringUsedForList(true);
				model.addElement(t);
				++i;
			}
			if (isChoosen && selected != null) {
				cob.setSelectedItem(selected);
			}
			processActionListener = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initGraph() {
		labelMap = new HashMap<DbTable, String>();
		iconMap = new HashMap<DbTable, Icon>();
		graph = new DelegateForest<DbTable, Integer>();
		treeLayout = new TreeLayout<DbTable, Integer>(graph);
		balloonLayout = new BalloonLayout<DbTable, Integer>(graph);
		// radialLayout.setSize(new Dimension(800, 800));
		// vv = new VisualizationViewer<DbTable, Integer>(radialLayout,
		// new Dimension(800, 600));
		
		vv = new VisualizationViewer<DbTable, Integer>(balloonLayout);
		
		createGraph();

		// auto center trial
		// Collection<DbTable> vertices = graph.getVertices();
		// for (DbTable v : vertices) {
		// if (v instanceof Scene) {
		// Scene s = (Scene) v;
		// Point2D p = treeLayout.getCenter();
		// int x = new Double(p.getX()).intValue();
		// int y = new Double(p.getY()).intValue();
		// treeLayout.setLocation(s, new Point(x, y + 50));
		// }
		// }

		vv.setBackground(Color.white);
		// vv.getRenderContext().setLabelOffset(100);
		vv.getRenderContext().setEdgeShapeTransformer(
				new EdgeShape.Line<DbTable, Integer>());
		vv.getRenderContext().setVertexLabelTransformer(
		new ToStringLabeller<DbTable>());
//		vv.getRenderContext().setVertexLabelTransformer(new DbObjTransformer());
		// Positioner p = new BasicVertexLabelRenderer.OutsidePositioner();
		// vv.getRenderer().getVertexLabelRenderer().setPositioner(p);

		// add tool tips transformer
//		vv.setVertexToolTipTransformer(new ToStringLabeller<DbTable>());
		vv.setVertexToolTipTransformer(new DbObjTransformer());
		
		graphPanel = new GraphZoomScrollPane(vv);
		final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<Object, Object>();
		vv.setGraphMouse(graphMouse);
		graphMouse.add(new MemoriaGraphMouse(this));

		final Transformer<DbTable, String> vertexStringerImpl = new VertexStringerImpl<DbTable>(
				labelMap);
		vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl);

		// BasicVertexLabelRenderer<DbTable, Integer> vlr = new
		// BasicVertexLabelRenderer<DbTable, Integer>();
		// vv.getRenderContext().setVertexLabelRenderer((VertexLabelRenderer)
		// vlr);

		final VertexIconShapeTransformer<DbTable> vertexImageShapeFunction = new VertexIconShapeTransformer<DbTable>(
				new EllipseVertexShapeTransformer<DbTable>());
		final DefaultVertexIconTransformer<DbTable> vertexIconFunction = new DefaultVertexIconTransformer<DbTable>();
		vertexImageShapeFunction.setIconMap(iconMap);
		vertexIconFunction.setIconMap(iconMap);
		vv.getRenderContext().setVertexShapeTransformer(
				vertexImageShapeFunction);
		vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);
		
		// vv.setComponentPopupMenu(createPopupMenu());
	}

	private void createGraph() {
		clearGraph();
		if (this.povSourceName == sceneCombo.getName()) {
			createGraphForScenes();
		} else if (this.povSourceName == characterCombo.getName()) {
			createGraphForCharacters();
		} else if (this.povSourceName == locationCombo.getName()) {
			createGraphForLocations();
		} else if (this.povSourceName == itemCombo.getName()) {
			createGraphForItems();
		} else if (this.povSourceName == tagCombo.getName()) {
			createGraphForTags();
		}
		
		treeLayout = new TreeLayout<DbTable, Integer>(graph);
		balloonLayout = new BalloonLayout<DbTable, Integer>(graph);
		// needed by balloon layout
		balloonLayout.setSize(new Dimension(800, 600));
		balloonLayout.setGraph(graph);
		// radialLayout.initialize();
		if (showBalloonLayout) {
			vv.setGraphLayout(balloonLayout);
		} else {
			vv.setGraphLayout(treeLayout);
		}
		// scaleToLayout(new CrossoverScalingControl());
		vv.repaint();

		// radialLayout.setInitializer(vv.getGraphLayout());
	}
	
	@SuppressWarnings("unused")
	private boolean isNothingSelected() {
		if (povId <= -1) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private void showMessage(String text) {
		Graphics2D g2d = (Graphics2D) vv.getGraphics();
		if (g2d == null) {
			return;
		}
		Rectangle r = vv.getBounds();
		int cx = (int) r.getCenterX();
		int cy = (int) r.getCenterY();
		g2d.setColor(Color.lightGray);
		g2d.fillRect(cx - 200, cy - 20, 400, 40);
		g2d.setColor(Color.black);
		g2d.drawString(text, cx - 180, cy + 5);
	}
	
	@SuppressWarnings("unused")
	private void scaleToLayout(ScalingControl scaler) {
		Dimension vd = vv.getPreferredSize();
		if (vv.isShowing()) {
			vd = vv.getSize();
		}
		Dimension ld = vv.getGraphLayout().getSize();
		if (vd.equals(ld) == false) {
			scaler.scale(vv, (float) (vd.getWidth() / ld.getWidth()),
					new Point2D.Double());
		}
	}

	private void createGraphForScenes() {
		graphIndex = 0;
		Scene povScene = ScenePeer.doSelectById(povId);
		if (povScene == null) {
			return;
		}
		graph.addVertex(povScene);
		labelMap.put(povScene, povScene.toString());
		iconMap.put(povScene, sceneIconLarge);
		
		sceneVertexTitle = I18N.getMsg("msg.graph.scenes.same.date");
		initVertices(povScene);
		
		Set<Tag> tags = new HashSet<Tag>();
		Set<Item> items = new HashSet<Item>();
		
		Date sceneDate = povScene.getDate();
		// Period scenePeriod = new Period(sceneDate, sceneDate);
		
		// scenes of same date
		List<Scene> scenesSameDate = ScenePeer.doSelectByDate(sceneDate);
		for (Scene scene : scenesSameDate) {
			if (scene.getId() == povScene.getId()) {
				continue;
			}
			if (scene.getStrandId() != povScene.getStrandId()) {
				continue;
			}
			Scene sceneClone = scene.clone();
			graph.addVertex(sceneClone);
			labelMap.put(sceneClone, sceneClone.getGraphLabelText());
			iconMap.put(sceneClone, sceneIconMedium);
			graph.addEdge(graphIndex++, sceneVertex, sceneClone);
			// scene tags
			List<TagLink> tagLinks = TagLinkPeer
					.doSelectBySceneId(scene.getId());
			if (!tagLinks.isEmpty()) {
				for (TagLink tagLink : tagLinks) {
					if (tagLink.hasOnlyScene()) {
						involvedTags.add(tagLink.getTag());
					}
				}
			}
			// scene items
			List<ItemLink> itemLinks = ItemLinkPeer.doSelectBySceneId(scene
					.getId());
			if (!itemLinks.isEmpty()) {
				for (ItemLink itemLink : itemLinks) {
					if (itemLink.hasOnlyScene()) {
						involvedTags.add(itemLink.getItem());
					}
				}
			}
		}

		// characters
		List<SceneLinkSbCharacter> characters = SceneLinkSbCharacterPeer
				.doSelectBySceneId(povScene.getId());
		for (SceneLinkSbCharacter link : characters) {
			SbCharacter character = link.getCharacter();
			graph.addVertex(character);
			labelMap.put(character, character.toString());
			iconMap.put(character, getCharacterIcon(character, IconSize.MEDIUM));
			graph.addEdge(graphIndex++, characterVertex, character);
		}

		// locations
		List<SceneLinkLocation> locations = SceneLinkLocationPeer
				.doSelectBySceneId(povScene.getId());
		for (SceneLinkLocation link : locations) {
			Location location = link.getLocation();
			graph.addVertex(location);
			labelMap.put(location, location.toString());
			iconMap.put(location, locationIconMedium);
			graph.addEdge(graphIndex++, locationVertex, location);
		}
		
		// directly assigned tags
		List<TagLink> tagLinks = TagLinkPeer.doSelectBySceneId(povScene.getId());
		for (TagLink tagLink : tagLinks) {
			if (!tagLink.hasOnlyScene()) {
				continue;
			}
			Tag tag = tagLink.getTag();
			tags.add(tag);
		}

		// directly assigned items
		List<ItemLink> itemLinks = ItemLinkPeer.doSelectBySceneId(povScene
				.getId());
		for (ItemLink itemLink : itemLinks) {
			if (!itemLink.hasOnlyScene()) {
				continue;
			}
			Item item = itemLink.getItem();
			items.add(item);
		}

		// indirectly assigned tags
		List<TagLink> tagLinks2 = TagLinkPeer.doSelectAll();
		for (TagLink tagLink : tagLinks2) {
			// character assigned to scene?
			if (tagLink.hasCharacter()) {
				SbCharacter character = tagLink.getCharacter();
				List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer
						.doSelect(povScene, character);
				if (list.isEmpty()) {
					continue;
				}
			}

			// location assigned to scene?
			if (tagLink.hasLocation()) {
				if (tagLink.hasStartScene() && !tagLink.hasEndScene()) {
					if (tagLink.getStartSceneId() != povScene.getId()) {
						continue;
					}
				} else {
					Period p = tagLink.getPeriod();
					if (p != null) {
						if (!p.isInside(sceneDate)) {
							continue;
						}
					}
				}
				if (tagLink.hasLocationOrCharacter()) {
					Location location = tagLink.getLocation();
					List<SceneLinkLocation> list = SceneLinkLocationPeer
							.doSelect(povScene, location);
					if (list.isEmpty()) {
						continue;
					}
				}
			}

			if (tagLink.hasOnlyScene()) {
				if (!tagLink.hasEndScene()) {
					continue;
				}
				// not same strand?
				Scene scene = tagLink.getScene();
				if (scene.getStrandId() != povScene.getStrandId()) {
					continue;
				}
				// not inside period?
				Period p = tagLink.getPeriod();
				if (p != null) {
					if (!p.isInside(sceneDate)) {
						continue;
					}
				}
			}
			
			// already in "tags"?
			Tag tag = tagLink.getTag();
			if (tag == null) {
				continue;
			}
			boolean found = false;
			for (Tag t : tags) {
				if (t == null) {
					continue;
				}
				if (t.getId() == tag.getId()) {
					found = true;
					break;
				}
			}
			if (found) {
				continue;
			}
			involvedTags.add(tag);
		}

		// indirectly assigned items
		List<ItemLink> itemLinks2 = ItemLinkPeer.doSelectAll();
		for (ItemLink itemLink : itemLinks2) {
			// character assigned to scene?
			if (itemLink.hasCharacter()) {
				SbCharacter character = itemLink.getCharacter();
				List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer
						.doSelect(povScene, character);
				if (list.isEmpty()) {
					continue;
				}
			}

			// location assigned to scene?
			if (itemLink.hasLocation()) {
				if (itemLink.hasStartScene() && !itemLink.hasEndScene()) {
					if (itemLink.getStartSceneId() != povScene.getId()) {
						continue;
					}
				} else {
					Period p = itemLink.getPeriod();
					if (p != null) {
						if (!p.isInside(sceneDate)) {
							continue;
						}
					}
				}
				if (itemLink.hasLocationOrCharacter()) {
					Location location = itemLink.getLocation();
					List<SceneLinkLocation> list = SceneLinkLocationPeer
							.doSelect(povScene, location);
					if (list.isEmpty()) {
						continue;
					}
				}
			}

			if (itemLink.hasOnlyScene()) {
				if (!itemLink.hasEndScene()) {
					continue;
				}
				// not same strand?
				Scene scene = itemLink.getScene();
				if (scene.getStrandId() != povScene.getStrandId()) {
					continue;
				}
				// not inside period?
				Period p = itemLink.getPeriod();
				if (p != null) {
					if (!p.isInside(sceneDate)) {
						continue;
					}
				}
			}
			
			// already in "items"?
			Item item = itemLink.getItem();
			if (item == null) {
				continue;
			}
			boolean found = false;
			for (Item i : items) {
				if (i == null) {
					continue;
				}
				if (i.getId() == item.getId()) {
					found = true;
					break;
				}
			}
			if (found) {
				continue;
			}
			involvedTags.add(item);
		}

		for (Tag tag : tags) {
			if (tag == null) {
				continue;
			}
			graph.addVertex(tag);
			labelMap.put(tag, tag.toString());
			iconMap.put(tag, tagIconMedium);
			graph.addEdge(graphIndex++, tagVertex, tag);
		}		

		for (Item item : items) {
			if (item == null) {
				continue;
			}
			graph.addVertex(item);
			labelMap.put(item, item.toString());
			iconMap.put(item, itemIconMedium);
			graph.addEdge(graphIndex++, tagVertex, item);
		}

		addToVertexInvolvedTags();
	}
	
	private void createGraphForTags() {
		graphIndex = 0;
		Tag povTag = TagPeer.doSelectById(povId);
		if (povTag == null) {
			return;
		}
		graph.addVertex(povTag);
		labelMap.put(povTag, povTag.toString());
		iconMap.put(povTag, tagIconLarge);

		showTagVertex = false;
		initVertices(povTag);
		
		Set<Location> locations = new HashSet<Location>();
		Set<SbCharacter> characters = new HashSet<SbCharacter>();
		Set<Scene> scenes = new HashSet<Scene>();

		// tag links
		ArrayList<TagLink> links = povTag.getLinks();
		for (TagLink link : links) {
			Period p = link.getPeriod();
			// is chosen date inside period?
			if (p != null) {
				if (!p.isInside(chosenDate)) {
					continue;
				}
			}
			
			if (link.hasLocationOrCharacter()) {
				// character
				if (link.hasCharacter()) {
					characters.add(link.getCharacter());
					// involved tags
					List<TagLink> invTagLinks = TagLinkPeer
							.doSelectByCharacterId(link.getCharacterId());
					for (TagLink invCharLink : invTagLinks) {
						if (invCharLink.getTagId() != povTag.getId()) {
							involvedTags.add(invCharLink.getTag());
						}
					}
					// involved items
					List<ItemLink> invItemLinks = ItemLinkPeer
							.doSelectByCharacterId(link.getCharacterId());
					for (ItemLink invCharLink : invItemLinks) {
						involvedTags.add(invCharLink.getItem());
					}
				}
				// location
				if (link.hasLocation()) {
					locations.add(link.getLocation());
					// involved tags
					List<TagLink> invTagLinks = TagLinkPeer
							.doSelectByLocationId(link.getLocationId());
					for (TagLink invLocationLink : invTagLinks) {
						if (invLocationLink.getTagId() != povTag.getId()) {
							involvedTags.add(invLocationLink.getTag());
						}
					}
					// involved items
					List<ItemLink> invItemLinks = ItemLinkPeer
							.doSelectByLocationId(link.getLocationId());
					for (ItemLink invLocationLink : invItemLinks) {
						involvedTags.add(invLocationLink.getItem());
					}
				}
				continue;
			}
			
			// scene
			Scene scene = link.getScene();
			if (scene == null) {
				continue;
			}
			List<Scene> scenesSameDate = ScenePeer.doSelectByDate(chosenDate);
			for (Scene sdScene : scenesSameDate) {
				if (!link.hasEndScene() && sdScene.getId() != scene.getId()) {
					continue;
				}
				if (sdScene.getStrandId() != scene.getStrandId()) {
					continue;
				}
				scenes.add(sdScene);
				// scene tags
				List<TagLink> tagLinks = TagLinkPeer.doSelectBySceneId(sdScene
						.getId());
				for (TagLink tagLink : tagLinks) {
					Tag tag = tagLink.getTag();
					if (tag == null) {
						continue;
					}
					if (povTag.getId() != tag.getId()) {
						involvedTags.add(tagLink.getTag());
					}
				}
				// scene items
				List<ItemLink> itemLinks = ItemLinkPeer
						.doSelectBySceneId(sdScene.getId());
				for (ItemLink itemLink : itemLinks) {
					Item item = itemLink.getItem();
					if (item == null) {
						continue;
					}
					involvedTags.add(itemLink.getItem());
				}
			}
		}

		addToVertexScenes(scenes);
		addToVertexCharacters(characters);
		addToVertexLocations(locations);
		addToVertexInvolvedTags();
	}

	private void createGraphForItems() {
		graphIndex = 0;
		Item povItem = ItemPeer.doSelectById(povId);
		if (povItem == null) {
			return;
		}
		graph.addVertex(povItem);
		labelMap.put(povItem, povItem.toString());
		iconMap.put(povItem, itemIconLarge);

		showTagVertex = false;
		initVertices(povItem);
		
		Set<Location> locations = new HashSet<Location>();
		Set<SbCharacter> characters = new HashSet<SbCharacter>();
		Set<Scene> scenes = new HashSet<Scene>();

		// item links
		ArrayList<ItemLink> links = povItem.getItemLinks();
		for (ItemLink link : links) {
			Period p = link.getPeriod();
			// is chosen date inside period?
			if (p != null) {
				if (!p.isInside(chosenDate)) {
					continue;
				}
			}
			
			if (link.hasLocationOrCharacter()) {
				// character
				if (link.hasCharacter()) {
					characters.add(link.getCharacter());
					// involved items
					List<ItemLink> invItemLinks = ItemLinkPeer
							.doSelectByCharacterId(link.getCharacterId());
					for (ItemLink itemLink : invItemLinks) {
						if (itemLink.getTagId() != povItem.getId()) {
							involvedTags.add(itemLink.getItem());
						}
					}
					// involved tags
					List<TagLink> invTagLinks = TagLinkPeer
							.doSelectByCharacterId(link.getCharacterId());
					for (TagLink tagLink : invTagLinks) {
						involvedTags.add(tagLink.getTag());
					}
				}
				// location
				if (link.hasLocation()) {
					locations.add(link.getLocation());
					// involved items
					List<ItemLink> invItemLinks = ItemLinkPeer
							.doSelectByLocationId(link.getLocationId());
					for (ItemLink itemLink : invItemLinks) {
						if (itemLink.getItemId() != povItem.getId()) {
							involvedTags.add(itemLink.getItem());
						}
					}
					// involved tags
					List<TagLink> invTagLinks = TagLinkPeer
							.doSelectByLocationId(link.getLocationId());
					for (TagLink tagLink : invTagLinks) {
						involvedTags.add(tagLink.getTag());
					}
				}
				continue;
			}
			
			// scene
			Scene scene = link.getScene();
			if (scene == null) {
				continue;
			}
			List<Scene> scenesSameDate = ScenePeer.doSelectByDate(chosenDate);
			for (Scene sdScene : scenesSameDate) {
				if (!link.hasEndScene() && sdScene.getId() != scene.getId()) {
					continue;
				}
				if (sdScene.getStrandId() != scene.getStrandId()) {
					continue;
				}
				scenes.add(sdScene);
				// scene items
				List<ItemLink> itemLinks = ItemLinkPeer.doSelectBySceneId(sdScene
						.getId());
				if (!itemLinks.isEmpty()) {
					for (ItemLink itemLink : itemLinks) {
						Item item = itemLink.getItem();
						if (item == null) {
							continue;
						}
						if (povItem.getId() != item.getId()) {
							involvedTags.add(itemLink.getItem());
						}
					}
				}
				// scene tags
				List<TagLink> tagLinks = TagLinkPeer.doSelectBySceneId(sdScene
						.getId());
				if (!tagLinks.isEmpty()) {
					for (TagLink tagLink : tagLinks) {
						Tag tag = tagLink.getTag();
						if (tag == null) {
							continue;
						}
						involvedTags.add(tagLink.getTag());
					}
				}
			}
		}

		addToVertexScenes(scenes);
		addToVertexCharacters(characters);
		addToVertexLocations(locations);
		addToVertexInvolvedTags();
	}

	private void addToVertexScenes(Set<Scene> scenes) {
		for (Scene scene : scenes) {
			graph.addVertex(scene);
			labelMap.put(scene, scene.getGraphLabelText());
			iconMap.put(scene, sceneIconMedium);
			graph.addEdge(graphIndex++, sceneVertex, scene);
		}
	}
	
	private void addToVertexCharacters(Set<SbCharacter> characters) {
		for (SbCharacter character : characters) {
			graph.addVertex(character);
			labelMap.put(character, character.toString());
			iconMap.put(character, getCharacterIcon(character, IconSize.MEDIUM));
			graph.addEdge(graphIndex++, characterVertex, character);
		}
	}

	private void addToVertexLocations(Set<Location> locations) {
		for (Location location : locations) {
			graph.addVertex(location);
			labelMap.put(location, location.toString());
			iconMap.put(location, locationIconMedium);
			graph.addEdge(graphIndex++, locationVertex, location);
		}
	}
	
	private void createGraphForLocations() {
		graphIndex = 0;
		Location povLoc = LocationPeer.doSelectById(povId);
		if (povLoc == null) {
			return;
		}
		graph.addVertex(povLoc);
		labelMap.put(povLoc, povLoc.toString());
		iconMap.put(povLoc, locationIconLarge);

		locationVertexTitle = I18N.getMsg("msg.graph.involved.locations");
		initVertices(povLoc);

		// scenes
		for (Scene scene : sceneList) {
			List<SceneLinkLocation> list = SceneLinkLocationPeer
					.doSelectBySceneId(scene.getId());
			for (SceneLinkLocation link : list) {
				Location location = link.getLocation();
				if (location.equals(povLoc)) {
					graph.addVertex(scene);
					labelMap.put(scene, scene.getGraphLabelText());
					iconMap.put(scene, sceneIconMedium);
					graph.addEdge(graphIndex++, sceneVertex, scene);
					sceneIds.add(scene.getId());
					// scene tags
					List<TagLink> tagLinks = TagLinkPeer
							.doSelectBySceneId(scene.getId());
					if (!tagLinks.isEmpty()) {
						for (TagLink tagLink : tagLinks) {
							involvedTags.add(tagLink.getTag());
						}
					}
					// scene items
					List<ItemLink> itemLinks = ItemLinkPeer
							.doSelectBySceneId(scene.getId());
					if (!itemLinks.isEmpty()) {
						for (ItemLink itemLink : itemLinks) {
							involvedTags.add(itemLink.getItem());
						}
					}
				}
			}
		}

		// characters
		for (Scene scene : sceneList) {
			List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer
					.doSelectBySceneId(scene.getId());
			for (SceneLinkSbCharacter linkChar : list) {
				List<SceneLinkLocation> loclinks = SceneLinkLocationPeer
						.doSelectBySceneId(scene.getId());
				for (SceneLinkLocation locLink : loclinks) {
					if (povLoc.getId() == locLink.getLocationId()) {
						SbCharacter character = linkChar.getCharacter();
						graph.addVertex(character);
						labelMap.put(character, character.toString());
						iconMap.put(character,
								getCharacterIcon(character, IconSize.MEDIUM));
						graph.addEdge(graphIndex++, characterVertex, character);
						// character tags
						List<TagLink> tagLinks = TagLinkPeer
								.doSelectByCharacterId(character.getId());
						if (!tagLinks.isEmpty()) {
							for (TagLink tagLink : tagLinks) {
								involvedTags.add(tagLink.getTag());
							}
						}
						// character items
						List<ItemLink> itemLinks = ItemLinkPeer
								.doSelectByCharacterId(character.getId());
						if (!itemLinks.isEmpty()) {
							for (ItemLink itemLink : itemLinks) {
								involvedTags.add(itemLink.getItem());
							}
						}
					}
				}
			}
		}

		// other locations
		for (Scene scene : sceneList) {
			List<SceneLinkLocation> list = SceneLinkLocationPeer
					.doSelectBySceneId(scene.getId());
			for (SceneLinkLocation link : list) {
				Location location = link.getLocation();
				if (sceneIds.contains(link.getSceneId())
						&& location.getId() != povLoc.getId()) {
					graph.addVertex(location);
					labelMap.put(location, location.toString());
					iconMap.put(location, locationIconMedium);
					graph.addEdge(graphIndex++, locationVertex, location);
					// involved tags
					List<TagLink> tagLinks = TagLinkPeer
							.doSelectByLocationId(location.getId());
					if (!tagLinks.isEmpty()) {
						for (TagLink tagLink : tagLinks) {
							involvedTags.add(tagLink.getTag());
						}
					}
					// involved items
					List<ItemLink> itemLinks = ItemLinkPeer
							.doSelectByLocationId(location.getId());
					if (!itemLinks.isEmpty()) {
						for (ItemLink itemLink : itemLinks) {
							involvedTags.add(itemLink.getItem());
						}
					}
				}
			}
		}

		// location tags
		List<TagLink> tagList = TagLinkPeer
				.doSelectByLocationId(povLoc.getId());
		for (TagLink tagLink : tagList) {
			Period p = tagLink.getPeriod();
			if (p != null) {
				// chosen date inside period?
				if (!p.isInside(chosenDate)) {
					continue;
				}
			}
			Tag tag = tagLink.getTag();
			if (tag == null) {
				continue;
			}
			Tag tagClone = tag.clone();
			graph.addVertex(tagClone);
			labelMap.put(tagClone, tag.toString());
			iconMap.put(tagClone, tagIconMedium);
			graph.addEdge(graphIndex++, tagVertex, tagClone);
		}

		// location items
		List<ItemLink> itemList = ItemLinkPeer.doSelectByLocationId(povLoc
				.getId());
		for (ItemLink itemLink : itemList) {
			Period p = itemLink.getPeriod();
			if (p != null) {
				// chosen date inside period?
				if (!p.isInside(chosenDate)) {
					continue;
				}
			}
			Item item = itemLink.getItem();
			if (item == null) {
				continue;
			}
			Item itemClone = item.clone();
			graph.addVertex(itemClone);
			labelMap.put(itemClone, item.toString());
			iconMap.put(itemClone, tagIconMedium);
			graph.addEdge(graphIndex++, tagVertex, itemClone);
		}

		addToVertexInvolvedTags();
	}

	private void createGraphForCharacters() {
		graphIndex = 0;
		SbCharacter povCh = SbCharacterPeer.doSelectById(povId);
		if (povCh == null) {
			return;
		}
		graph.addVertex(povCh);
		labelMap.put(povCh, povCh.toString());
		iconMap.put(povCh, getCharacterIcon(povCh, IconSize.LARGE));

		initVertices(povCh);

		// character tags
		List<TagLink> chTaglist = TagLinkPeer.doSelectByCharacterId(povCh.getId());
		for (TagLink link : chTaglist) {
			if (link.hasPeriod()) {
				Period p = link.getPeriod();
				// is chosen date inside period?
				if (p != null) {
					if (!p.isInside(chosenDate)) {
						continue;
					}
				}
			}
			Tag tag = link.getTag();
			if (tag == null) {
				continue;
			}
			Tag tagClone = tag.clone();
			graph.addVertex(tagClone);
			labelMap.put(tagClone, tag.toString());
			iconMap.put(tagClone, tagIconMedium);
			graph.addEdge(graphIndex++, tagVertex, tagClone);
		}

		// character items
		List<ItemLink> chItemlist = ItemLinkPeer.doSelectByCharacterId(povCh.getId());
		for (ItemLink link : chItemlist) {
			if (link.hasPeriod()) {
				Period p = link.getPeriod();
				// is chosen date inside period?
				if (p != null) {
					if (!p.isInside(chosenDate)) {
						continue;
					}
				}
			}
			Item item = link.getItem();
			if (item == null) {
				continue;
			}
			Item itemClone = item.clone();
			graph.addVertex(itemClone);
			labelMap.put(itemClone, item.toString());
			iconMap.put(itemClone, itemIconMedium);
			graph.addEdge(graphIndex++, tagVertex, itemClone);
		}

		// scenes
		for (Scene scene : sceneList) {
			List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer
					.doSelectBySceneId(scene.getId());
			for (SceneLinkSbCharacter link : list) {
				SbCharacter character = link.getCharacter();
				if (character.equals(povCh)) {
					graph.addVertex(scene);
					labelMap.put(scene, scene.getGraphLabelText());
					iconMap.put(scene, sceneIconMedium);
					graph.addEdge(graphIndex++, sceneVertex, scene);
					sceneIds.add(scene.getId());
					List<TagLink> tagLinks = TagLinkPeer
							.doSelectBySceneId(scene.getId());
					for (TagLink tagLink : tagLinks) {
						Tag tag = tagLink.getTag();
						if (tag == null) {
							continue;
						}
						if (!isTagInGraph(tag)) {
							involvedTags.add(tag);
						}
					}
					List<ItemLink> itemLinks = ItemLinkPeer
							.doSelectBySceneId(scene.getId());
					for (ItemLink itemLink : itemLinks) {
						Item item = itemLink.getItem();
						if (item == null) {
							continue;
						}
						if (!isItemInGraph(item)) {
							involvedTags.add(item);
						}
					}
				}
			}
		}

		// locations
		for (Scene scene : sceneList) {
			List<SceneLinkLocation> list = SceneLinkLocationPeer
					.doSelectBySceneId(scene.getId());
			for (SceneLinkLocation link : list) {
				Location location = link.getLocation();
				if (!sceneIds.contains(link.getSceneId())) {
					continue;
				}
				
				graph.addVertex(location);
				labelMap.put(location, location.toString());
				iconMap.put(location, locationIconMedium);
				graph.addEdge(graphIndex++, locationVertex, location);
				
				List<TagLink> tagLinks = TagLinkPeer
						.doSelectByLocationId(location.getId());
				for (TagLink tagLink : tagLinks) {
					Tag tag = tagLink.getTag();
					if (tag == null) {
						continue;
					}
					if (!isTagInGraph(tag)) {
						involvedTags.add(tag);
					}
				}
				List<ItemLink> itemLinks = ItemLinkPeer
						.doSelectByLocationId(location.getId());
				for (ItemLink itemLink : itemLinks) {
					Item item = itemLink.getItem();
					if (item == null) {
						continue;
					}
					if (!isItemInGraph(item)) {
						involvedTags.add(item);
					}
				}
			}
		}

		// characters
		for (Scene scene : sceneList) {
			List<SceneLinkSbCharacter> list = SceneLinkSbCharacterPeer
					.doSelectBySceneId(scene.getId());
			for (SceneLinkSbCharacter link : list) {
				SbCharacter character = link.getCharacter();
				if (!(character.getId() != povCh.getId()
						&& sceneIds.contains(link.getSceneId()))) {
					continue;
				}
				
				graph.addVertex(character);
				labelMap.put(character, character.toString());
				iconMap.put(character,
						getCharacterIcon(character, IconSize.MEDIUM));
				graph.addEdge(graphIndex++, characterVertex, character);
					
				List<TagLink> tagLinks = TagLinkPeer
						.doSelectByCharacterId(character.getId());
				for (TagLink tagLink : tagLinks) {
					if (tagLink.getCharacterId() != povCh.getId()) {
						Tag tag = tagLink.getTag();
						if (!isTagInGraph(tag)) {
							involvedTags.add(tag);
						}
					}
				}
				List<ItemLink> itemLinks = ItemLinkPeer
						.doSelectByCharacterId(character.getId());
				for (ItemLink itemLink : itemLinks) {
					if (itemLink.getCharacterId() != povCh.getId()) {
						Item item = itemLink.getItem();
						if (!isItemInGraph(item)) {
							involvedTags.add(item);
						}
					}
				}
			}
		}
		
		addToVertexInvolvedTags();
	}
	
	private boolean isTagInGraph(Tag tag) {
		if (tag == null) {
			return false;
		}
		Collection<DbTable> tags = graph.getVertices();
		for (DbTable dbObj : tags) {
			if (dbObj instanceof Tag) {
				if (dbObj.getRealId() == tag.getRealId()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isItemInGraph(Item item) {
		if (item == null) {
			return false;
		}
		Collection<DbTable> tags = graph.getVertices();
		for (DbTable dbObj : tags) {
			if (dbObj instanceof Item) {
				if (dbObj.getRealId() == item.getRealId()) {
					return true;
				}
			}
		}
		return false;
	}

	private void initVertices(DbTable pov) {
		addVertexScene(pov);
		addVertexCharachter(pov);
		addVertexLocation(pov);
		if(showTagVertex){			
			addVertexTag(pov);
		}
		addVertexInvoldedTag(pov);
		sceneList = ScenePeer.doSelectByDate(chosenDate);
		sceneIds = new ArrayList<Integer>();
		involvedTags = new HashSet<Tag>();
		// clear settings
		sceneVertexTitle = null;
		locationVertexTitle = null;
		showTagVertex = true;
	}

	private void addVertexTag(DbTable parentVertex) {
		tagVertex = new Tag(true);
		tagVertex.setName(I18N.getMsg("msg.tags") + " & "
				+ I18N.getMsg("msg.items"));
		graph.addVertex(tagVertex);
		labelMap.put(tagVertex, tagVertex.getName());
		iconMap.put(tagVertex, emptyIcon);
		graph.addEdge(graphIndex++, parentVertex, tagVertex);
	}

	private void addVertexInvoldedTag(DbTable parentVertex) {
		involvedTagVertex = new Tag(true);
		involvedTagVertex.setName(I18N.getMsg("msg.graph.involved.tags_items"));
		graph.addVertex(involvedTagVertex);
		labelMap.put(involvedTagVertex, involvedTagVertex.getName());
		iconMap.put(involvedTagVertex, emptyIcon);
		graph.addEdge(graphIndex++, parentVertex, involvedTagVertex);
	}

	private void addToVertexInvolvedTags() {
		for (Tag tag : involvedTags) {
			if (tag == null) {
				continue;
			}
			if (tag instanceof Item) {
				Item itemClone = (Item) tag.clone();
				graph.addVertex(itemClone);
				labelMap.put(itemClone, ((Item) tag).toString());
				iconMap.put(itemClone, itemIconMedium);
				graph.addEdge(graphIndex++, involvedTagVertex, itemClone);
			} else if (tag instanceof Tag) {
				Tag tagClone = tag.clone();
				graph.addVertex(tagClone);
				labelMap.put(tagClone, tag.toString());
				iconMap.put(tagClone, tagIconMedium);
				graph.addEdge(graphIndex++, involvedTagVertex, tagClone);
			}
		}
	}

	private void addVertexScene(DbTable parentVertex) {
		sceneVertex = new Scene(true);
		sceneVertex.setSceneNo(-1);
		if (sceneVertexTitle != null) {
			sceneVertex.setTitle(sceneVertexTitle);
		} else {
			sceneVertex.setTitle(I18N.getMsg("msg.common.scenes"));
		}
		graph.addVertex(sceneVertex);
		labelMap.put(sceneVertex, sceneVertex.toString());
		iconMap.put(sceneVertex, emptyIcon);
		graph.addEdge(graphIndex++, parentVertex, sceneVertex);
	}

	private void addVertexCharachter(DbTable parentVertex) {
		characterVertex = new SbCharacter(true);
		characterVertex.setFirstname(I18N.getMsg("msg.common.persons"));
		graph.addVertex(characterVertex);
		labelMap.put(characterVertex, characterVertex.getName());
		iconMap.put(characterVertex, emptyIcon);
		graph.addEdge(graphIndex++, parentVertex, characterVertex);
	}

	private void addVertexLocation(DbTable parentVertex) {
		locationVertex = new Location(true);
		if (locationVertexTitle != null) {
			locationVertex.setName(locationVertexTitle);
		} else {
			locationVertex.setName(I18N.getMsg("msg.menu.locations"));
		}
		graph.addVertex(locationVertex);
		labelMap.put(locationVertex, locationVertex.toString());
		iconMap.put(locationVertex, emptyIcon);
		graph.addEdge(graphIndex++, parentVertex, locationVertex);
	}

	private Icon getCharacterIcon(SbCharacter character, IconSize iconSize) {
		if (iconSize == IconSize.MEDIUM) {
			if (character.getGender().isMale()) {
				return manIconMedium;
			}
			if (character.getGender().isFemale()) {
				return womanIconMedium;
			}
			return alienIconMedium;
		}
		if (iconSize == IconSize.LARGE) {
			if (character.getGender().isMale()) {
				return manIconLarge;
			}
			if (character.getGender().isFemale()) {
				return womanIconLarge;
			}
			return characterIconLarge;
		}
		return emptyIcon;
	};

	@SuppressWarnings("unused")
	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new TableNewAction(new Scene()));
		menu.add(new TableNewAction(new Chapter()));
		menu.add(new Separator());
		menu.add(new TableNewAction(new SbCharacter()));
		menu.add(new TableNewAction(new Location()));
		menu.add(new TableNewAction(new Tag()));
		menu.add(new TableNewAction(new Item()));
		menu.add(new Separator());
		menu.add(new TableNewAction(new Strand()));
		menu.add(new TableNewAction(new Part()));
		menu.add(new Separator());
		// generate chapters
		ActionRegistry ar = ActionRegistry.getInstance();
		menu.add(ar.getAction(SbAction.CHAPTER_GENERATE));
		return menu;
	}

	public void setCharacterId(int characterId) {
		this.povId = characterId;
	}

	class VertexStringerImpl<V> implements Transformer<V, String> {
		Map<V, String> map = new HashMap<V, String>();
		boolean enabled = true;

		public VertexStringerImpl(Map<V, String> map) {
			this.map = map;
		}

		public String transform(V v) {
			if (isEnabled()) {
				return "<html><table width='100'><tr><td>" + map.get(v)
						+ "</td></tr></table>";
			} else {
				return "";
			}
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

	private void updateCombos() {
		if (this.povDbObj instanceof SbCharacter) {
			characterCombo.setSelectedItem(povDbObj);
		} else if (this.povDbObj instanceof Location) {
			locationCombo.setSelectedItem(povDbObj);
		} else if (this.povDbObj instanceof Item) {
			itemCombo.setSelectedItem(povDbObj);
		} else if (this.povDbObj instanceof Tag) {
			tagCombo.setSelectedItem(povDbObj);
		} else if (this.povDbObj instanceof Scene) {
			sceneCombo.setSelectedItem(povDbObj);
		}
	}
	
	public void refresh(DbTable dbObj) {
		if (dbObj == null) {
			return;
		}
		this.povId = dbObj.getId();
		this.povDbObj = dbObj;
		createGraph();
		updateCombos();
	}
	
	private void setDateEnabled(boolean enabled) {
		setDateComboEnabled(enabled);
		setDateSliderEnabled(enabled);
	}
	
	private void setDateComboEnabled(boolean enabled) {
		dateCombo.setEnabled(enabled);
	}
	
	private void setDateSliderEnabled(boolean enabled) {
		dateSlider.setEnabled(enabled);
		btDecDate.setEnabled(enabled);
		btIncDate.setEnabled(enabled);
	}
	
	public void setDbObj(DbTable value) {
		if (value instanceof Scene) {
			setScene((Scene) value);
		} else if (value instanceof SbCharacter) {
			setCharacter((SbCharacter) value);
		} else if (value instanceof Location) {
			setLocation((Location) value);
		} else if (value instanceof Item) {
			setItem((Item) value);
		} else if (value instanceof Tag) {
			setTag((Tag) value);
		}
	}
	
	public void setScene(Scene scene) {
		sceneCombo.setSelectedItem(scene);
	}

	public void setCharacter(SbCharacter character) {
		characterCombo.setSelectedItem(character);
	}

	public void setLocation(Location location) {
		locationCombo.setSelectedItem(location);
	}

	public void setTag(Tag tag) {
		tagCombo.setSelectedItem(tag);
	}

	public void setItem(Item item) {
		itemCombo.setSelectedItem(item);
	}

	public void animate() {
		try {
			Random random = new Random();

			int a = random.nextInt(5);
			int i = 0;

			switch (a) {
			case 0:
				i = random.nextInt(sceneCombo.getItemCount() - 1) + 1;
				if (i > sceneCombo.getItemCount()) {
					return;
				}
				sceneCombo.setSelectedIndex(i);
				break;
			case 1:
				i = random.nextInt(characterCombo.getItemCount() - 1) + 1;
				if (i > characterCombo.getItemCount()) {
					return;
				}
				characterCombo.setSelectedIndex(i);
				break;
			case 2:
				i = random.nextInt(locationCombo.getItemCount() - 1) + 1;
				if (i > locationCombo.getItemCount()) {
					return;
				}
				locationCombo.setSelectedIndex(i);
				break;
			case 3:
				i = random.nextInt(tagCombo.getItemCount() - 1) + 1;
				if (i > tagCombo.getItemCount()) {
					return;
				}
				tagCombo.setSelectedIndex(i);
				break;
			case 4:
				i = random.nextInt(itemCombo.getItemCount() - 1) + 1;
				if (i > itemCombo.getItemCount()) {
					return;
				}
				itemCombo.setSelectedIndex(i);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}
	
	public boolean hasAutoRefresh() {
		return cbAutoRefresh.isSelected();
	}

	private void resetAllButOneCombo(ComboType combo) {
		ComboType[] s = ComboType.values();
		ComboType[] a = (ComboType[]) ArrayUtils.removeElement(s, combo);
		resetCombos(a);
	}
	
	private void resetCombos(ComboType[] combos) {
		JComboBox combo = null;
		for (ComboType type : combos) {
			switch (type) {
			case SCENE:
				combo = sceneCombo;
				break;
			case CHARACTER:
				combo = characterCombo;
				break;
			case LOCATION:
				combo = locationCombo;
				break;
			case TAG:
				combo = tagCombo;
				break;
			case ITEM:
				combo = itemCombo;
				break;
			}
			// reset combo
			if (combo != null) {
				combo.removeActionListener(this);
				combo.setSelectedIndex(0);
				combo.addActionListener(this);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == null || !processActionListener) {
			return;
		}
		
		this.povSourceName = ((JComponent) evt.getSource()).getName();
		DbTable obj = null;
		if (this.povSourceName == sceneCombo.getName()) {
			obj = (DbTable) sceneCombo.getSelectedItem();
			resetAllButOneCombo(ComboType.SCENE);
			dateSlider.setDate(((Scene) obj).getDate());
			setDateEnabled(false);
		} else if (this.povSourceName == characterCombo.getName()) {
			obj = (DbTable) characterCombo.getSelectedItem();
			resetAllButOneCombo(ComboType.CHARACTER);
			setDateEnabled(true);
		} else if (this.povSourceName == locationCombo.getName()) {
			obj = (DbTable) locationCombo.getSelectedItem();
			resetAllButOneCombo(ComboType.LOCATION);
			setDateEnabled(true);
		} else if (this.povSourceName == tagCombo.getName()) {
			obj = (DbTable) tagCombo.getSelectedItem();
			resetAllButOneCombo(ComboType.TAG);
			setDateEnabled(true);
		} else if (this.povSourceName == itemCombo.getName()) {
			obj = (DbTable) itemCombo.getSelectedItem();
			resetAllButOneCombo(ComboType.ITEM);
			setDateEnabled(true);
		}
		
		if (this.povSourceName == dateCombo.getName()) {
			Date date = (Date) dateCombo.getSelectedItem();
			dateSlider.setDate(date);
			obj = getSelectedItemFromCombos();
		}

		refresh(obj);
		dateChanged();
	}
	
	private DbTable getSelectedItemFromCombos() {
		DbTable obj = (DbTable) characterCombo.getSelectedItem();
		if (obj.isNew()) {
			obj = (DbTable) locationCombo.getSelectedItem();
		}
		if (obj.isNew()) {
			obj = (DbTable) tagCombo.getSelectedItem();
		}
		if (obj.isNew()) {
			obj = (DbTable) itemCombo.getSelectedItem();
		}
		return obj;
	}
	
	private boolean checkComboStates() {
		if (this.povSourceName == null || sceneCombo == null
				|| characterCombo == null || locationCombo == null
				|| tagCombo == null || itemCombo == null) {
			return false;
		}
		return true;
	}
	
	private boolean isChosen(ComboType comboType) {
		switch (comboType) {
		case SCENE:
			if (!checkComboStates()) {
				return false;
			}
			return this.povSourceName == sceneCombo.getName() ? true : false;
		case CHARACTER:
			if (!checkComboStates()) {
				return false;
			}
			return this.povSourceName == characterCombo.getName() ? true
					: false;
		case LOCATION:
			if (!checkComboStates()) {
				return false;
			}
			return this.povSourceName == locationCombo.getName() ? true : false;
		case TAG:
			if (!checkComboStates()) {
				return false;
			}
			return this.povSourceName == tagCombo.getName() ? true : false;
		case ITEM:
			if (!checkComboStates()) {
				return false;
			}
			return this.povSourceName == itemCombo.getName() ? true : false;
		}
		return false;
	}

	private void disableActionListener() {
		processActionListener = false;
	}

	private void enableActionListener() {
		processActionListener = true;
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
		JSlider source = (JSlider) evt.getSource();
		if (!source.getValueIsAdjusting()) {
			// int val = source.getValue();
			dateChanged();
		}
	}

	private void dateChanged() {
		chosenDate = dateSlider.getDate();
		if (oldDate == null) {
			createGraph();
			return;
		}
		if (oldDate.compareTo(chosenDate) != 0) {
			createGraph();
		}

	}
	
	@Override
	public void refresh() {
		refreshControlPanel();
		createGraph();
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			showBalloonLayout = true;
			makeLayoutTransition();
		} else {
			showBalloonLayout = false;
			makeLayoutTransition();
		}
		try {
			Internal internal = InternalPeer
					.doSelectByKey(Constants.ProjectSetting.MEMORIA_TREE);
			if (internal == null) {
				internal = new Internal();
			}
			internal.setKey(Constants.ProjectSetting.MEMORIA_TREE);
			internal.setBooleanValue(!showBalloonLayout);
			internal.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
