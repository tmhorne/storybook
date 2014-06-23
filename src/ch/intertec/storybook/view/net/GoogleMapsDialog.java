package ch.intertec.storybook.view.net;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;
import ch.intertec.storybook.toolkit.net.ClientHttpRequest;

@SuppressWarnings("serial")
public class GoogleMapsDialog extends JDialog {

	public GoogleMapsDialog() {
		super();
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("flowy");
		setLayout(layout);

		System.out.println("starting...");

		ClientHttpRequest chr;
		 try {
			chr = new ClientHttpRequest(
					"http://localhost/storybook/googlemaps/read.php");
			chr.setParameter("test0", "this is a java test");
			chr.setParameter("test1", "blah");
			chr.post();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// WebBrowser webBrowser = getWebBrowser();
		// add(webBrowser);

		JButton closeBt = new JButton("close");
		add(closeBt);
		System.out.println("done.");
	}

	/*
	private WebBrowser getWebBrowser() {
		WebBrowserUtil.enableDebugMessages(true);
		WebBrowser webBrowser = null;
		BrMap brMap = null;
		try {
			// Desktop.open(new
			// File("C:\\Users\\martin\\Documents\\dummy.txt"));
			// Desktop.browse(new URL("http://storybook.intertec.ch"));
			webBrowser = new WebBrowser();
			System.out.println("is default mozilla "
					+ WebBrowserUtil.isDefaultBrowserMozilla());

			// doesn't work, bug pending, exception:
			// Can't execute the native embedded browser. Error message:
			// java.io.IOException: The filename, directory name, or volume
			// label syntax is incorrect
			// webBrowser.setURL(new URL("http://storybook.intertec.ch"));

			BrMap.DESIGN_MODE = false;
			brMap = new BrMap();
			brMap.execJS(":_findAddress(\"" + "zurich" + "\")");
			brMap.execJS("");

			BrMapBalloonSprite bl = new BrMapBalloonSprite(
					"<html><b>Location:</b><br><b style=\"color:green\">"
							+ "zurich" + "</b></html>", brMap.getPoint(brMap
							.getViewCenter()));
			bl.add(brMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return webBrowser;
	}
	*/
}
