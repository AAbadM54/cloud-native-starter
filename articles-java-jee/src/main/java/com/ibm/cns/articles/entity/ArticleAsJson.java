package com.ibm.cns.articles.entity;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.cns.articles.entity.Article;

@ApplicationScoped
public class ArticleAsJson {

	public ArticleAsJson() {}
	
	public JsonObject createJson(Article article) {
		return Json.createObjectBuilder().add("id", article.id).add("title", article.title).add("url", article.url)
				.add("author", article.author).build();
	}
}