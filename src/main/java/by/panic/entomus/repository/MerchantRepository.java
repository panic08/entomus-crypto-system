package by.panic.entomus.repository;

import by.panic.entomus.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByApiKey(String apiKey);
    Merchant findByApiKey(String apiKey);
}
