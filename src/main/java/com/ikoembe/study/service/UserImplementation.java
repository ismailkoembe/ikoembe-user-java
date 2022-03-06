package com.ikoembe.study.service;

import com.ikoembe.study.models.ERole;
import com.ikoembe.study.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserImplementation {
    private static final Logger log = LoggerFactory.getLogger(UserImplementation.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> findUserByAge(int age, String lastname){
        /*TODO: int can not be parsed, method doesn't work*/
        Query query = new Query();
        query.addCriteria(where(User.FIELD_DOB).lt(LocalDateTime.now().minusYears(age)));
        Criteria criteria = Criteria.where(User.FIELD_LASTNAME).is(lastname);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, User.class);

    }
    public List<User> findUserByRole(String role){
        Query query = new Query();
        query.addCriteria(where(User.FIELD_ROLES).is(role));
        return mongoTemplate.find(query, User.class);

    }

}
