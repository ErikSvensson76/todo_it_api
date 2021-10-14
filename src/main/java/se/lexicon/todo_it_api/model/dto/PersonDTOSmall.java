package se.lexicon.todo_it_api.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonDTOSmall extends RepresentationModel<PersonDTOSmall> implements Serializable {
    private Integer personId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
}
