package com.colin81.rubiktimer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Methods to expose the database
 * 
 * @author Colin Gillespie
 * 
 */
public class DBConnector {

	private static Logger LOGGER = Logger
			.getLogger(DBConnector.class.getName());

	/* The id field for SQLite tables is stored in the hidden field rowid */

	private static final String PUZZLE_TABLE = "Puzzle";
	private static final String PUZZLE_NAME = "Name";
	private static final String PUZZLE_SCRAMBLE = "Scrambler";
	private static final String PUZZLE_IMAGE = "Image";

	private static final String PROFILE_TABLE = "Profile";
	private static final String PROFILE_PUZZLE = "Puzzle";
	private static final String PROFILE_NAME = "Name";
	private static final String PROFILE_DESC = "Description";

	private static final String SOLVE_TABLE = "Solve";
	private static final String SOLVE_PROFILE = "Profile";
	private static final String SOLVE_SCRAMBLE = "Scramble";
	private static final String SOLVE_TIME = "SolveTime";
	private static final String SOLVE_DATETIME = "Time";

	private final Connection connection;
	private final Statement statement;

	public DBConnector(final File database) throws SQLException,
			ClassNotFoundException {
		LOGGER.setLevel(Level.ALL);
		LOGGER.info("constructing");

		LOGGER.info("Loading sqlite connector");
		Class.forName("org.sqlite.JDBC");

		connection = DriverManager.getConnection("jdbc:sqlite:"
				+ database.getAbsolutePath());
		statement = connection.createStatement();

		initDB();
		// TODO: remove after debugging
		// printAll();
	}

	/**
	 * 
	 * @param profile
	 * @return
	 * @throws SQLException
	 */
	public int addProfile(final Profile profile) throws SQLException {
		final String qry = String.format(
				"INSERT INTO %s (%s, %s, %s) VALUES (%s, '%s', '%s')",
				PROFILE_TABLE, PROFILE_PUZZLE, PROFILE_NAME, PROFILE_DESC,
				profile.getPuzzle().getId(), profile.getName(),
				profile.getDescription());

		statement.executeUpdate(qry);
		return getLastInsertId(PROFILE_TABLE);
	}

	/**
	 * Adds a Puzzle to the database.
	 * 
	 * @param puzzle
	 * @return int - Returns the rowid of the added Puzzle.
	 * @throws SQLException
	 * @see Puzzle
	 */
	public int addPuzzle(final Puzzle puzzle) throws SQLException {
		/* check that puzzle is unique */
		try {
			for (final Puzzle p : getPuzzles()) {
				if (p.equals(puzzle)) {
					LOGGER.info("Puzzle already exists!");
					return p.getId();
				}
			}
		} catch (final NullPointerException e) {

		}

		final String qry = String.format(
				"INSERT INTO %s (%s, %s, %s) VALUES ('%s', '%s', '%s')",
				PUZZLE_TABLE, PUZZLE_NAME, PUZZLE_SCRAMBLE, PUZZLE_IMAGE,
				puzzle.getName(), puzzle.getScrambler(), puzzle.getImage());

		statement.executeUpdate(qry);
		return getLastInsertId(PUZZLE_TABLE);

	}

	/**
	 * Adds a Solve to the database.
	 * 
	 * @param solve
	 * @return The rowid of the added Solve.
	 * @throws SQLException
	 * @see Solve
	 */
	public int addSolve(final Solve solve) throws SQLException {
		// TODO: check for uniqueness of solve???
		// could be intensive - better handled by app?

		final String qry = String
				.format("INSERT INTO %s (%s, %s, %s, %s) VALUES ('%d', '%s', '%d', '%d')",
						SOLVE_TABLE, SOLVE_PROFILE, SOLVE_SCRAMBLE, SOLVE_TIME,
						SOLVE_DATETIME, solve.getProfile().getId(),
						solve.getScramble(), solve.getSolveTime(),
						solve.getDateTime());

		statement.executeUpdate(qry);
		return getLastInsertId(PUZZLE_TABLE);
	}

