package com.erp.support.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.support.entity.KBArticle;
import com.erp.support.repository.KBArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class KBArticleService {

    private final KBArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<KBArticle> search(String q, Pageable pageable) {
        return articleRepository.search(blank(q), pageable);
    }

    @Transactional(readOnly = true)
    public KBArticle get(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("KBArticle", id));
    }

    @Transactional
    public KBArticle save(KBArticle article) {
        return articleRepository.save(article);
    }

    @Transactional
    public void vote(UUID id, boolean helpful) {
        KBArticle article = get(id);
        if (helpful) {
            article.setHelpfulYes(article.getHelpfulYes() + 1);
        } else {
            article.setHelpfulNo(article.getHelpfulNo() + 1);
        }
        articleRepository.save(article);
    }

    @Transactional
    public void delete(UUID id) {
        articleRepository.deleteById(id);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
