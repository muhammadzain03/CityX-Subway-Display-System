package ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.view.NewsPanel;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsController extends DataFetcherController {

    private final NewsPanel newsPanel;
    private static final String API_KEY = "cf8af3cfb21547feaaa7a89fe96541f4";
    private static final String NEWS_REGEX = "\"title\":\"(.*?)\"";
    private static final Pattern NEWS_PATTERN = Pattern.compile(NEWS_REGEX);

    public NewsController(NewsPanel newsPanel) {
        this.newsPanel = newsPanel;
    }

    /**
     * Retrieves news headlines for a given country code and updates the NewsPanel.
     * 
     * @param countryCode the country code to retrieve news for
     */
    public void retrieveNews(String countryCode) {
        new Thread(() -> {
            try {
                String url = "https://newsapi.org/v2/top-headlines?country=" + countryCode + "&apiKey=" + API_KEY;
                String newsData = fetchData(url);

                Matcher matcher = NEWS_PATTERN.matcher(newsData);

                StringBuilder allNews = new StringBuilder();
                while (matcher.find()) {
                    allNews.append(matcher.group(1).trim()).append(". ");
                }

                String news = allNews.toString();
                updateNews(news);

            } catch (IOException e) {
                e.printStackTrace();
                updateNews("Failed to fetch news");
            }
        }).start();
    }

    /**
     * Updates the news panel with the provided news content.
     *
     * @param news the news content to display
     */
    private void updateNews(String news) {
        newsPanel.updateNewsLabel(news);
    }
}
