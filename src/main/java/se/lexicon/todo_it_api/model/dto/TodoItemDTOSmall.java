package se.lexicon.todo_it_api.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class TodoItemDTOSmall extends RepresentationModel<TodoItemDTOSmall> implements Serializable {

    private Integer todoId;
    private String title;
    private String description;
    private LocalDate deadLine;
    private boolean done;

}
