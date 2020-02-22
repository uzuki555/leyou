package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data,
                                             @PathVariable("type") Integer type){
        Boolean bool = userService.checkUser(data,type);
        if(bool==null){
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone")String phone){

        this.userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(User user,@RequestParam("code")String code ){
        this.userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/query")
    public ResponseEntity<User> queryByUser(@RequestParam("username")String username,@RequestParam("password")String password){
        User user =this.userService.queryByUser(username,password);
        if(user==null){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
