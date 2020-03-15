package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostRepository;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.kafka.common.protocol.types.Field;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private DiscussionPostMapper discussionPostMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void testInsert(){
        discussPostRepository.save(discussionPostMapper.selectPostById(241));
        discussPostRepository.save(discussionPostMapper.selectPostById(242));
        discussPostRepository.save(discussionPostMapper.selectPostById(243));
    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussionPostMapper.selectUserPosts(101,0,100));
        discussPostRepository.saveAll(discussionPostMapper.selectUserPosts(102,0,100));
        discussPostRepository.saveAll(discussionPostMapper.selectUserPosts(103,0,100));
    }

    @Test
    public void testUpdateList(){
        DiscussPost discussPost = discussionPostMapper.selectPostById(241);
        discussPost.setContent("4565456465465456");
        discussPostRepository.save(discussPost);
    }

    @Test
    public void testDeleteList(){
        discussPostRepository.deleteAll();
    }

    @Test
    public void  testSearchByRepository(){
        SearchQuery searchQuery =  new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,100))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for(DiscussPost post:page){
            System.out.println(post);
        }
    }


    @Test
    public void  testElasticsearchTemplate(){
        SearchQuery searchQuery =  new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,100))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
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
//        System.out.println(page.getTotalElements());
//        System.out.println(page.getTotalPages());
//        System.out.println(page.getNumber());
//        System.out.println(page.getSize());
//        for(DiscussPost post:page){
//            System.out.println(post);
//        }
    }
}
