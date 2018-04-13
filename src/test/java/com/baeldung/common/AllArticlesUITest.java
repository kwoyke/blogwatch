package com.baeldung.common;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baeldung.base.BaseUITest;
import com.baeldung.config.GlobalConstants;
import com.baeldung.util.Utils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class AllArticlesUITest extends BaseUITest {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ListIterator<String> allArticlesList;
    Multimap<String, String> badURLs = ArrayListMultimap.create();

    boolean loadNextUrl = true;
    boolean allTestsFlag = false;

    @BeforeEach
    public void loadNewWindow() throws IOException {
        logger.info("inside loadNewWindow()");
        allTestsFlag = false;
        page.openNewWindow();
        allArticlesList = Utils.fetchAllArtilcesAsListIterator();
        badURLs.clear();
        loadNextURL();
    }

    @AfterEach
    public void closeWindow() {
        page.quiet();
    }

    @Test
    @Tag("givenAllTheArticles_whenArticleLods_thenArticleHasNoEmptyDiv")
    public final void givenAllTheArticles_whenArticleLods_thenArticleHasNoEmptyDiv() throws IOException {
        do {
            if (page.findEmptyDivs().size() > 0) {
                badURLs.put("givenAllTheArticles_whenArticleLods_thenArticleHasNoEmptyDiv", page.getUrl());
            }
        } while (loadNextURL());

        if (!allTestsFlag && badURLs.size() > 0) {
            fail("Failed test-->" + badURLs.toString());
        }
    }

    @Test
    @Tag("givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheTop")
    public final void givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheTop() throws IOException {
        do {
            if (!Utils.excludePage(page.getUrl(), GlobalConstants.ARTILCE_JAVA_WEEKLY) && page.findShortCodesAtTheTopOfThePage().size() != 1) {
                badURLs.put("givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheTop", page.getUrl());
            }
        } while (loadNextURL());

        if (!allTestsFlag && badURLs.size() > 0) {
            fail("Failed test-->" + badURLs.toString());
        }
    }

    @Test
    @Tag("givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheEnd")
    public final void givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheEnd() throws IOException {
        do {
            if (!Utils.excludePage(page.getUrl(), GlobalConstants.ARTILCE_JAVA_WEEKLY) && page.findShortCodesAtTheEndOfThePage().size() != 1) {
                badURLs.put("givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheEnd", page.getUrl());
            }
        } while (loadNextURL());

        if (!allTestsFlag && badURLs.size() > 0) {
            fail("Failed test-->" + badURLs.toString());
        }
    }

    @Test
    @Tag("givenAllTheArticles_whenArticleLods_thenImagesPointToCorrectEnv")
    public final void givenAllTheArticles_whenArticleLods_thenImagesPointToCorrectEnv() throws IOException {
        do {
            List<WebElement> imgTags = page.findImagesPointingToInvalidEnvOnTheArticle();
            if (imgTags.size() > 0) {
                badURLs.put("givenAllTheArticles_whenArticleLods_thenImagesPointToCorrectEnv", page.getUrl() + " ( " + imgTags.stream().map(webElement -> webElement.getAttribute("src") + " , ").collect(Collectors.joining()) + ")\n");
            }
        } while (loadNextURL());

        if (!allTestsFlag && badURLs.size() > 0) {
            fail("Failed test-->" + badURLs.toString());
        }
    }

    @Test
    @Tag("givenAllArticles_whenArticleLoads_thenTheMetaDescriptionExists")
    public final void givenAllArticles_whenArticleLoads_thenTheMetaDescriptionExists() throws IOException {
        do {
            if (!Utils.excludePage(page.getUrl(), GlobalConstants.ARTILCE_JAVA_WEEKLY) && !page.findMetaDescriptionTag()) {
                badURLs.put("givenAllArticles_whenArticleLoads_thenTheMetaDescriptionExists", page.getUrl());
            }
        } while (loadNextURL());

        if (!allTestsFlag && badURLs.size() > 0) {
            fail("Failed test-->" + badURLs.toString());
        }
    }

    @Test
    @Tag("givenTestsTargetedToAllArticlesUrls_whenTheTestRuns_thenItPasses")
    @Tag("givenTestsTargetedToAllUrls_whenTheTestRuns_thenItPasses")
    public final void givenTestsTargetedToAllArticlesUrls_whenTheTestRuns_thenItPasses() throws IOException {
        allTestsFlag = true;
        do {
            loadNextUrl = false;
            givenAllTheArticles_whenArticleLods_thenArticleHasNoEmptyDiv();
            givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheTop();
            givenAllArticleList_whenArticleLoads_thenItHasSingleShortcodeAtTheEnd();
            givenAllTheArticles_whenArticleLods_thenImagesPointToCorrectEnv();
            givenAllArticles_whenArticleLoads_thenTheMetaDescriptionExists();
            loadNextUrl = true;
        } while (loadNextURL());

        if (badURLs.size() > 0) {
            String testsResult = "";
            for (Map.Entry<String, Collection<String>> entry : badURLs.asMap().entrySet()) {
                testsResult = testsResult + entry.getKey() + "=" + entry.getValue().toString() + "\n";
            }
            fail("Failed tests-->" + testsResult);
        }
    }

    private boolean loadNextURL() {
        if (!allArticlesList.hasNext() || !loadNextUrl) {
            return false;
        }

        page.setUrl(page.getBaseURL() + allArticlesList.next());
        logger.info(page.getUrl());

        page.loadUrlWithThrottling();

        return true;

    }

}
