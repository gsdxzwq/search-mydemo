package com.zhaowq.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsAction;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticSearchUtils {

	private static TransportClient client;

	/**
	 * 返回一个client单例
	 */
	public static synchronized TransportClient getClientInstance() {
		if (client == null) {
			initClient();
		}
		return client;
	}

	/**
	 * 初始化Client
	 */
	private static void initClient() {
		Settings settings = Settings.builder().put("cluster.name", "tclsearch2").put("client.transport.sniff", true).build();
		client = new PreBuiltTransportClient(settings);
		try {
			client = client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.120.198.3"), 9301));
		} catch (UnknownHostException e) {
		}
	}

	/**
	 * 判断索引库是否已存在
	 */
	public static boolean indicesExist(String indiceName) {
		Client client = getClientInstance();
		IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequestBuilder(client.admin().indices(), IndicesExistsAction.INSTANCE,
				indiceName).request();
		IndicesExistsResponse response = client.admin().indices().exists(indicesExistsRequest).actionGet();
		return response.isExists();
	}

	/**
	 * 创建索引库
	 */
	// client.admin().indices().prepareCreate("test").execute().actionGet();
	public static void createIndices(String indiceName) {
		CreateIndexRequest createIndexRequest = new CreateIndexRequestBuilder(client.admin().indices(), CreateIndexAction.INSTANCE, indiceName)
				.request();
		client.admin().indices().create(createIndexRequest).actionGet();
	}

	/**
	 * 删除索引库
	 */
	public static void deleteIndices(String indiceName) {
		Client client = getClientInstance();
		client.admin().indices().prepareDelete(indiceName).execute().actionGet();
	}

}
