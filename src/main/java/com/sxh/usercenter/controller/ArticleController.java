package com.sxh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sxh.usercenter.Mapper.ArticleMapper;
import com.sxh.usercenter.Model.VO.ArticleVO;
import com.sxh.usercenter.Model.domain.Article;
import com.sxh.usercenter.Model.domain.Team;
import com.sxh.usercenter.Model.domain.User;
import com.sxh.usercenter.Model.request.article.ArticleDeleteRequest;
import com.sxh.usercenter.Model.request.article.ArticleGetRequest;
import com.sxh.usercenter.Model.request.article.ArticlePublishRequest;
import com.sxh.usercenter.Model.request.team.TeamDeleteRequest;
import com.sxh.usercenter.common.BaseResponse;
import com.sxh.usercenter.common.ErrorCode;
import com.sxh.usercenter.common.ResultUtils;
import com.sxh.usercenter.exception.BusinessException;
import com.sxh.usercenter.service.ArticleService;
import com.sxh.usercenter.service.TeamService;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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

    @Resource
    TeamService teamService;

    @Resource
    ArticleMapper articleMapper;

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
        List<ArticleVO> vos = articleVOS.stream().sorted((a, b) -> b.getPublishTime().compareTo(a.getPublishTime())).toList();
        return ResultUtils.success(vos);
    }

    @PostMapping("/publish")
    public BaseResponse<Boolean> publishArticle(@RequestBody ArticlePublishRequest publishRequest,HttpServletRequest request){
        if (publishRequest==null || publishRequest.getMainBody()==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Article article=new Article();
        article.setUserId(loginUser.getUserId());
        article.setMainBody(publishRequest.getMainBody());
        article.setTeamId(publishRequest.getTeamId());
        article.setPublishTime(new Date());
        boolean save = articleService.save(article);
        return ResultUtils.success(save);
    }

    @PostMapping("/like")
    public BaseResponse<Boolean> likeTheArticle(@RequestBody TeamDeleteRequest teamDeleteRequest){
        if (teamDeleteRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Article article=articleService.getById(teamDeleteRequest.getT_id());
        int likeNum = article.getLikeNum()+1;
        article.setLikeNum(likeNum);
        boolean b = articleService.updateById(article);
        return ResultUtils.success(b);
    }

    @PostMapping("/delete")
    public BaseResponse<Integer> deleteArticle(@RequestBody ArticleDeleteRequest deleteRequest, HttpServletRequest request){
        if (deleteRequest==null||request==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Article article=articleService.getById(deleteRequest.getId());
        User user=userService.getLoginUser(request);
        Team team=teamService.getById(deleteRequest.getTeamId());
        if (article.getUserId()!=user.getUserId() || team.getT_userId()!=user.getUserId())
            throw new BusinessException(ErrorCode.NO_AUTH,"您无权删除文章");
        int i = articleMapper.deleteById(article);
        return ResultUtils.success(i);
    }
}
