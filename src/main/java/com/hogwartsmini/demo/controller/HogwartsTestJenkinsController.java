package com.hogwartsmini.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dto.AddHogwartsTestUserDto;
import com.hogwartsmini.demo.dto.BuildDto;
import com.hogwartsmini.demo.dto.UpdateHogwartsTestUserDto;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.dto.jenkins.AddHogwartsTestJenkinsDto;
import com.hogwartsmini.demo.dto.jenkins.QueryHogwartsTestJenkinsListDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestJenkinsService;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import com.hogwartsmini.demo.util.JenkinsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:14
 **/
@Api(tags = "霍格沃兹测试学院-Jenkins管理模块")
@RestController
@RequestMapping("hogwartsJenkins")
@Slf4j
public class HogwartsTestJenkinsController {

    @Autowired
    private HogwartsTestJenkinsService hogwartsTestJenkinsService;

    @Autowired
    private TokenDb tokenDb;

    @ApiOperation("添加Jenkins接口")
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    @PostMapping()
    public ResultDto<HogwartsTestJenkins> save(HttpServletRequest request, @RequestBody AddHogwartsTestJenkinsDto addHogwartsTestJenkinsDto){

        HogwartsTestJenkins hogwartsTestJenkins = new HogwartsTestJenkins();

        log.info("AddHogwartsTestJenkinsDto=== "+ JSONObject.toJSONString(addHogwartsTestJenkinsDto));
        //校验Jenkins的baseUrl是否为空，为空给出提示
        if(StringUtils.isEmpty(addHogwartsTestJenkinsDto.getUrl())){
            return ResultDto.fail("baseUrl不能为空");
        }
        //从客户端请求的header中获取token，并根据token获取用户信息 -- 为什么要从这里获取？？？
        TokenDto tokenDto = tokenDb.getUserInfo(request.getHeader(UserBaseStr.LOGIN_TOKEN));

        //注意：字段的类型、名称均需一致
        BeanUtils.copyProperties(addHogwartsTestJenkinsDto,hogwartsTestJenkins);
        //设置用户id
        hogwartsTestJenkins.setCreateUserId(tokenDto.getUserId());
        //打印日志，方便调试
        log.info("=== "+ JSONObject.toJSONString(hogwartsTestJenkins));
        return hogwartsTestJenkinsService.save(hogwartsTestJenkins);

    }


    @ApiOperation("添加Jenkins接口")
    @GetMapping()
    public ResultDto<PageTableResponse<HogwartsTestJenkins>> list(HttpServletRequest request,
                                               PageTableRequest<QueryHogwartsTestJenkinsListDto> pageTableRequest){

        //从客户端请求的header中获取token，并根据token获取用户信息 -- 为什么要从这里获取？？？
        TokenDto tokenDto = tokenDb.getUserInfo(request.getHeader(UserBaseStr.LOGIN_TOKEN));

        //获取分页请求中的查询参数对象
        //将当前用户id作为查询条件，防止用户数据混乱

        if(Objects.isNull(pageTableRequest.getParams())){
            pageTableRequest.setParams(new QueryHogwartsTestJenkinsListDto());
        }
        pageTableRequest.getParams().setCreateUserId(tokenDto.getUserId());

        return hogwartsTestJenkinsService.list(pageTableRequest);

    }

}
