package com.crossrank.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SuppressWarnings("SameReturnValue")
@Controller
public class RunnerPageController {

    @GetMapping("/runners")
    public String resultsPage(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "runners";
    }
}