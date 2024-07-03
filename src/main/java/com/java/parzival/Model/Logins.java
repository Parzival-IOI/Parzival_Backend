package com.java.parzival.Model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.Update;

@Document(value = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Logins {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private String refreshToken;
    @CreatedDate
    @Indexed(name="createdAt", expireAfterSeconds = 3600)
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
