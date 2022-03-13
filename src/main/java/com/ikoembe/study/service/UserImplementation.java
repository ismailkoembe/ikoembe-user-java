package com.ikoembe.study.service;

import com.ikoembe.study.models.User;
import com.ikoembe.study.payload.request.GuardianInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserImplementation {
    private static final Logger log = LoggerFactory.getLogger(UserImplementation.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> findUserByAgeAndRole(int age, String role) {
        Query query = new Query();
        query.addCriteria(where(User.FIELD_DOB).lt(LocalDateTime.now().minusYears(age)));
        Criteria criteria = Criteria.where(User.FIELD_ROLES).is(role);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, User.class);

    }

    public List<User> findUserByRole(String role) {
        Query query = new Query();
        query.addCriteria(where(User.FIELD_ROLES).is(role));
        return mongoTemplate.find(query, User.class);
    }

    /**
     * TODO : Since username should be unique, for now it is not needed
     * verify if guardians exits/match or not
     * If we change the approach this method should be improved and used
     * */
    public String getGuardianAccountId(GuardianInfo guardianInfo) {
        Query query = new Query();
        Criteria criteria = where(User.FIELD_USERNAME).is(guardianInfo.getUsername());
        query.addCriteria(criteria);
        List<User> guardians = mongoTemplate.find(query, User.class);
//        guardians.forEach(x-> guardianAccountIds.add(x.getAccountId()));
        return guardians.get(0).getAccountId();
    }

}
