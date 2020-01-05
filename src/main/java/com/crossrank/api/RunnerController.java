package com.crossrank.api;

import com.crossrank.backend.CrossRank;
import com.crossrank.backend.datatypes.Person;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunnerController {

    @CrossOrigin(origins = "http://localhost:9000")
    @GetMapping("/api/runners")
    public Person person(@RequestParam(required = false, defaultValue = "Conor Gregg Escalante") String name) {
        return CrossRank.GetPerson(name);
    }
}
