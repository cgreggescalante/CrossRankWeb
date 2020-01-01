package com.crossrank.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RankingPageController {

    @GetMapping("/rankings")
    public String resultsPage() {
        return "rankings";
    }

}