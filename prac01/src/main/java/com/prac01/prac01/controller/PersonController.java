package com.prac01.prac01.controller;

import com.prac01.prac01.domain.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @GetMapping("/myInfo")
    public Person getPerson() {
        Person person = new Person();
        person.setName("TaeRam");
        person.setAge(25);
        person.setAddress("경기");
        person.setJob("백수");
        return person;
    }
}
