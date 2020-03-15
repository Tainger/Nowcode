package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 重点看。。。。。。。。。。
 */
@Component
public class SensitiveFilter {

    private static  final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static  final String REPLACEMENT = "***";
    private  TrieNode root = new TrieNode();

    //构造器调用之后
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))
         ){
            String word ;
            while((word=bufferedReader.readLine())!=null){
                this.addKey(word);
            }
        }catch (Exception e){
            logger.error("加载敏感词失败"+e.getMessage());
        }
    }

    /**
     * 插入数据
     * @param word
     */
    private void addKey(String word) {
        TrieNode temp = root;
        Character c;
        for(int i =0;i<word.length();i++){
            c= word.charAt(i);
            TrieNode cur = temp.get(c);
            if(cur==null){
                cur = new TrieNode();
                temp.put(c,cur);
            }
            temp=cur;
            if(i == word.length()-1)
                cur.setEnd(true);
        }
    }

    /**
     *
     * @param text
     * @return
     */
    public String filter(String text){
        TrieNode cur =root;
        int begin =0;
        int end =0;
        StringBuilder res = new StringBuilder();
        while(end<text.length()){
            Character c = text.charAt(end);
            if(isSymbol(c)){
                if(cur==root) {
                    res.append(text.charAt(begin));
                    end=begin++;
                }
                end++;
            }
            cur = cur.get(c);
            if(cur==null){
                res.append(c);
                end++;
                cur=root;
            }else if(cur.getisEnd()){
                res.append(REPLACEMENT);
                begin=end++;
            }else {
                end++;
            }
        }
        return res.append(text.substring(begin)).toString();
    }

    /**
     * 非东亚文字
     * @param c
     * @return
     */
    private  boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80 || c>0x9FFF);
    }
    private class TrieNode{
        private boolean isEnd = false;
        private Map<Character,TrieNode> map = new HashMap();

        public boolean getisEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public void put(Character c,TrieNode node){
            map.put(c,node);
        }

        public TrieNode get(Character c){
            return map.get(c);
        }
    }


}
