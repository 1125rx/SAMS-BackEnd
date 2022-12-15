package com.sxh.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxh.usercenter.Model.domain.Article;
import com.sxh.usercenter.service.ArticleService;
import com.sxh.usercenter.Mapper.ArticleMapper;
import org.springframework.stereotype.Service;

/**
* @author sxh
* @description 针对表【article(发布文章表)】的数据库操作Service实现
* @createDate 2022-12-15 18:45:05
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

}




