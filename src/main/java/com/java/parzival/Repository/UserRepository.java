package com.java.parzival.Repository;

import com.java.parzival.Model.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {

    Optional<Users> findByUsername(String username);

//    Optional<Users> findByEmail(String email);
//
//    List<Users> findByRole(String role);
}
