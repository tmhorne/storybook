package ch.intertec.storybook.playground.jung;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.apache.commons.collections15.Transformer;

import ch.intertec.storybook.toolkit.I18N;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;

@SuppressWarnings("serial")
public class Test02 extends JungTest {
	DirectedSparseGraph<Number, Number> graph;
//	UndirectedSparseGraph<Number, Number> graph;
	VisualizationViewer<Number, Number> vv;

	public Test02(Container content) {
		graph = new DirectedSparseGraph<Number, Number>();
//		graph = new UndirectedSparseGraph<Number, Number>();

		Number[] vertices = new Number[4];
		vertices[0] = new Integer(0);
		vertices[1] = new Integer(1);
		vertices[2] = new Integer(2);
		vertices[3] = new Integer(3);
		graph.addVertex(vertices[0]);
		graph.addVertex(vertices[1]);
		graph.addVertex(vertices[2]);
		graph.addVertex(vertices[3]);
		graph.addEdge(new Double(Math.random()), vertices[0], vertices[1],
				EdgeType.DIRECTED);
		graph.addEdge(new Double(Math.random()), vertices[1], vertices[2],
				EdgeType.DIRECTED);
		graph.addEdge(new Double(Math.random()), vertices[1], vertices[3],
				EdgeType.DIRECTED);

		KKLayout<Number, Number> layout = new KKLayout<Number, Number>(graph);
//		FRLayout<Number, Number> layout = new FRLayout<Number, Number>(graph);
		layout.setMaxIterations(100);
		vv = new VisualizationViewer<Number, Number>(layout, new Dimension(600,
				600));
		vv.setBackground(Color.white);

		Map<Number, String> labelMap = new HashMap<Number, String>();
		labelMap.put(vertices[0], "lisa");
		labelMap.put(vertices[1], "locations");
		labelMap.put(vertices[2], "bern");
		labelMap.put(vertices[3], "zurich");
		final Transformer<Number, String> vertexStringerImpl = new VertexStringerImpl<Number>(
				labelMap);
		vv.getRenderContext().setVertexLabelTransformer(vertexStringerImpl);
		
		Map<Number, Icon> iconMap = new HashMap<Number, Icon>();
		Icon characterIcon = I18N.getIcon("icon.small.character");
		Icon locationIcon = I18N.getIcon("icon.small.location");
		iconMap.put(vertices[0], characterIcon);
		iconMap.put(vertices[1], locationIcon);
		iconMap.put(vertices[2], locationIcon);
		iconMap.put(vertices[3], locationIcon);

		final VertexIconShapeTransformer<Number> vertexImageShapeFunction = new VertexIconShapeTransformer<Number>(
				new EllipseVertexShapeTransformer<Number>());
		final DefaultVertexIconTransformer<Number> vertexIconFunction = new DefaultVertexIconTransformer<Number>();
		vertexImageShapeFunction.setIconMap(iconMap);
		vertexIconFunction.setIconMap(iconMap);
		vv.getRenderContext().setVertexShapeTransformer(
				vertexImageShapeFunction);
		vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);
		
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);

		final DefaultModalGraphMouse<?, ?> graphMouse = new DefaultModalGraphMouse<Object, Object>();
		vv.setGraphMouse(graphMouse);
	}

	class VertexStringerImpl<V> implements Transformer<V, String> {
		Map<V, String> map = new HashMap<V, String>();
		boolean enabled = true;

		public VertexStringerImpl(Map<V, String> map) {
			this.map = map;
		}

		public String transform(V v) {
			if (isEnabled()) {
				return map.get(v);
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
}
