package com.nowcoder.community.service;


import com.nowcoder.community.dao.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ElasticService {

    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 保存ElasticSearch服务器中的帖子
     * @param discussPost
     */
    public void saveDiscussPost(DiscussPost discussPost){
        discussPostRepository.save(discussPost);
    }
    /**
     * 删除ElasticSearch服务器中的帖子
     * @param discussPost
     */
    public void deleteDiscussPos(DiscussPost discussPost){
        discussPostRepository.delete(discussPost);
    }

    /**
     *
     * @param keyword 关键词
     * @param current 第几页
     * @param limit 条数
     * @return
     */
    public Page<DiscussPost> searchService(String keyword,int current, int limit){
        SearchQuery searchQuery =  new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        return  elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() < 0) {
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost discussPost = new DiscussPost();
                    String id = hit.getSourceAsMap().get("id").toString();
                    discussPost.setId(Integer.valueOf(id));
                    String userId = hit.getSourceAsMap().get("userId").toString();
                    discussPost.setUserId(Integer.valueOf(userId));

                    String type = hit.getSourceAsMap().get("type").toString();
                    discussPost.setType(Integer.valueOf(type));

                    String status = hit.getSourceAsMap().get("status").toString();
                    discussPost.setType(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    discussPost.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    discussPost.setCommentCount(Integer.valueOf(commentCount));

                    String score = hit.getSourceAsMap().get("score").toString();
                    discussPost.setScore(Double.valueOf(commentCount));

                    String content = hit.getSourceAsMap().get("content").toString();
                    discussPost.setContent(String.valueOf(content));
                    String title = hit.getSourceAsMap().get("title").toString();
                    discussPost.setContent(String.valueOf(title));

                    /**
                     * 这段代码
                     */
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        discussPost.setTitle(titleField.getFragments()[0].toString());
                    }
                    /**
                     * 这段代码
                     */
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        discussPost.setTitle(contentField.getFragments()[0].toString());
                    }
                    list.add(discussPost);
                }
                return new AggregatedPageImpl(list, pageable, hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }
        });

    }

}
