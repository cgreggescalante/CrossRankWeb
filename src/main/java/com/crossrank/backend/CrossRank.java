/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.crossrank.backend;

import com.crossrank.LoadingBar;
import com.crossrank.backend.datatypes.*;
import com.crossrank.backend.serialization.*;

import java.util.*;


public class CrossRank {
    private MeetIndex meetIndex;

    private MeetResults meetResults;

    private Rankings rankings;

    public static void main(String[] args) {
        CrossRank crossRank = new CrossRank();

        crossRank.UpdateRankings();

        crossRank.SaveRankings();
    }

    public void UpdateRankings() {
        BuildIndex();
        GetResults();

        ScoreMeets();
    }

    public void SaveRankings() {
        System.out.println("SAVING RANKINGS");

        PersonSerializer.SaveRunners(rankings.getRunners());

        RaceSerializer.SaveRaces(meetResults.getRaces());

        RankingsSerializer.SaveRankings(rankings);
        RankingsSerializer.SaveRunnerDirectory(rankings.getRunnerDirectory());
        RankingsSerializer.SaveSortedRankings(rankings.getSortedRankingsBoys(), rankings.getSortedRankingsGirls());
    }

    private void BuildIndex() {
        // Attempts to load the MeetIndex
        meetIndex = MeetIndexSerializer.LoadMeetIndex();

        // If no index was loaded, creates a new index and compiles the 2019 season, saves
        if (meetIndex == null) {
            System.out.println("NO MEET INDEX SAVED");

            meetIndex = new MeetIndex();
            meetIndex.CompileSeason(2019);

            MeetIndexSerializer.SaveMeetIndex(meetIndex);
        } else {
            System.out.println("EXISTING MEET INDEX LOADED");
        }
    }


    private void GetResults() {
        // Attempts to load the meetResults
        meetResults = MeetResultsSerializer.LoadMeetResults();

        // If no results were loaded, generates new results
        if (meetResults == null) {
            System.out.println("LOADING RESULTS");

            meetResults = new MeetResults();

            // LoadingBar used to provide a visual indicator as to the progress of the results loading
            LoadingBar loadingBar = new LoadingBar(50, meetIndex.getMeets().size());

            // Iterates through the meetId, resultIds stored in the meetIndex.
            for (Map.Entry<Integer, List<Integer>> entry : meetIndex.getMeets().entrySet()) {

                // Generates the Race objects containing the results for the meet and
                // all races ran at that meet
                List<Race> newRaces = MeetResults.GetRaces(entry.getKey(), entry.getValue());

                meetResults.getRaces().addAll(newRaces);

                loadingBar.updateProgress();
            }

            loadingBar.end();

            // Sorts the races in chronological order so they can be properly scored
            meetResults.getRaces().sort(Comparator.comparing(Race::getMeetDate));

            // Saves the MeetResults object to a file
            MeetResultsSerializer.SaveMeetResults(meetResults);
        } else {
            System.out.println("EXISTING MEET RESULTS LOADED");
        }
    }

    private void ScoreMeets() {
        rankings = new Rankings();

        rankings.ScoreRaces(meetResults.getRaces());
    }
}
