package fassto.fms.dynamicorderapi.domain.config.dto;

import fassto.fms.dynamicorderapi.domain.config.entity.CustomerConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerConfigData {

    @NotBlank(message = "센터 정보는 필수 입력입니다.")
    private String whCd;

    @NotBlank(message = "고객사 정보는 필수 입력입니다.")
    private String cstCd;

    @NotEmpty(message = "config 정보는 필수 입력입니다.")
    @Valid
    private List<ConfigDetail> configs;

    private CustomerTags tags;

    public CustomerConfigData(CustomerConfig customerConfig) {
        this.whCd = customerConfig.getWhCd();
        this.cstCd = customerConfig.getCstCd();
        this.configs = customerConfig.getConfigs();
        this.tags = customerConfig.getTags();
    }
}