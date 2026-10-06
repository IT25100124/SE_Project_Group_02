package com.sliit.awardvote.award.dao;
//The `AwardProgramme` model/entity is imported into this DAO to handle database operations.
import com.sliit.awardvote.award.model.AwardProgramme;
//You import the common `AbstractJpaDao` class to inherit it for database operations.
import com.sliit.awardvote.common.dao.AbstractJpaDao;

//The `@Repository` annotation is used to inform Spring that this class belongs to the Repository/DAO layer.
import org.springframework.stereotype.Repository;
//Database transaction behavior control
import org.springframework.transaction.annotation.Transactional;

@Repository //making AwardDaoImpl a Spring Repository bean.
@Transactional(readOnly = true) //Read data

//Inherit save / find / update / delete (AbstractJpaDao)
//This class implements the contract of the AwardDao interface.
public class AwardDaoImpl extends AbstractJpaDao<AwardProgramme> implements AwardDao {

    public AwardDaoImpl() { //class constructor
        super(AwardProgramme.class);
    }
}
