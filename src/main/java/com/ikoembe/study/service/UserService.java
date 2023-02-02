package com.ikoembe.study.service;

import com.ikoembe.study.models.User;
import com.ikoembe.study.payload.request.GuardianInfo;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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
        return findUserByRole("ROLE_GUARDIAN").stream()
                .map(User::getAccountId).collect(Collectors.toList());
    }

    @Deprecated
    public boolean isUserOlderThan(LocalDate dob, int year){
        return dob.isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(year)));
    }


    public BiFunction<LocalDate, Integer, Boolean> isOlderThan = (dob, year) ->
            dob.isBefore(ChronoLocalDate.from(LocalDateTime.now().minusYears(year)));

    public void upsert (String accountId, User user){
        final Query query = new Query();
        query.addCriteria(where(User.FIELD_ACCOUNTID).is(accountId));
        Update update = new Update();
        update.set(User.FIELD_ADDRESS_CITY,user.getAddress().getCity());
        update.set(User.FIELD_ADDRESS_COUNTRY,user.getAddress().getCountry());
        update.set(User.FIELD_ADDRESS_STREET,user.getAddress().getStreet());
        update.set(User.FIELD_ADDRESS_ZIPCODE,user.getAddress().getZipcode());
        update.set(User.FIELD_ADDRESS_NUMBER,user.getAddress().getNumber());
        update.set(User.FIELD_ADDRESS_PHONENUMBER,user.getAddress().getPhoneNumber());
        update.set(User.FIELD_ADDRESS_MOBILENUMBER,user.getAddress().getMobileNumber());
        UpdateResult result = mongoTemplate.upsert(query, update, User.class);
    }
}
