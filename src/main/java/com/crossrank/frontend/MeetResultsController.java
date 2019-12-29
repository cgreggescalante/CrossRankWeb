package com.crossrank.frontend;

import com.crossrank.backend.Fetcher;
import com.crossrank.backend.Race;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetResultsController {

    @CrossOrigin(origins = "http://localhost:9000")
    @GetMapping("/api/meet/results")
    public Race race(@RequestParam(required = false, defaultValue = "363847") int raceId,
                     @RequestParam(required = false, defaultValue = "680009") int resultsId) {
        return Fetcher.GetRaces(raceId, resultsId, 0).get(0);
    }
}
