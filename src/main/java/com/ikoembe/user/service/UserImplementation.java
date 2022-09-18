package com.ikoembe.user.service;

import com.ikoembe.user.models.User;
import com.ikoembe.user.payload.request.GuardianInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
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
        final Query query = new Query();
        query.addCriteria(where(User.FIELD_ROLES).is(role));
        return mongoTemplate.find(query, User.class);
    }

    /**
     * TODO : Since username should be unique, for now it is not needed
     * verify if guardians exits/match or not
     * If we change the approach this method should be improved and used
     * */
    public List<String> getGuardiansAccountId(GuardianInfo guardianInfo) {
        List<User> guardians = findUserByRole("ROLE_GUARDIAN");
                List<String> accountIds =new ArrayList<>();
        guardians.stream().forEach(x-> accountIds.add(x.getAccountId()));
        return accountIds;
    }

    public boolean isUserOlderThan(LocalDate dob, int year){
        return dob.isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(year)));
    }
}
