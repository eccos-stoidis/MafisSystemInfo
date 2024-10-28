package common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Response class to represent API responses.
 * Automatically generates constructors, getters, setters, equals, and hashCode using Lombok.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = -4284837767945306847L;

    private LocalDateTime timestamp;
    private Integer responseCode;
    private String responseText;


}
