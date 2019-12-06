package com.yilnz.surfing.ippool.controller;

import com.yilnz.surfing.core.proxy.HttpProxy;
import com.yilnz.surfing.ippool.service.impl.IPPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.ws.WebServiceRefs;
import java.util.Set;

@Controller
public class IPPoolController {

    @Autowired
    private IPPoolService ipPoolService;

    @Autowired
    private IPPoolService ipPoolServiceGFW;


    @RequestMapping("/get_proxy")
    @ResponseBody
    public String get_proxy(@RequestParam(value = "gfw", required = false) Integer gfw){
        IPPoolService service = getIpPoolService(gfw);
        final HttpProxy proxy = service.getOne();
        if (proxy != null) {
            return proxy.toString();
        }
        return null;
    }

    private IPPoolService getIpPoolService(@RequestParam(value = "gfw", required = false) Integer gfw) {
        IPPoolService service;
        if (gfw != null && gfw.equals(1)) {
            service = ipPoolServiceGFW;
            service.setGFW(true);
        } else {
            service = ipPoolService;
            service.setGFW(false);
        }
        return service;
    }

    @RequestMapping("/list_proxy")
    @ResponseBody
    public String list_proxy(@RequestParam(value = "gfw", required = false) Integer gfw){
        IPPoolService service = getIpPoolService(gfw);
        final Set<HttpProxy> listFromRedis = service.getListFromRedis();
        return listFromRedis.toString().replaceAll("\\[|\\]", "");
    }
}
