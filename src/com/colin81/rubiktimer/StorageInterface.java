package com.colin81.rubiktimer;

import java.util.List;

public interface StorageInterface {

	int addProfile(Profile profile) throws Exception;

	int addPuzzle(Puzzle puzzle) throws Exception;

	int addSolve(Solve solve) throws Exception;

	Solve getFastestSolve(Profile profile) throws Exception;

	Solve getSlowestSolve(Profile profile) throws Exception;

	List<Solve> getSolves(Profile profile) throws Exception;

}
