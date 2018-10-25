package com.zhaowq.suggest;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * reference：https://tech.meituan.com/pinyin_suggest.html
 * <p>
 * 字符串多音字全排列算法
 *
 * @author zhaowq
 * @date 2018/10/25
 */
public class PinyinSuggest {
    public static List<String> getPermutationSentence(List<List<String>> termArrays, int start) {
        if (CollectionUtils.isEmpty(termArrays)) {
            return Collections.emptyList();
        }

        int size = termArrays.size();
        if (start < 0 || start >= size) {
            return Collections.emptyList();
        }
        if (start == size - 1) {
            return termArrays.get(start);
        }

        List<String> strings = termArrays.get(start);

        List<String> permutationSentences = getPermutationSentence(termArrays, start + 1);

        if (CollectionUtils.isEmpty(strings)) {
            return permutationSentences;
        }

        if (CollectionUtils.isEmpty(permutationSentences)) {
            return strings;
        }

        List<String> result = new ArrayList<>();
        for (String pre : strings) {
            for (String suffix : permutationSentences) {
                result.add(pre + suffix);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<List<String>> termArrays = new ArrayList<>();
        ArrayList<String> word = new ArrayList<>();
        word.add("chong");
        word.add("zhong");
        termArrays.add(word);
        ArrayList<String> word2 = new ArrayList<>();
        word2.add("qing");
        termArrays.add(word2);
        System.out.println(getPermutationSentence(termArrays, 0));
    }
}
