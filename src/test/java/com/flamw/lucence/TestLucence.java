package com.flamw.lucence;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestLucence {
//    创建索引
    @Test
    public void create() throws IOException {
        //步骤一：创建Directory对象，用于指定索引库的位置    RAMDirectory内存
        Directory directory = FSDirectory.open(new File("D:\\lucence\\index").toPath());
        //步骤二：创建一个IndexWriter对象，用于写索引
        IndexWriter indexWriter=new IndexWriter(directory,new IndexWriterConfig());
        //步骤三：读取磁盘中文件，对应每一个文件创建一个文档对象
        File file=new File("D:\\lucence\\resource\\");
        //步骤四：获取文件列表
        File[] files = file.listFiles();
        for (File item:files) {
            //步骤五：获取文件数据，封装域   参数三：是否存储
            Field fieldName=new TextField("fieldName",item.getName(), Field.Store.YES);
            Field fieldPath=new TextField("fieldPath",item.getPath(), Field.Store.YES);
            Field fieldSize=new TextField("fieldSize", FileUtils.sizeOf(item)+"", Field.Store.YES);
            Field fieldContent=new TextField("fieldContent", FileUtils.readFileToString(item,"UTF-8"), Field.Store.YES);
            //步骤六：创建文档对象，向文档对象中添加域
            Document document=new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldSize);
            document.add(fieldContent);

            //步骤七：创建索引，将文档对象写入到索引库
            indexWriter.addDocument(document);
        }
        //步骤八：关闭资源
        indexWriter.close();
    }


    @Test
    public void query() throws IOException {
        //1.创建Directory对象，指定索引库位置
        Directory directory = FSDirectory.open(new File("D:\\lucence\\index").toPath());
        //2.创建IndexReader对象，读取索引库内容
        IndexReader indexReader= DirectoryReader.open(directory);
        //3.创建IndexSearcher对象
        IndexSearcher indexSearcher=new IndexSearcher(indexReader);
        //4.创建Query查询对象
        Query query=new TermQuery(new Term("fieldContent","spring"));
        //5.执行查询，获取到文档对象
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("共获取："+topDocs.totalHits+"个文档~~~~~~~~~~~~~~~~~~~~~");
        //6.获取文档列表
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;
        for (ScoreDoc item:scoreDocs) {
            //获取文档ID
            int docId=item.doc;
            //取出文档
            Document doc = indexSearcher.doc(docId);
            //获取到文档域中数据
            System.out.println("fieldName:"+doc.get("fieldName"));
            System.out.println("fieldPath:"+doc.get("fieldPath"));
            System.out.println("fieldSize:"+doc.get("fieldSize"));
            System.out.println("fieldContent:"+doc.get("fieldContent"));
            System.out.println("==============================================================");
        }
        //7.关闭资源
        indexReader.close();
    }
}
