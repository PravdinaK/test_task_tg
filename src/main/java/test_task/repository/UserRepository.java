package test_task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test_task.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}