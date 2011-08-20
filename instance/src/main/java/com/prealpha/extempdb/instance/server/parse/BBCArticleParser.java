package com.prealpha.extempdb.instance.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;

public class BBCArticleParser implements ArticleParser {
	private final SimpleHttpClient httpClient;
	private Provider<Team> teamProvider;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
	
	@Inject
	private BBCArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		String title;
		Date date;
		String byline = null;
		List<String> paragraphs = new ArrayList<String>();
		
		try {
			Document document = getDocument(articleUrl.getUrl());
			
			//TITLE
			title=document.getElementsByClass("story-header").get(0).text();
			
			//DATE
			String dateString = document.getElementsByClass("date").get(0).text()+" "+document.getElementsByClass("time").get(0).text();
			try {
				date = DATE_FORMAT.parse(dateString);
			} catch (ParseException px) {
				throw new ArticleParseException(articleUrl, px);
			}
			
			//PARAGRAPHS 
			for(Element e:document.select("div.story-body p")){
				if(e.tag().getName().equals("p")){
					paragraphs.add(e.text());
//					System.out.println(e.text());
//					System.out.println();
				}
			}
			
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
		
		return new Article(teamProvider.get(),articleUrl,title,byline,date,paragraphs);
	}

	private Document getDocument(String url) throws IOException, RobotsExclusionException {
		if(!url.contains("print=true")){
			url+="?print=true";
		}
		
		InputStream stream = httpClient.doGet(url);
		Document document = Jsoup.parse(stream, null, url);
		return document;
	}
}
