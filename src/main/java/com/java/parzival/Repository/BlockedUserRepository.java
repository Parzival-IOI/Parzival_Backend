package com.java.parzival.Repository;

import com.java.parzival.Model.BlockedUsers;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedUserRepository extends MongoRepository<BlockedUsers, String> {
    Optional<BlockedUsers> findByUserId(String userId);
}
