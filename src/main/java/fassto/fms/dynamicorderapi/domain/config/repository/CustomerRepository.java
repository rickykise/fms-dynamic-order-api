package fassto.fms.dynamicorderapi.domain.config.repository;

import fassto.fms.dynamicorderapi.domain.config.entity.CustomerConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerConfig, String> {
    List<CustomerConfig> findByWhCdOrderByCstCd(String whCd);
    Optional<CustomerConfig> findByWhCdAndCstCd(String whCd, String cstCd);
    CustomerConfig save(CustomerConfig customerConfig);
}
