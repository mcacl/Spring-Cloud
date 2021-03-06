package com.example.use1.ctrl;

import com.example.use1.iface.Iface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * 创建时间:2019/03/28
 * 创建人:pmc
 * 描述:
 */
@RestController
@RequestMapping(value = "/test")
@Api("服务列表")//swagger 支持
public class Test {
    @Autowired
    private Iface iface;//远程调用注册中心服务有两种方式 1.feign 就要写iface那种接口 2。resttemplete+ribbion 要写Bean 配置类且注册成bean

    @Autowired
    private RestTemplate restTemplate;//使用第2种方式 resttemplete+ribbion 需要在config类中声明bean

    @Value("${server.port}")
    String port;

    @ApiOperation("测试地址是否通畅 返回服务端口")
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String test() {
        return "use1服务使用者:端口" + port;
    }

    @ApiOperation("测试远程调用接口")
    @RequestMapping(value = "/hello/{name}")
    public String testuse(@ApiParam("名称") @PathVariable(name = "name") String name) {
        return iface.hello(name);
    }

    @RequestMapping(value = "/word")
    public String word() {
        return iface.word();
    }

    @RequestMapping(value = "/ld")
    public Object getld(@RequestBody(required = false) Object object) {
        Object object1 = iface.getldcode(object);
        return object1;
    }

    @RequestMapping()
    public String wordrest() {
        //只有在config类中 restTemplate 加了LoadBalanced注解才能识别cloud1提供者
        Object res = restTemplate.getForObject("http://cloud1/test/word", String.class);
        return res.toString();
    }
}
