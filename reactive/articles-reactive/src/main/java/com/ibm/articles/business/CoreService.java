package com.ibm.articles.business;

import com.ibm.articles.data.DataAccessManager;
import com.ibm.articles.data.NoConnectivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.concurrent.CompletableFuture;
import com.ibm.articles.business.Article;
import com.ibm.articles.business.InvalidArticle;
import com.ibm.articles.business.NoDataAccess;
import io.vertx.core.Vertx;
import io.vertx.axle.core.eventbus.EventBus;

@ApplicationScoped
public class CoreService {

	private static final String CREATE_SAMPLES = "CREATE";

	@Inject
	@ConfigProperty(name = "samplescreation", defaultValue = "CREATE")
	private String samplescreation;

	@Inject
	Vertx vertx;

	@Inject
	private DataAccessManager dataAccessManager;

	@PostConstruct
	private void addArticles() {
		if (samplescreation.equalsIgnoreCase(CREATE_SAMPLES)) addSampleArticles();
	}

	private Article createArticle(String title, String url, String author) {
		long id = new java.util.Date().getTime();
		String idAsString = String.valueOf(id);

		if (url == null)
			url = "Unknown";
		if (author == null)
			author = "Unknown";

		Article article = new Article();
		article.title = title;
		article.creationDate = idAsString;
		article.id = idAsString;
		article.url = url;
		article.author = author;

		return article;
	}

	public Article addArticle(String title, String url, String author) throws NoDataAccess, InvalidArticle {
		if (title == null)
			throw new InvalidArticle();

		Article article = createArticle(title, url, author);

		try {
			dataAccessManager.getDataAccess().addArticle(article);

			sendMessageToKafka(article);

			return article;
		} catch (NoConnectivity e) {
			e.printStackTrace();
			throw new NoDataAccess(e);
		}
	}

	public CompletableFuture<Article> addArticleReactive(String title, String url, String author) {
		CompletableFuture<Article> future = new CompletableFuture<>();

		if (title == null) {
			future.completeExceptionally(new InvalidArticle());
		}
		else {
			Article article = createArticle(title, url, author);
			dataAccessManager.getDataAccess().addArticleReactive(article).thenAccept(newArticle -> {
				sendMessageToKafka(newArticle);
				future.complete(newArticle);
			});
		}
		return future;
	}	

	@Inject
  	EventBus bus; 

	private void sendMessageToKafka(Article article) {
		bus.publish("com.ibm.articles.apis.NewArticleCreated", article.id);
	}

	public Article getArticle(String id) throws NoDataAccess, ArticleDoesNotExist {
		Article article;
		try {
			article = dataAccessManager.getDataAccess().getArticle(id);
			return article;
		} catch (NoConnectivity e) {
			e.printStackTrace();
			throw new NoDataAccess(e);
		}
	}

	public CompletableFuture<Article> getArticleReactive(String id) {
		return dataAccessManager.getDataAccess().getArticleReactive(id);
	}

	public List<Article> getArticles(int requestedAmount) throws NoDataAccess, InvalidInputParamters {
		if (requestedAmount < 0)
			throw new InvalidInputParamters();
		List<Article> articles;
		try {
			articles = dataAccessManager.getDataAccess().getArticles();

			articles = this.sortArticles(articles);
			articles = this.applyAmountFilter(articles, requestedAmount);

			return articles;
		} catch (NoConnectivity e) {
			e.printStackTrace();
			throw new NoDataAccess(e);
		}
	}

	public CompletableFuture<List<Article>> getArticlesReactive(int requestedAmount) {
		CompletableFuture<List<Article>> future = new CompletableFuture<>();
		
		if (requestedAmount < 0) {
			future.completeExceptionally(new InvalidInputParamters());
		}
		else {
			dataAccessManager.getDataAccess().getArticlesReactive()
			.thenApply(articles -> {
				articles = this.sortArticles(articles);
				articles = this.applyAmountFilter(articles, requestedAmount);

				return articles;
			}).whenComplete((articles, throwable) -> {
				future.complete(articles);          
			});
		}

        return future;
	}

	private List<Article> sortArticles(List<Article> articles) {
		Comparator<Article> comparator = new Comparator<Article>() {
			@Override
			public int compare(Article left, Article right) {
				try {
					int leftDate = Integer.valueOf(left.creationDate.substring(6));
					int rightDate = Integer.valueOf(right.creationDate.substring(6));
					return rightDate - leftDate;
				} catch (NumberFormatException e) {
					return 0;
				}
			}
		};
		Collections.sort(articles, comparator);
		return articles;
	}

	private List<Article> applyAmountFilter(List<Article> articles, int requestedAmount) {
		int amount = articles.size();
		if (amount > requestedAmount) {
			amount = requestedAmount;
			List<Article> output = new ArrayList<Article>(amount);
			for (int index = 0; index < amount; index++) {
				output.add(articles.get(index));
			}
			articles = output;
		}
		return articles;
	}

	private void addSampleArticles() {
		System.out.println("com.ibm.articles.business.Service.addSampleArticles");
		try {
			this.addArticleReactive("Blue Cloud Mirror — (Don’t) Open The Doors!",
					"https://haralduebele.blog/2019/02/17/blue-cloud-mirror-dont-open-the-doors/", "Harald Uebele");
			Thread.sleep(5);
			this.addArticleReactive("Recent Java Updates from IBM", "http://heidloff.net/article/recent-java-updates-from-ibm",
					"Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("Developing and debugging Microservices with Java",
					"http://heidloff.net/article/debugging-microservices-java-kubernetes", "Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("IBM announced Managed Istio and Managed Knative",
					"http://heidloff.net/article/managed-istio-managed-knative", "Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("Three Minutes Demo of Blue Cloud Mirror",
					"http://heidloff.net/article/blue-cloud-mirror-demo-video", "Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("Blue Cloud Mirror Architecture Diagrams",
					"http://heidloff.net/article/blue-cloud-mirror-architecture-diagrams", "Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("Three awesome TensorFlow.js Models for Visual Recognition",
					"http://heidloff.net/article/tensorflowjs-visual-recognition", "Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("Install Istio and Kiali on IBM Cloud or Minikube",
					"https://haralduebele.blog/2019/02/22/install-istio-and-kiali-on-ibm-cloud-or-minikube/", "Harald Uebele");
			Thread.sleep(5);
			this.addArticleReactive("Dockerizing Java MicroProfile Applications",
					"http://heidloff.net/article/dockerizing-container-java-microprofile", "Niklas Heidloff");
			Thread.sleep(5);
			this.addArticleReactive("Debugging Microservices running in Kubernetes",
					"http://heidloff.net/article/debugging-microservices-kubernetes", "Niklas Heidloff");			
		} catch (InterruptedException e) {			
		}
	}
}
