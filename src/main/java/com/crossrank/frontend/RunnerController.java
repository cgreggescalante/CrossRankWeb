package com.crossrank.frontend;

import com.crossrank.backend.CrossRank;
import com.crossrank.backend.Person;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunnerController {

    @CrossOrigin(origins = "http://localhost:9000")
    @GetMapping("/api/runners")
    public Person person(@RequestParam(required = false, defaultValue = "0") int id) {
        return CrossRank.GetPerson(id);
    }
}