	/**
	 * Closes open database objects.
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		statement.close();
		connection.close();
	}

	@Override
	public void finalize() throws SQLException {
		close();
	}

	/**
	 * Returns the rowid for the newest row in a table.
	 * 
	 * @param table
	 * @return rowid or 0 if no rows exist.
	 * @throws SQLException
	 */
	private int getLastInsertId(final String table) throws SQLException {
		final String qry = "SELECT last_insert_rowid() FROM " + table;

		final ResultSet rs = statement.executeQuery(qry);
		return rs.getInt(1);
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Profile getProfile(final int id) throws SQLException {
		final String qry = String.format(
				"SELECT rowid, * FROM %s WHERE rowid=%d", PROFILE_TABLE, id);

		final ResultSet rs = statement.executeQuery(qry);
		final int rowid = rs.getInt("rowid");
		if (rowid > 0) {
			final Profile p = new Profile(rowid);
			p.setName(rs.getString(PROFILE_NAME));
			p.setPuzzle(getPuzzle(rs.getInt(PROFILE_PUZZLE)));
			p.setDescription(rs.getString(PROFILE_DESC));

			// TODO remove
			System.out.println(p);

			return p;
		}

		LOGGER.severe("Profile [" + id + "] doesn't exist!");
		return null;
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @see Profile
	 */
	public List<Profile> getProfiles() throws SQLException {
		final String qry = String.format("SELECT rowid, * FROM %s",
				PROFILE_TABLE);

		final ResultSet rs = statement.executeQuery(qry);
		final List<Profile> profiles = new ArrayList<Profile>();

		while (rs.next()) {
			final Profile p = new Profile(rs.getInt("rowid"));
			p.setPuzzle(getPuzzle(rs.getInt(PROFILE_PUZZLE)));
			p.setName(rs.getString(PROFILE_NAME));
			p.setDescription(rs.getString(PROFILE_DESC));
			profiles.add(p);
		}

		return profiles;
	}

	/**
	 * Returns the Puzzle at the given rowid or null if it doesn't exist.
	 * 
	 * @param id
	 * @return The puzzle at the specified rowid.
	 * @throws SQLException
	 * @see Puzzle
	 */
	public Puzzle getPuzzle(final int id) throws SQLException {
		final String qry = String.format(
				"SELECT rowid, * FROM %s WHERE rowid=%d", PUZZLE_TABLE, id);
		LOGGER.info(qry);

		final ResultSet rs = statement.executeQuery(qry);
		final int rowid = rs.getInt("rowid");
		if (rowid > 0) {
			final Puzzle p = new Puzzle(rowid);
			p.setName(rs.getString(PUZZLE_NAME));
			p.setScrambler(rs.getString(PUZZLE_SCRAMBLE));
			p.setImage(rs.getString(PUZZLE_IMAGE));

			// TODO remove
			System.out.println(p);

			return p;
		}

		LOGGER.severe("Puzzle [" + id + "] doesn't exist!");
		return null;
	}

	/**
	 * Retrieves all the puzzles in order of their rowid.
	 * 
	 * @return All the puzzles or an empty List if none exist.
	 * @throws SQLException
	 * @see Puzzle
	 */
	public List<Puzzle> getPuzzles() throws SQLException {
		final String qry = String.format("SELECT rowid, * FROM %s",
				PUZZLE_TABLE);

		final ResultSet rs = statement.executeQuery(qry);
		final List<Puzzle> puzzles = new ArrayList<Puzzle>();

		while (rs.next()) {
			final Puzzle p = new Puzzle(rs.getInt("rowid"));
			p.setName(rs.getString(PUZZLE_NAME));
			p.setScrambler(rs.getString(PUZZLE_SCRAMBLE));
			puzzles.add(p);
		}

		// TODO: remove print
		for (final Puzzle p : puzzles) {
			System.out.println(p.toString());
		}

		return puzzles;
	}

	/**
	 * Retrieves all the solves in order of their rowid.
	 * 
	 * @return All the solves or an empty List if none exist.
	 * @throws SQLException
	 * @see Solve
	 */
	public List<Solve> getSolves() throws SQLException {
		final String qry = String.format("SELECT * FROM %s", SOLVE_TABLE);
		return getSolvesFromQuery(qry);
	}

	public List<Solve> getSolvesForProfile(final Profile p) throws SQLException {
		final String qry = String.format("SELECT * FROM %s WHERE %s=%d",
				SOLVE_TABLE, SOLVE_PROFILE, p.getId());
		return getSolvesFromQuery(qry);
	}

	private List<Solve> getSolvesFromQuery(final String qry)
			throws SQLException {

		final ResultSet rs = statement.executeQuery(qry);
		final List<Solve> solves = new ArrayList<Solve>();

		while (rs.next()) {
			final Solve s = new Solve(rs.getInt("rowid"));
			s.setScramble(rs.getString(SOLVE_SCRAMBLE));
			s.setSolveTime(rs.getInt(SOLVE_TIME));
			s.setDateTime(rs.getInt(SOLVE_DATETIME));
			s.setProfile(getProfile(rs.getInt(SOLVE_PROFILE)));
			solves.add(s);
		}

		return solves;
	}

	/**
	 * Creates tables for the database.
	 * 
	 * @throws SQLException
	 */
	private void initDB() throws SQLException {
		LOGGER.info("init database");
		final String createPuzzle = String.format(
				"CREATE TABLE IF NOT EXISTS %s (%s TEXT NOT NULL,"
						+ " %s TEXT, %s TEXT);", PUZZLE_TABLE, PUZZLE_NAME,
				PUZZLE_SCRAMBLE, PUZZLE_IMAGE);
		statement.executeUpdate(createPuzzle);

		final String createProfile = String
				.format("CREATE TABLE IF NOT EXISTS %s "
						+ "(%s INTEGER NOT NULL, %s TEXT NOT NULL, %s TEXT NULLABLE);",
						PROFILE_TABLE, PROFILE_PUZZLE, PROFILE_NAME,
						PROFILE_DESC);
		statement.executeUpdate(createProfile);

		final String createSolve = String.format(
				"CREATE TABLE IF NOT EXISTS %s (%s	INTEGER	NOT NULL,"
						+ "%s	TEXT," + "%s	INTEGER	NOT NULL,"
						+ "%s	INTEGER NOT NULL);", SOLVE_TABLE, SOLVE_PROFILE,
				SOLVE_SCRAMBLE, SOLVE_TIME, SOLVE_DATETIME);
		statement.executeUpdate(createSolve);
	}

	/**
	 * prints contents of the db for debugging purposes
	 */
	public void printAll() {
		try {
			for (final Puzzle p : getPuzzles()) {
				System.out.println(p.toString());
			}
			System.out
					.println("\n---------------------------------------------\n");
			for (final Profile p : getProfiles()) {
				System.out.println(p.toString());
			}
			System.out
					.println("\n---------------------------------------------\n");
			for (final Solve s : getSolves()) {
				System.out.println(s.toString());
			}
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final NullPointerException e1) {

		}
	}
}
