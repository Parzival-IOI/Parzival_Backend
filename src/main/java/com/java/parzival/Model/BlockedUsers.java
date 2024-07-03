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

@Document(value = "blockedUsers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BlockedUsers {
    @Id
    private String id;
    private String userId;
    private int attempt;
    @Indexed(name="createdAt", expireAfterSeconds = 3600)
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
