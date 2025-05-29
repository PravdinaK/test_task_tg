package telegram_webapp_auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String username;

    private String languageCode;
    private String photoUrl;
    private Boolean allowsWriteToPm;

}