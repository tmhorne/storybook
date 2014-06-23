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
package ch.intertec.storybook.model;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import ch.intertec.storybook.action.DisposeDialogAction;
import ch.intertec.storybook.main.MainFrame;
import ch.intertec.storybook.model.Scene.Status;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.toolkit.ProjectTools;
import ch.intertec.storybook.toolkit.swing.SwingTools;
import ch.intertec.storybook.toolkit.swing.WaitDialog;

public class PersistenceManager {

    private static Logger logger = Logger.getLogger(PersistenceManager.class);
    private static PersistenceManager thePersistenceManager;
    private String databaseName;
    private boolean init;
    private boolean openOnlyIfExists;
    private Connection connection;
    private File file;

    private PersistenceManager() {
        // make the constructor private
        init = false;
        connection = null;
        databaseName = null;
    }

    public void open(File file) {
    	this.file = file;
        this.databaseName = ProjectTools.getDatabaseName(file);
        this.openOnlyIfExists = true;
        this.init = true;
        try {
            getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("open db, databaseName=" + this.databaseName);
    }

    public void create(File file) {
    	this.file = file;
        this.databaseName = ProjectTools.getDatabaseName(file);
        this.openOnlyIfExists = false;
        this.init = true;
        try {
            getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("create db, databaseName=" + this.databaseName);
    }

    public void init(String databaseName) {
        init(databaseName, false);
    }

    @Deprecated
    public void init(String databaseName, boolean onlyOpenIfExists) {
        this.databaseName =
                ProjectTools.getProjectDir() + File.separator + databaseName;
        this.openOnlyIfExists = onlyOpenIfExists;
        this.connection = null;
        this.init = true;
        try {
            getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("init db, databaseName=" + this.databaseName);
    }

	public void initDbModel() {
		initDbModel(false);
	}
    
    public void initDbModel(boolean isDemo) {
        try {
            //  create tables
            InternalPeer.createTable();
            PartPeer.createTable();
            StrandPeer.createTable();
            ScenePeer.createTable();
            SceneLinkStrandPeer.createTable();
            SbCharacterPeer.createTable();
            SceneLinkSbCharacterPeer.createTable();
            LocationPeer.createTable();
            SceneLinkLocationPeer.createTable();
            ChapterPeer.createTable();
            IdeasPeer.createTable();
            GenderPeer.createTable();
            TagPeer.createTable();
            TagLinkPeer.createTable();

            // set DB model version
            InternalPeer.setDbModelVersion();

            // create default strand
            Strand strand = new Strand();
            strand.setName(I18N.getMsg("db.init.strand.name"));
            strand.setAbbreviation(I18N.getMsg("db.init.strand.abbr"));
            strand.setColor(SwingTools.getNiceBlue());
            strand.setSort(0);
            strand.save();

            // create default part
            Part part = new Part();
            part.setNumber(1);
            part.setName(I18N.getMsg("db.init.part"));
            part.save();

			if (!isDemo) {
				// create first scene
				Scene scene = new Scene();
				scene.setDate(new Date(new java.util.Date().getTime()));
				scene.setStrand(strand);
				scene.setChapterId(-1);
				scene.setTitle("");
				scene.setText("");
				scene.setStatus(Status.OUTLINE.ordinal());
				scene.save();

				// create first chapter
				Chapter chapter = new Chapter();
				chapter.setChapterNo(1);
				chapter.setTitle(I18N.getMsg("msg.common.chapter") + " 1");
				chapter.setPart(part);
				chapter.setDescription("");
				chapter.save();
			}
            
			// set default view scale factors
			InternalPeer.setScaleFactorDefaults();
            
            // set and save default view scales
            MainFrame.getInstance().getContentPanelType().setDefaultScales();
            ProjectTools.saveAllScales();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void saveTables() {
		saveTables(500);
	}

	public void saveTables(int showClockDelay) {
		if (!isConnectionOpen()) {
			return;
		}
		if (showClockDelay == -1) {
			return;
		}
		
		MainFrame mainFrame = MainFrame.getInstance();
		WaitDialog dlg = new WaitDialog(mainFrame,
				I18N.getMsg("msg.file.saving"));
		Timer timer = new Timer(showClockDelay, new DisposeDialogAction(dlg));
		timer.setRepeats(false);
		timer.start();
		SwingTools.showModalDialog(dlg, mainFrame);
	}
    
    public static PersistenceManager getInstance() {
        if (thePersistenceManager == null) {
            thePersistenceManager = new PersistenceManager();
        }
        return thePersistenceManager;
    }

    public Connection getConnection() {
        if (!init) {
            return null;
        }
        if (connection == null) {
            String connectionStr = "jdbc:h2:" + databaseName;
            if (openOnlyIfExists) {
                connectionStr = connectionStr + ";IFEXISTS=TRUE";
            }
            logger.info("connect to: " + connectionStr);
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(
                        connectionStr, "sa", "");
            } catch (Exception e) {
                logger.error(e);
                SwingTools.showException(e);
            }
        }
        return connection;
    }

    public void closeConnection() {
        if (!isConnectionOpen()) {
            return;
        }
        try {
            this.connection.close();
            this.init = false;
            this.connection = null;
            this.databaseName = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

	public File getFile() {
		return file;
	}
    
    public boolean isConnectionOpen() {
        return connection != null;
    }

    /**
     * Closes the result set
     *
     * @param result The ResultSet that needs to close
     */
    public void closeResultSet(ResultSet result) {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException se) {
            logger.error("### PersistenceManager, SQLException, closing result set", se);
        }
    }

    /**
     * Closes the prepare statement
     *
     * @param prepare The PreparedStatement that needs to close
     */
    public void closePrepareStatement(PreparedStatement prepare) {
        try {
            if (prepare != null) {
                prepare.close();
            }
        } catch (SQLException se) {
            logger.error("### PersistenceManager, SQLException, closing prepare statement", se);
        }
    }

    /**
     * Closes the statement
     *
     * @param stmt The Statement that needs to close
     */
    public void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
            logger.error("### PersistenceManager, SQLException, closing prepare statement", se);
        }
    }

    public int getGeneratedId(PreparedStatement stmt) throws SQLException {
        int retour = -1;
        ResultSet rs = null;
        try {
            rs = stmt.getGeneratedKeys();
            int count = 0;
            while (rs.next()) {
                if (count > 0) {
                    throw new SQLException("error: got more than one id");
                }
                retour = rs.getInt(1);
                ++count;
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            this.closeResultSet(rs);
        }
        return retour;
    }
}
