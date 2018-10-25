package com.zhaowq.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author zhaowq
 * @date 2018/10/25
 */
public class Searcher {
    private Analyzer analyzer;
    private Directory directory;

    @Before
    public void init() throws IOException {
        analyzer = new StandardAnalyzer();
        // 在内存中 创建索引
        // directory = new RAMDirectory();
        // 在磁盘中创建索引
        directory = LuceneUtil.getDirectory("D:\\index");
    }

    // 搜索
    @Test
    public void search() throws IOException, ParseException {
        // 打开索引库
        DirectoryReader ireader = LuceneUtil.getDirectoryReader(directory);
        IndexSearcher isearcher = LuceneUtil.getIndexSearcher(ireader);
        QueryParser parser = new QueryParser("desc", analyzer);
        Query query = parser.parse("三国演义");
        ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
        for (ScoreDoc hit : hits) {
            Document hitDoc = isearcher.doc(hit.doc);
            System.out.println("id： " + hitDoc.get("id") + "  title:" + hitDoc.get("title") + "   author:"
                    + hitDoc.get("author") + "   price:" + hitDoc.get("price") + "   desc:" + hitDoc.get("desc")
                    + "  得分:" + hit.score);
        }
        LuceneUtil.close(ireader, directory);
    }

    // 搜索排序
    @Test
    public void searchBySort() throws IOException, ParseException {
        // 打开索引库
        DirectoryReader ireader = LuceneUtil.getDirectoryReader(directory);
        IndexSearcher isearcher = LuceneUtil.getIndexSearcher(ireader);
        QueryParser parser = new QueryParser("desc", analyzer);
        Query query = parser.parse("三国演义");
        ScoreDoc[] hits = isearcher.search(query, 10, new Sort(new SortField("price", SortField.Type.INT,
                true))).scoreDocs;
        for (ScoreDoc hit : hits) {
            Document hitDoc = isearcher.doc(hit.doc);
            System.out.println("id： " + hitDoc.get("id") + "  title:" + hitDoc.get("title") + "   author:"
                    + hitDoc.get("author") + "   price:" + hitDoc.get("price") + "   desc:" + hitDoc.get("desc")
                    + "  得分:" + hit.score);
        }
        LuceneUtil.close(ireader, directory);
    }

    @Test
    public void testSearchByPage() {
        try {
            searchByPage("title", "三国演义", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 搜索分页
    private void searchByPage(String fieldName, String value, int pageNum) throws Exception {
        // 打开索引库
        DirectoryReader reader = LuceneUtil.getDirectoryReader(directory);
        IndexSearcher isearcher = LuceneUtil.getIndexSearcher(reader);
        WildcardQuery query = new WildcardQuery(new Term(fieldName, "*" + value + "*"));
        Sort sort = new Sort(new SortField("price", SortField.Type.INT, true));
        // 计算需要几条
        int PAGE_SIZE = 10;
        int start = (pageNum - 1) * PAGE_SIZE;
        ScoreDoc[] hits;
        if (start == 0) {
            hits = isearcher.search(query, PAGE_SIZE, new Sort(new SortField("price", SortField.Type.INT,
                    true))).scoreDocs;
        } else {
            ScoreDoc[] hitsPres = isearcher.search(query, start, sort).scoreDocs;
            ScoreDoc preHit = hitsPres[start - 1];
            hits = isearcher.searchAfter(preHit, query, PAGE_SIZE, sort).scoreDocs;
        }
        for (ScoreDoc hit : hits) {
            Document hitDoc = isearcher.doc(hit.doc);
            System.out.println("id： " + hitDoc.get("id") + "  title:" + hitDoc.get("title") + "   author:"
                    + hitDoc.get("author") + "   price:" + hitDoc.get("price") + "   desc:" + hitDoc.get("desc"));
        }
        LuceneUtil.close(reader, directory);
    }
}

