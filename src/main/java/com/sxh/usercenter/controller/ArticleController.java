package com.sxh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Model.VO.ArticleVO;
import com.sxh.usercenter.Model.domain.Article;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.request.article.ArticleGetRequest;
import com.sxh.usercenter.Model.request.team.TeamDeleteRequest;
import com.sxh.usercenter.common.BaseResponse;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.common.ResultUtils;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.ArticleService;
import com.sxh.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: usercenter
 * @description:
 * @author: SXH
 * @create: 2022-12-15 18:46
 **/
@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {
    @Resource
    ArticleService articleService;

    @Resource
    UserService userService;

    @PostMapping("/list")
    public BaseResponse<List<ArticleVO>> getArticleList(@RequestBody TeamDeleteRequest deleteRequest, HttpServletRequest request){
        if (deleteRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long teamId=deleteRequest.getT_id();
        QueryWrapper<Article> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        List<Article> articles=articleService.list(queryWrapper);
        List<ArticleVO> articleVOS=new ArrayList<>();
        for (Article article : articles) {
            ArticleVO articleVO=new ArticleVO();
            User user=userService.getById(article.getUserId());
            BeanUtils.copyProperties(article,articleVO);
            articleVO.setUserVO(userService.getUserVO(user));
            articleVOS.add(articleVO);
        }
        return ResultUtils.success(articleVOS);
    }
}
