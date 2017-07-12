package com.nieyue.service;

import java.util.List;

import com.nieyue.bean.Article;

/**
 * 文章逻辑层接口
 * @author yy
 *
 */
public interface ArticleService {
	/** 新增文章 */	
	public boolean addArticle(Article article) ;	
	/** 删除文章 */	
	public boolean delArticle(Integer articleId) ;
	/** 更新文章*/	
	public boolean updateArticle(Article article);
	/** 装载文章 */	
	public Article loadArticle(Integer articleId);
	/** 装载文章Small */	
	public Article loadSmallArticle(Integer articleId);
	/** 文章总共数目 */	
	public int countAll(String status,Integer acountId,String type,Integer isRecommend,Integer fixedRecommend);
	/** 分页文章信息 */
	public List<Article> browsePagingArticle(
			String status,
			Integer acountId,String type,Integer isRecommend,Integer fixedRecommend,
			int pageNum,int pageSize,String orderName,String orderWay) ;
	/** 文章类型 */
	public List<String> browseArticleTypeList(Integer acountId) ;		
}
