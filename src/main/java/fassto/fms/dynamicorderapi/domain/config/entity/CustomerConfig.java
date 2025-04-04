package fassto.fms.dynamicorderapi.domain.config.entity;

import fassto.fms.dynamicorderapi.domain.config.dto.ConfigDetail;
import fassto.fms.dynamicorderapi.domain.config.dto.CustomerConfigData;
import fassto.fms.dynamicorderapi.domain.config.dto.CustomerTags;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor
@Document(collection = "dynamicOrder")
@CompoundIndex(name = "whCd_cstCd_idx", def = "{'whCd': 1, 'cstCd': 1}", unique = true)
public class CustomerConfig {

    @Id
    private String id;

    private String whCd;
    private String cstCd;
    private List<ConfigDetail> configs;
    private CustomerTags tags;

    public CustomerConfig(CustomerConfigData customerConfigData) {
        this.whCd = customerConfigData.getWhCd();
        this.cstCd = customerConfigData.getCstCd();
        this.configs = customerConfigData.getConfigs();
        this.tags = customerConfigData.getTags();
    }

    public CustomerConfig(String id, CustomerConfig customerConfig) {
        this.id = id;
        this.whCd = customerConfig.getWhCd();
        this.cstCd = customerConfig.getCstCd();
        this.configs = customerConfig.getConfigs();
        this.tags = customerConfig.getTags();
    }
}
