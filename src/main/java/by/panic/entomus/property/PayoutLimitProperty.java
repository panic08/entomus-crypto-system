package by.panic.entomus.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("payouts.limit")
@Getter
@Setter
public class PayoutLimitProperty {
    private String minEth;
    private String maxEth;

    private String minBtc;
    private String maxBtc;

    private String minTrx;
    private String maxTrx;

    private String minBnb;
    private String maxBnb;

    private String minMatic;
    private String maxMatic;

    private String minEtc;
    private String maxEtc;

    private String minAvax;
    private String maxAvax;

    private String minBch;
    private String maxBch;

    private String minSol;
    private String maxSol;

    private String minLtc;
    private String maxLtc;

    private String minUsdt;
    private String maxUsdt;

    private String minUsdc;
    private String maxUsdc;

    private String minDai;
    private String maxDai;
}
