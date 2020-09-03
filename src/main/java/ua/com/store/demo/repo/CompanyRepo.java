package ua.com.store.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.store.demo.model.CompanyEntity;

public interface CompanyRepo extends JpaRepository<CompanyEntity, Long> {
}
