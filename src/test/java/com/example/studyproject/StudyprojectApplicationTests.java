package com.example.studyproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
class StudyprojectApplicationTests {

    @Test
    void contextLoads() {
    }

}
