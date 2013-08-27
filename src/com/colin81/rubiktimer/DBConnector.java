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
public class DBConnector implements StorageInterface {

	private static Logger LOGGER = Logger
			.getLogger(DBConnector.class.getName());

	/* The id field for SQLite tables is stored in the hidden field rowid */

	private static final String PUZZLE_TABLE = "Puzzle";
	private static final String PUZZLE_NAME = "Puz_Name";
	private static final String PUZZLE_SCRAMBLE = "Scrambler";
	private static final String PUZZLE_IMAGE = "Image";

	private static final String PROFILE_TABLE = "Profile";
	private static final String PROFILE_PUZZLE = "Puzzle";
	private static final String PROFILE_NAME = "Pro_Name";
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
		LOGGER.info("constructing: " + database.getAbsolutePath());

		LOGGER.info("Loading sqlite connector");
		Class.forName("org.sqlite.JDBC");

		connection = DriverManager.getConnection("jdbc:sqlite:"
				+ database.getAbsolutePath());
		statement = connection.createStatement();

		initDB();
	}

	/**
	 * 
	 * @param profile
	 * @return
	 * @throws SQLException
	 */
	@Override
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
	@Override
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
	@Override
	public int addSolve(final Solve solve) throws SQLException {
		// TODO: check for uniqueness of solve???
		// could be intensive - better handled by app?

		final String qry = String.format(
				"INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, '%s', %d, %d)",
				SOLVE_TABLE, SOLVE_PROFILE, SOLVE_SCRAMBLE, SOLVE_TIME,
				SOLVE_DATETIME, solve.getProfile().getId(),
				solve.getScramble(), solve.getSolveTime(), solve.getDateTime());

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

	@Override
	public Solve getFastestSolve(final Profile p) throws SQLException {
		final String qry = String.format(
				"SELECT rowid, * FROM %s WHERE %s=%d ORDER BY %s LIMIT 1",
				SOLVE_TABLE, SOLVE_PROFILE, p.getId(), SOLVE_TIME);

		return getSolvesFromQry(p, qry).get(0);
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
		final String qry = String
				.format("SELECT %1$s.rowid AS id_a, %2$s.rowid AS id_b, * FROM %1$s INNER JOIN %2$s ON id_a=id_b",
						PROFILE_TABLE, PUZZLE_TABLE);
		LOGGER.info(qry);
		final ResultSet rs = statement.executeQuery(qry);
		final List<Profile> profiles = new ArrayList<Profile>();

		while (rs.next()) {
			final Profile p = new Profile(rs.getInt("id_a"));
			p.setName(rs.getString(PROFILE_NAME));
			p.setDescription(rs.getString(PROFILE_DESC));
			final Puzzle puzzle = new Puzzle(rs.getInt("id_b"));
			puzzle.setName(rs.getString(PUZZLE_NAME));
			puzzle.setScrambler(rs.getString(PUZZLE_SCRAMBLE));
			puzzle.setImage(rs.getString(PUZZLE_IMAGE));
			p.setPuzzle(puzzle);
			profiles.add(p);

			// TODO remove
			System.out.println(p);
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

		return puzzles;
	}

	@Override
	public Solve getSlowestSolve(final Profile p) throws SQLException {
		final String qry = String.format(
				"SELECT rowid, * FROM %s WHERE %s=%d ORDER BY %s DESC LIMIT 1",
				SOLVE_TABLE, SOLVE_PROFILE, p.getId(), SOLVE_TIME);

		return getSolvesFromQry(p, qry).get(0);
	}

	@Override
	public List<Solve> getSolves(final Profile p) throws SQLException {
		final String qry = String.format("SELECT rowid, * FROM %s WHERE %s=%d",
				SOLVE_TABLE, SOLVE_PROFILE, p.getId());

		return getSolvesFromQry(p, qry);
	}

	private List<Solve> getSolvesFromQry(final Profile p, final String qry)
			throws SQLException {
		final List<Solve> solves = new ArrayList<Solve>();

		final ResultSet rs = statement.executeQuery(qry);
		while (rs.next()) {
			final Solve solve = new Solve(rs.getInt("rowid"));
			solve.setScramble(rs.getString(SOLVE_SCRAMBLE));
			solve.setSolveTime(rs.getInt(SOLVE_TIME));
			solve.setDateTime(rs.getInt(SOLVE_DATETIME));
			solve.setProfile(p);
			solves.add(solve);
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

		final String createProfile = String.format(
				"CREATE TABLE IF NOT EXISTS %s "
						+ "(%s INTEGER NOT NULL, %s TEXT NOT NULL, %s TEXT, "
						+ "FOREIGN KEY(%s) REFERENCES %s(rowid));",
				PROFILE_TABLE, PROFILE_PUZZLE, PROFILE_NAME, PROFILE_DESC,
				PROFILE_PUZZLE, PUZZLE_TABLE);
		statement.executeUpdate(createProfile);

		final String createSolve = String.format(
				"CREATE TABLE IF NOT EXISTS %s (%s	INTEGER	NOT NULL, "
						+ "%s TEXT, " + "%s INTEGER NOT NULL, "
						+ "%s INTEGER NOT NULL, "
						+ "FOREIGN KEY(%s) REFERENCES %s(rowid));",
				SOLVE_TABLE, SOLVE_PROFILE, SOLVE_SCRAMBLE, SOLVE_TIME,
				SOLVE_DATETIME, SOLVE_PROFILE, PROFILE_TABLE);
		statement.executeUpdate(createSolve);

	}

	@Override
	public int removeSolve(final Solve solve) throws SQLException {
		final String qry = String.format("DELETE FROM %s WHERE rowid=%d",
				SOLVE_TABLE, solve.getId());

		return statement.executeUpdate(qry);
	}
}