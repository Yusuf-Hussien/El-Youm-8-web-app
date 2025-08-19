package phi.elyoum8.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Generic API response wrapper containing success status, data payload, and error message if it exists")
public class ApiResponseWrapper<T>{

    @Schema(description = "Indicates whether the API request was successful", example = "true")
    private boolean success;

    @Schema(description = "The data payload of the response, null if the request failed")
    private T data;

    @Schema(description = "Error message if the request failed, null if successful", example = "Resource not found")
    private String message;

    public static <T> ApiResponseWrapper<T> success(T data)
    {
        return new ApiResponseWrapper<>(true,data,null);
    }

    public static <T> ApiResponseWrapper<T> error(String message)
    {
        return new ApiResponseWrapper<>(false,null,message);
    }
}
