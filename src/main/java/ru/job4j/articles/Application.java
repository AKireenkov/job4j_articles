package ru.job4j.articles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.articles.service.DelFilesAndDirectory;
import ru.job4j.articles.service.SimpleArticleService;
import ru.job4j.articles.service.generator.RandomArticleGenerator;
import ru.job4j.articles.store.ArticleStore;
import ru.job4j.articles.store.WordStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getSimpleName());

    public static final int TARGET_COUNT = 1_000_000;

    public static void main(String[] args) throws IOException {
        delDirectory();
        var properties = loadProperties();
        var wordStore = new WordStore(properties);
        var articleStore = new ArticleStore(properties);
        var articleGenerator = new RandomArticleGenerator();
        var articleService = new SimpleArticleService(articleGenerator);
        wordStore.init();
        articleStore.init();
        articleService.generate(wordStore, TARGET_COUNT, articleStore);
    }

    private static void delDirectory() throws IOException {
        Path path = Paths.get("./db-dir");
        if (Files.exists(path)) {
            DelFilesAndDirectory del = new DelFilesAndDirectory();
            Files.walkFileTree(path, del);
        }
    }

    private static Properties loadProperties() {
        LOGGER.info("Загрузка настроек приложения");
        var properties = new Properties();
        try (InputStream in = Application.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(in);
        } catch (Exception e) {
            LOGGER.error("Не удалось загрузить настройки. { }", e.getCause());
            throw new IllegalStateException();
        }
        return properties;
    }

}
