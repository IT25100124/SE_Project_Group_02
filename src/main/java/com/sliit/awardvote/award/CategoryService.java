package com.sliit.awardvote.award;

import com.sliit.awardvote.common.AbstractCrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends AbstractCrudService<Category, Long> {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    protected JpaRepository<Category, Long> getRepository() {
        return categoryRepository;
    }

    public List<Category> findByProgramme(Long programmeId) {
        return categoryRepository.findByAwardProgrammeId(programmeId);
    }
}
