package com.weds.devmanages.controller;

import com.weds.devmanages.service.sign.Signature;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("example")
public class ExampleController {

    @PostMapping(value = "test/{var1}/{var2}", produces = MediaType.ALL_VALUE)
    @Signature
    public String myController(@PathVariable String var1, @PathVariable String var2,
                               @RequestParam String var3, @RequestParam String var4, @RequestBody User user) {

        return String.join(",", var1, var2, var3, var4, user.toString());
    }

    @Data
    private static class User {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("name", name)
                    .append("age", age)
                    .toString();
        }
    }
}