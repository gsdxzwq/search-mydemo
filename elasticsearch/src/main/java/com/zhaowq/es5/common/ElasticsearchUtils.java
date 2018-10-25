package com.zhaowq.es5.common;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * @ClassName: ElasticsearchUtils
 * @Description: 工具类
 * @author: zhaowq
 * @date: 2017年4月6日 下午4:14:40
 */
public class ElasticsearchUtils {

	private static TransportClient client;

	public static synchronized TransportClient getClientInstance() {
		if (client == null) {
			initClient();
		}
		return client;
	}

	private static Client initClient() {
		Settings settings = Settings.builder().put("cluster.name", "myClusterName").put("client.transport.sniff", true).build();
		TransportClient client = new PreBuiltTransportClient(settings);
		return client;
	}

	public void index() {
		Client client = ElasticsearchUtils.getClientInstance();
		String json = "{" +
				"\"user\":\"kimchy\"," +
				"\"postDate\":\"2013-01-30\"," +
				"\"message\":\"trying out Elasticsearch\"" +
				"}";
		IndexResponse response = client.prepareIndex("twitter", "tweet", "1").setSource(json).get();

	}

	public void scroll() {
		QueryBuilder qb = QueryBuilders.termQuery("multi", "test");

		SearchResponse scrollResp = client.prepareSearch("IndiceName")
				.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				.setScroll(new TimeValue(60000))
				.setQuery(qb)
				.setSize(100).get(); // max of 100 hits will be returned for
										// each scroll
		// Scroll until no hits are returned
		do {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				// Handle the hit...
			}

			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		} while (scrollResp.getHits().getHits().length != 0); // Zero hits mark
																// the end of
																// the scroll
																// and the while
																// loop.
	}

	public void multiSearch() {
		SearchRequestBuilder srb1 = client
				.prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
		SearchRequestBuilder srb2 = client
				.prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kimchy")).setSize(1);

		MultiSearchResponse sr = client.prepareMultiSearch()
				.add(srb1)
				.add(srb2)
				.get();

		// You will get all individual responses from
		// MultiSearchResponse#getResponses()
		long nbHits = 0;
		for (MultiSearchResponse.Item item : sr.getResponses()) {
			SearchResponse response = item.getResponse();
			nbHits += response.getHits().getTotalHits();
		}

		// QueryBuilders.boolQuery().
	}
}
