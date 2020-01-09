package com.crossrank.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
public class RankingPageController {

    @GetMapping("/rankings")
    public String resultsPage() {
        return "rankings";
    }

}