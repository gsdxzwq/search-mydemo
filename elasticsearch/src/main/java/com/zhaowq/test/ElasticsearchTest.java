package com.zhaowq.test;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;

/**
 * @ClassName: ElasticsearchTest
 * @Description: TODO
 * @author: zhaowq
 * @date: 2017年5月9日 下午3:58:03
 */
public class ElasticsearchTest {
	// private ElasticsearchTemplate elasticsearchTemplate;
	private static String indiceName = "test";
	private static TransportClient client = ElasticSearchUtils.getClientInstance();

	public static void main(String[] args) throws IOException {

		if (!ElasticSearchUtils.indicesExist(indiceName)) {

			ElasticSearchUtils.createIndices(indiceName);
		}

		// 自定义分词器
		String source = XContentFactory.jsonBuilder().startObject().startObject("settings").startObject("analysis").startObject("tokenizer")
				.startObject("my_edge_ngram_tokenizer").field("type", "edge_ngram").field("min_gram", 1).field("max_gram", 20)
				.startArray("token_chars").value("letter").value("digit").endArray().endObject().endObject().startObject("analyzer")
				.startObject("my_edge_ngram_analyzer").field("tokenizer", "my_edge_ngram_tokenizer").endObject().endObject().endObject().endObject()
				.endObject().string();
		System.out.println(source);

		client.admin().indices().prepareClose(indiceName).get();
		// 创建索引库，同时设置好边缘nGram分词器
		UpdateSettingsRequestBuilder requestBuilder = client.admin().indices().prepareUpdateSettings(indiceName).setSettings(source);
		UpdateSettingsResponse updateSettingsResponse = requestBuilder.get();
		System.out.println(updateSettingsResponse.isAcknowledged());

		client.admin().indices().prepareOpen(indiceName).get();
	}
}
