package ch.intertec.storybook.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.intertec.storybook.toolkit.Constants;
import ch.intertec.storybook.toolkit.I18N;
import ch.intertec.storybook.view.modify.GenderDialog;

public class GenderPeer {

    private static Logger logger = Logger.getLogger(GenderPeer.class);

    /**
     * Has to be package private!
     * @throws Exception
     */
    static void createTable() throws Exception {
		logger.debug("createTable: drop table " + Gender.TABLE_NAME
				+ " if exists");
        String sql = "drop table " + Gender.TABLE_NAME + " if exists";
        Statement stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);

        logger.debug("create table " + Gender.TABLE_NAME);
        sql = "create table " + Gender.TABLE_NAME + " ("
                + Gender.Column.ID + " identity primary key,"
                + Gender.Column.NAME + " varchar(1024),"
        		+ Gender.Column.CHILDHOOD + " int,"
        		+ Gender.Column.ADOLESCENCE + " int,"
        		+ Gender.Column.ADULTHOOD + " int,"
        		+ Gender.Column.RETIREMENT + " int"
        		+ ")";
        stmt = PersistenceManager.getInstance().getConnection().createStatement();
        stmt.execute(sql);
        
        // TODO insert default genders
		insertGender(stmt, 1, I18N.getMsg("msg.dlg.person.gender.male"));
		insertGender(stmt, 2, I18N.getMsg("msg.dlg.person.gender.female"));
//		sql = "insert into gender(id, name) values(2, '"
//				+ I18N.getMsg("msg.dlg.person.gender.female") + "')";
//        stmt = PersistenceManager.getInstance().getConnection().createStatement();
//        stmt.execute(sql);
    }

    private static void insertGender(Statement stmt, int id,String name) throws SQLException {
    	int childhood = Constants.LifeCycle.CHILDHOOD_YEARS.getYears();
    	int adolescence = Constants.LifeCycle.ADOLESCENCE_YEARS.getYears();
    	int adulthood = Constants.LifeCycle.ADULTHOOD_YEARS.getYears();
    	int retirement = Constants.LifeCycle.RETIREMENT_YEARS.getYears();
		String sql = "insert into gender ( "
			+ Gender.Column.ID + ", "
			+ Gender.Column.NAME + ", "
			+ Gender.Column.CHILDHOOD + ", "
			+ Gender.Column.ADOLESCENCE + ", "
			+ Gender.Column.ADULTHOOD + ", "
			+ Gender.Column.RETIREMENT + " "
			+ " ) values ( "
			+ id + ", "
			+ "'" + name + "', "
			+ "'" + childhood + "', "
			+ "'" + adolescence + "', "
			+ "'" + adulthood + "', "
			+ "'" + retirement + "' "
			+ " )";
	    stmt = PersistenceManager.getInstance().getConnection().createStatement();
	    stmt.execute(sql);
    }
    
    public static List<Gender> doSelectAll() {
        Statement stmt = null;
        ResultSet rs = null;
        List<Gender> list = new ArrayList<Gender>();
        try {
            StringBuffer sql = new StringBuffer("select * from ");
            sql.append(Gender.TABLE_NAME);
            sql.append(" order by ");
            sql.append(Gender.Column.NAME);
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                Gender gender = makeGender(rs);
                list.add(gender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static List<Integer> doSelectIds() {
        List<Integer> list = new ArrayList<Integer>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select " + Gender.Column.ID);
            sql.append(" from " + Gender.TABLE_NAME);
            sql.append(" order by " + Gender.Column.ID);
            stmt = PersistenceManager.getInstance().getConnection().createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                int genderNo = rs.getInt(Gender.Column.ID.toString());
                list.add(genderNo);
            }
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return list;
    }

    public static Gender doSelectById(int id) {
        Gender ch = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "select * from " + Gender.TABLE_NAME
                    + " where " + Gender.Column.ID + " = ?";
            stmt = PersistenceManager.getInstance().getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                ch = makeGender(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PersistenceManager.getInstance().closeResultSet(rs);
            PersistenceManager.getInstance().closeStatement(stmt);
        }
        return ch;
    }

	public static void makeOrUpdateGender(GenderDialog dlg, boolean edit)
			throws Exception {
		Gender gender;
		Gender old = null;
		if (edit) {
			gender = dlg.getGender();
			old = GenderPeer.doSelectById(gender.getId());
			old.markAsExpired();
		} else {
			gender = new Gender();
		}
		gender.setName(dlg.getNameTextField().getText());
		try {
			gender.setChildhood(Integer.parseInt(dlg.getChildhoodTextField()
					.getText()));
		} catch (NumberFormatException e) {
			gender.setChildhood(0);
		}
		try {
			gender.setAdolescence(Integer.parseInt(dlg
					.getAdolescenceTextField().getText()));
		} catch (NumberFormatException e) {
			gender.setAdolescence(0);
		}
		try {
			gender.setAdulthood(Integer.parseInt(dlg.getAdulthoodTextField()
					.getText()));
		} catch (NumberFormatException e) {
			gender.setAdulthood(0);
		}
		try {
			gender.setRetirement(Integer.parseInt(dlg.getRetirementTextField()
					.getText()));
		} catch (NumberFormatException e) {
			gender.setRetirement(0);
		}
		gender.save();
		PCSDispatcher.getInstance().firePropertyChange(
				PCSDispatcher.Property.GENDER.toString(), old, gender);
	}

	private static Gender makeGender(ResultSet rs) throws SQLException {
		Gender gender = new Gender(rs.getInt(Gender.Column.ID.toString()));
		gender.setName(rs.getString(Gender.Column.NAME.toString()));
		gender.setChildhood(rs.getInt(Gender.Column.CHILDHOOD.toString()));
		gender.setAdolescence(rs.getInt(Gender.Column.ADOLESCENCE.toString()));
		gender.setAdulthood(rs.getInt(Gender.Column.ADULTHOOD.toString()));
		gender.setRetirement(rs.getInt(Gender.Column.RETIREMENT.toString()));
		return gender;
	}

	public static boolean doDelete(Gender gender) throws Exception {
		boolean ret = false;
		if (gender != null) {
			Statement stmt = null;
			try {
				// update each character whose gender is the one to delete to 'man'
				String sql = "update " + SbCharacter.TABLE_NAME
						+ " set gender_id = " + Gender.MALE + " where "
						+ SbCharacter.Column.GENDER_ID + " = " + gender.getId();
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				stmt.execute(sql);

				// delete gender
				sql = "delete from " + Gender.TABLE_NAME + " where "
						+ Gender.Column.ID + " = " + gender.getId();
				stmt = PersistenceManager.getInstance().getConnection()
						.createStatement();
				stmt.execute(sql);
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				PersistenceManager.getInstance().closeStatement(stmt);
			}

			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.GENDER.toString(), gender, null);
		}
		return ret;
	}
	
	public static void makeCopy(Gender gender) {
		try {
			Gender copy = new Gender();
			copy.setName(DbPeer.getCopyString(gender.getName()));
			copy.save();
			PCSDispatcher.getInstance().firePropertyChange(
					PCSDispatcher.Property.GENDER, null, gender);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
