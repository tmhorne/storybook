package ch.intertec.storybook.toolkit;

import java.awt.Color;
import java.util.StringTokenizer;

import ch.intertec.storybook.toolkit.swing.ColorUtil;
import ch.intertec.storybook.toolkit.swing.SwingTools;

public class HtmlTools {
	
	public static int countWords(String text) {
		int count = 0;
		StringTokenizer stk = new StringTokenizer(text, " ");
		while (stk.hasMoreTokens()) {
			@SuppressWarnings("unused")
			String token = stk.nextToken();
			count++;
		}
		return count;
	}
	
	public static String getRow2Cols(StringBuffer text1, String text2) {
		return getRow2Cols(text1.toString(), text2.toString());
	}

	public static String getRow2Cols(StringBuffer text1, StringBuffer text2) {
		return getRow2Cols(text1.toString(), text2.toString());
	}

	public static String getRow2Cols(String text1, StringBuffer text2) {
		return getRow2Cols(text1.toString(), text2.toString());
	}

	public static String getRow2Cols(String text1, String text2) {
		return "<tr><td>" + text1 + "</td><td>" + text2 + "</td></tr>";
	}

	public static String getTitle(String title) {
		StringBuffer buf = new StringBuffer();
		buf.append("<div style='padding-top:2px;padding-bottom:2px;");
		buf.append("padding-left:4px;padding-right:4px;");
		buf.append("margin-bottom:2px;");
		buf.append("'><b>");
		buf.append(title);
		buf.append("</b></div>");
		return buf.toString();
	}
	
	public static String getColoredTitle(Color clr, String title) {
		String htmlClr = (clr == null ? "white" : ColorUtil.getHexName(clr));
		StringBuffer buf = new StringBuffer();
		buf.append("<div style='padding-top:2px;padding-bottom:2px;");
		buf.append("padding-left:4px;padding-right:4px;");
		buf.append("margin-bottom:2px;");
		if (clr != null) {
			buf.append("background-color:");
			buf.append(htmlClr);
			buf.append(";");
		}
		if (clr != null && ColorUtil.isDark(clr)) {
			buf.append("color:white;");
		}
		buf.append("'><b>");
		buf.append(title);
		buf.append("</b></div>");
		return buf.toString();
	}
	
	public static String getHeadWithCSS() {
		StringBuffer buf = new StringBuffer();
		buf.append("<head>");
		buf.append("<style type='text/css'><!--\n");
		// body
		buf.append("body {");
		buf.append("font-family:Arial,sans-serif;");
		buf.append("font-size:10px;");
		buf.append("padding-left:2px;");
		buf.append("padding-right:2px;");
		buf.append("}\n");
		// h1
		buf.append("h1 {");
		buf.append("font-family:Arial,sans-serif;");
		buf.append("font-size:16px;");
		buf.append("text-align:center;");
		buf.append("margin-top:15px;");
		buf.append("margin-bottom:15px;");
		buf.append("}\n");
		// h2
		buf.append("h2 {");
		buf.append("font-family:Arial,sans-serif;");
		buf.append("font-size:12px;");
		buf.append("margin-top:15px;");
		buf.append("}\n");
		// div
		buf.append("p {");
		buf.append("margin-top:2px;");
		buf.append("}\n");
		// div
		buf.append("div {");
		buf.append("padding-left:5px;");
		buf.append("padding-right:5px;");
		buf.append("}\n");
		// table
		buf.append("table tr {");
		// buf.append("border-width:1px;border-color:#000000;border-style:solid;");
		buf.append("margin:0px;");
		buf.append("padding:0px;");
		buf.append("}\n");
		buf.append("td {");
		buf.append("margin-right:5px;");
		buf.append("padding:2px;");
		buf.append("}\n");
		buf.append("--></style>");
		buf.append("</head>\n");
		return buf.toString();
	}

	public static void formateDescr(StringBuffer buf, String descr,
			boolean shorten) {
		String str = descr.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		if (str.isEmpty()) {
			return;
		}
		if (shorten) {
			buf.append("<div style='width:300px'>");
			buf.append(SwingTools.shortenString(str, 300));
		} else {
			buf.append("<div>");
			buf.append(str);
		}
		buf.append("</div>");
	}

	public static void formateNotes(StringBuffer buf, String notes,
			boolean shorten) {
		String str = notes.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		if (str.isEmpty()) {
			return;
		}
		buf.append("<hr style='margin:5px'/>");
		if (shorten) {
			buf.append("<div style='width:300px'>");
			buf.append(SwingTools.shortenString(str, 300));
		} else {
			buf.append("<div>");
			buf.append(str);
		}
		buf.append("</div>");
	}
}
