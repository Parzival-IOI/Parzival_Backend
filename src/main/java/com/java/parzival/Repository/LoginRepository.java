package com.java.parzival.Repository;

import com.java.parzival.Model.Logins;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginRepository extends MongoRepository<Logins, String> {
    Optional<Logins> findByUserId(String userId);
    Optional<Logins> findByRefreshToken(String refreshToken);
}
