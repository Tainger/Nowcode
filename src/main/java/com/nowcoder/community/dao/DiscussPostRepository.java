package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.Element;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {


}
