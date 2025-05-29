package telegram_webapp_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import telegram_webapp_auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}