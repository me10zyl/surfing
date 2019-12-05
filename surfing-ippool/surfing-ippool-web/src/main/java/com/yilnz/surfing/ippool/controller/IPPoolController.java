package com.yilnz.surfing.ippool.controller;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Controller
public class IPPoolController {

    @Autowired
    private IPPoolService ipPoolService;


    @RequestMapping("/get_proxy")
    @ResponseBody
    public String get_proxy(){
        final HttpProxy proxy = ipPoolService.getOne();
        if (proxy != null) {
            return proxy.toString();
        }
        return null;
    }

    @RequestMapping("/list_proxy")
    @ResponseBody
    public String list_proxy(){
        final Set<HttpProxy> listFromRedis = ipPoolService.getListFromRedis();
        return listFromRedis.toString().replaceAll("\\[|\\]", "");
    }
}
