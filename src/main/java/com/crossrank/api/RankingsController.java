package com.crossrank.api;

import com.crossrank.backend.CrossRank;
import com.crossrank.backend.datatypes.Rankings;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankingsController {

    @CrossOrigin(origins = "http://localhost:9000")
    @GetMapping("/api/runners/rankings")
    public Rankings rankings(@RequestParam(required = false, defaultValue = "1") int page,
                         @RequestParam(required = false, defaultValue = "10") int pageLength,
                         @RequestParam(required = false, defaultValue = "boys") String sex) {
        return CrossRank.GetRankings(page, pageLength, sex);
    }
}
