package com.zhaowq.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author zhaowq
 * @date 2018/10/25
 */
public class LuceneUtil {

    public static Directory getDirectory(String path) {
        Directory directory = null;
        try {
            directory = FSDirectory.open(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directory;
    }

    public static Directory getRAMDirectory() {
        return new RAMDirectory();
    }

    public static DirectoryReader getDirectoryReader(Directory directory) {
        DirectoryReader reader = null;
        try {
            reader = DirectoryReader.open(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    public static IndexSearcher getIndexSearcher(DirectoryReader reader) {
        return new IndexSearcher(reader);
    }

    public static IndexWriter getIndexWriter(Directory directory, Analyzer analyzer) {
        IndexWriter iwriter = null;
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            // Sort sort=new Sort(new SortField("content", Type.STRING));
            // config.setIndexSort(sort);//排序
            config.setCommitOnClose(true);
            //自动提交
            // config.setMergeScheduler(new ConcurrentMergeScheduler());
            // config.setIndexDeletionPolicy(new
            // SnapshotDeletionPolicy(NoDeletionPolicy.INSTANCE));
            iwriter = new IndexWriter(directory, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iwriter;
    }

    public static void close(IndexWriter indexWriter, Directory directory) {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                indexWriter = null;
            }
        }
        if (directory != null) {
            try {
                directory.close();
            } catch (IOException e) {
                directory = null;
            }
        }
    }

    public static void close(DirectoryReader reader, Directory directory) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                reader = null;
            }
        }
        if (directory != null) {
            try {
                directory.close();
            } catch (IOException e) {
                directory = null;
            }
        }
    }
}
