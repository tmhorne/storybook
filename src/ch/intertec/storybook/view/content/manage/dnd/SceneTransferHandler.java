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

package ch.intertec.storybook.view.content.manage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import ch.intertec.storybook.model.ScenePeer;
import ch.intertec.storybook.view.content.manage.ChapterPanel;

@SuppressWarnings("serial")
public class SceneTransferHandler extends TransferHandler {
	
	DataFlavor sceneFlavor = DataFlavor.stringFlavor;
	DTScenePanel sourceScene;

	public boolean importData(JComponent comp, Transferable t) {
		if (canImport(comp, t.getTransferDataFlavors())) {
			DTScenePanel destDtScene = (DTScenePanel) comp;
			// don't drop on myself
			if (sourceScene == destDtScene) {
				return true;
			}
			try {
				String sourceSceneIdStr = (String) t.getTransferData(sceneFlavor);
				int sourceSceneId = Integer.parseInt(sourceSceneIdStr);
				switch (destDtScene.getType()) {
				case DTScenePanel.TYPE_NONE:
					return swapScenes(sourceSceneId, destDtScene);
				case DTScenePanel.TYPE_BEGIN:
					return moveSceneToBegin(sourceSceneId, destDtScene);
				case DTScenePanel.TYPE_NEXT:
					return moveScene(sourceSceneId, destDtScene);
				case DTScenePanel.TYPE_MAKE_UNASSIGNED:
					ScenePeer.makeSceneUnassigned(sourceSceneId);
				default:
					break;
				}
				
			} catch (UnsupportedFlavorException ufe) {
				ufe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return false;
	}
		
	private boolean moveScene(int sourceSceneId, DTScenePanel destDtScene){
		System.out.println("SceneTransferHandler.moveScene(): ");
		ChapterPanel destChapterPanel = (ChapterPanel) destDtScene.getParent();
		int destChapterId = destChapterPanel.getChapter().getId();
		int sceneNo = destDtScene.getPreviousNumber() + 1;
		return ScenePeer.insertScene(sourceSceneId, destChapterId, sceneNo);
	}
	
	private boolean moveSceneToBegin(int sourceSceneId, DTScenePanel destDtScene){
		System.out.println("SceneTransferHandler.moveSceneToBegin(): ");
		ChapterPanel destChapterPanel = (ChapterPanel) destDtScene.getParent();
		int destChapterId = destChapterPanel.getChapter().getId();
		return ScenePeer.moveSceneToBegin(sourceSceneId, destChapterId);
	}
	
	private boolean swapScenes(int sourceSceneId, DTScenePanel destDtScene){
		System.out.println("SceneTransferHandler.swapScenes(): ");
		int destSceneId = destDtScene.getScene().getId();
		return ScenePeer.swapScenes(sourceSceneId, destSceneId);		
	}

	protected Transferable createTransferable(JComponent comp) {
		sourceScene = (DTScenePanel) comp;
		return new SceneTransferable(sourceScene);
	}

	public int getSourceActions(JComponent comp) {
		return COPY_OR_MOVE;
	}

	protected void exportDone(JComponent comp, Transferable data, int action) {
		
	}

	public boolean canImport(JComponent comp, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (sceneFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}

	class SceneTransferable implements Transferable {
		private String sceneId;

		SceneTransferable(DTScenePanel pic) {
			sceneId = Integer.toString(pic.getScene().getId());
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return sceneId;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { sceneFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return sceneFlavor.equals(flavor);
		}
	}
}
