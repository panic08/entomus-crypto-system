package by.panic.entomus.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(name = "CreateMerchantResponse")
public class CreateMerchantResponse {
    private int state;
    private Result result;

    @Getter
    @Setter
    @Builder
    @Schema(name = "CreateMerchantResponseResult")
    public static class Result {
        @JsonProperty(value = "api_key")
        private String apiKey;
    }
}
