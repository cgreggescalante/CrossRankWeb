package com.crossrank.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
public class IndexPageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

}