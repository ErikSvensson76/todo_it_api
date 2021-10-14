package se.lexicon.todo_it_api.model.forms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TodoItemForm implements Serializable {
    @NotBlank(message = "This field is required")
    @Size(min = 2, max = 50, message = "This field need to have between 2 and 50 characters")
    private String title;
    @NotBlank(message = "This field is required")
    @Size(min = 2, max = 1000, message = "This field need to have between 2 and 1000 characters")
    private String description;
    @NotNull(message = "This field is required")
    @Future(message = "A valid deadline need to a future ISO date")
    private LocalDate deadLine;
    private boolean done;
}
