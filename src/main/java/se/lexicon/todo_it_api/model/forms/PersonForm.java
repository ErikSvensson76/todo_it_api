package se.lexicon.todo_it_api.model.forms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonForm implements Serializable {
    @NotBlank(message = "This field is required")
    @Size(min = 2, message = "Need to contain at least 2 letters")
    private String firstName;
    @NotBlank(message = "This field is required")
    @Size(min = 2, message = "Need to contain at least 2 letters")
    private String lastName;
    @NotNull(message = "This field is required")
    @PastOrPresent(message = "Need to be in the past or present")
    private LocalDate birthDate;
}
