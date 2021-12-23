package account.dao;

import account.entity.Group;
import account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{
    Optional<Group> findById(Long userId);
    Group findByCode(String code);
}