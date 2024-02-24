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
    private String minEthUsdt;
    private String minEthUsdc;
    private String minEthDai;

    private String minBtc;

    private String minTrx;
    private String minTrxUsdt;
    private String minTrxUsdc;

    private String minBnb;
    private String minBnbUsdt;
    private String minBnbUsdc;
    private String minBnbDai;

    private String minPolygon;

    private String minEtc;

    private String minAvax;

    private String minBch;

    private String minSol;

    private String minLtc;
}
