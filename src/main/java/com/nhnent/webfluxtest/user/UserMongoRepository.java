package com.nhnent.webfluxtest.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author haekyu.cho@nhnent.com
 * @since 2018-04-12
 */
public interface UserMongoRepository extends ReactiveCrudRepository<User, Long> {

}
