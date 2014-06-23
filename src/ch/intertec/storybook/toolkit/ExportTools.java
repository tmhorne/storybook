package ch.intertec.storybook.toolkit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;

import ch.intertec.storybook.model.Chapter;
import ch.intertec.storybook.model.ChapterPeer;
import ch.intertec.storybook.model.Part;
import ch.intertec.storybook.model.PartPeer;
import ch.intertec.storybook.model.Scene;
import ch.intertec.storybook.model.ScenePeer;

public class ExportTools {

	public static String getExportText(boolean expPart,
			boolean expChapterNumbers, boolean expChapterWord,
			boolean expChapterDescr, boolean expSceneTitles) {
		return getExportText(expPart, expChapterNumbers, expChapterWord,
				expChapterDescr, expSceneTitles, -1);
	}
	
	public static String getExportText(boolean expPart,
			boolean expChapterNumbers, boolean expChapterWord,
			boolean expChapterDescr, boolean expSceneTitles, int limit) {
		String text = "";
		text += ProjectTools.getProjectName();
		List<Part> partList = PartPeer.doSelectAll();
		for (Part part : partList) {
			if (expPart) {
				text += "\n\n\n" + I18N.getMsg("msg.common.part") + " "
						+ part.getNumberStr() + ": ";
				text += part.getName();
				text += "\n";
			}
			List<Chapter> chapterList = ChapterPeer.doSelectByPart(part);
			for (Chapter chapter : chapterList) {
				text += "\n\n";
				if (expChapterNumbers) {
					if (expChapterWord) {
						text += I18N.getMsg("msg.common.chapter") + " ";
					}
					text += chapter.getChapterNoStr() + ": ";
				}
				text += chapter.getTitle();
				text += "\n";
				if (expChapterDescr) {
					text += "\n" + chapter.getDescription() + "\n";
				}
				List<Scene> sceneList = ScenePeer.doSelectByChapter(chapter);
				int i = 0;
				for (Scene scene : sceneList) {
					if (expSceneTitles) {
						if (i > 0) {
							text += "\n";
						}
						text += "\n" + scene.getTitle();
					}
					text += "\n" + scene.getText();
					++i;
				}

				text += "\n";
				
				if (limit != -1 && text.length() > limit) {
					return text + "\n...";
				}
			}
		}
		return text;
	}

	/**
	 * Exports a text (a string) to a text file, UTF-8 encoded.
	 * 
	 * @param file the file to export to
	 * @param text the text to export
	 */
	public static void exportTextToFile(File file, String text) {
		try {
			FileWriterWithEncoding writer = new FileWriterWithEncoding(file,
					"UTF-8");
			writer.write(text + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
