package com.yilnz.surfing.ippool.controller;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IPPoolController {

    @Autowired
    private IPPoolService ipPoolService;


    @RequestMapping("/get_proxy")
    @ResponseBody
    public HttpProxy get_proxy(){
        return ipPoolService.getOne();
    }
}
