package za.co.armandkamffer.mamba.Controllers;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class HelloWorldController {

    @RequestMapping("/")
    String home() {
        return "Hello World! v0.0.7";
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldController.class, args);
    }

}