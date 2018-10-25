package com.zhaowq.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author zhaowq
 * @date 2018/10/25
 */
public class Indexer {
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

    @Test
    public void index() {
        IndexWriter indexWriter = null;
        try {
            indexWriter = LuceneUtil.getIndexWriter(directory, analyzer);
            Document document = new Document();
            document.add(new StringField("title", "西游记", Field.Store.YES));
            document.add(new StringField("author", "孙悟空", Field.Store.YES));
            document.add(new TextField("desc", "《西游记》是中国古典四大名著之一", Field.Store.YES));
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.close(indexWriter, directory);
        }
    }

    @Test
    public void addIndex() {
        IndexWriter indexWriter = null;
        try {
            indexWriter = LuceneUtil.getIndexWriter(directory, analyzer);
            FieldType fieldType = new FieldType();
            fieldType.setStored(true);
            fieldType.setIndexOptions(IndexOptions.DOCS);
            Random random = new Random();
            List<Document> documents = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Document document = new Document();
                document.add(new Field("id", i + "", fieldType));
                document.add(new StringField("title", "三国演义" + i, Field.Store.YES));
                document.add(new StringField("author", "罗贯中", Field.Store.YES));
                document.add(new TextField("desc", "《三国演义》是中国古典四大名著之一", Field.Store.YES));
                int price = random.nextInt(100) + 1;
                document.add(new NumericDocValuesField("price", price));
                document.add(new StoredField("price", price));
                documents.add(document);
            }
            indexWriter.addDocuments(documents);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.close(indexWriter, directory);
        }
    }

    @Test
    public void delIndex() throws ParseException {
        IndexWriter indexWriter = null;
        try {
            indexWriter = LuceneUtil.getIndexWriter(directory, analyzer);
            indexWriter.deleteDocuments(new Term("title", "三国演义1"));
            // indexWriter.deleteDocuments(new Term("id","9"));
            // QueryParser queryParser=new QueryParser("id", analyzer);
            // Query query=queryParser.parse("2");
            // indexWriter.deleteDocuments(query);
            // 强制合并删除的索引信息
            // indexWriter.forceMergeDeletes();
            // 注意提交或者设置关闭自动提交
            // indexWriter.commit();
            System.out.println("deletions:" + indexWriter.hasDeletions() + "   maxDoc:" + indexWriter.maxDoc()
                    + "  num:" + indexWriter.numDocs());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.close(indexWriter, directory);
        }
    }

    @Test
    public void delAllIndex() throws ParseException {
        IndexWriter indexWriter = null;
        try {
            indexWriter = LuceneUtil.getIndexWriter(directory, analyzer);
            indexWriter.deleteAll();
            System.out.println("deletions:" + indexWriter.hasDeletions() + "   maxDoc:" + indexWriter.maxDoc()
                    + "  num:" + indexWriter.numDocs());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.close(indexWriter, directory);
        }
    }

    @Test
    public void update() throws ParseException {
        IndexWriter indexWriter = null;
        try {
            indexWriter = LuceneUtil.getIndexWriter(directory, analyzer);
            Document doc = new Document();
            FieldType fieldType = new FieldType();
            fieldType.setStored(true);
            fieldType.setIndexOptions(IndexOptions.DOCS);
            doc.add(new Field("id", "9", fieldType));
            doc.add(new StringField("title", "水浒传", Field.Store.YES));
            doc.add(new StringField("author", "施耐庵", Field.Store.YES));
            doc.add(new TextField("desc", "《水浒传》，是中国四大名著之一，是一部描写宋江起义的长篇小说", Field.Store.YES));
            indexWriter.updateDocument(new Term("id", "9"), doc);
            // 强制合并删除的索引信息
            // indexWriter.forceMergeDeletes();
            // 注意提交或者设置关闭自动提交
            // indexWriter.commit();
            System.out.println("deletions:" + indexWriter.hasDeletions() + "   maxDoc:" + indexWriter.maxDoc()
                    + "  num:" + indexWriter.numDocs());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.close(indexWriter, directory);
        }
    }

    // 搜索
    @Test
    public void search() {
        // 打开索引字典
        DirectoryReader ireader = null;
        IndexSearcher isearcher;
        try {
            ireader = LuceneUtil.getDirectoryReader(directory);
            isearcher = LuceneUtil.getIndexSearcher(ireader);
            // Analyzer analyzer1=new IKAnalyzer(true);
            // QueryParser parser = new QueryParser("desc", analyzer);
            // Query query = parser.parse("三国演义");
            TermQuery query = new TermQuery(new Term("title", "三国演义1"));
            ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                System.out.println("id： " + hitDoc.get("id") + "  title:" + hitDoc.get("title") + "   author:"
                        + hitDoc.get("author") + "   price:" + hitDoc.get("price") + "   desc:" + hitDoc.get("desc")
                        + "  得分:" + hit.score);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.close(ireader, directory);
        }
    }
}
