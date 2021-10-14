package se.lexicon.todo_it_api.service;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import se.lexicon.todo_it_api.model.dto.PersonDTO;
import se.lexicon.todo_it_api.model.dto.PersonDTOSmall;
import se.lexicon.todo_it_api.model.dto.TodoItemDTO;
import se.lexicon.todo_it_api.model.dto.TodoItemDTOSmall;

public interface LinkService {
    PersonDTO addLinks(PersonDTO personDTO);

    Link[] getLinksForCollectionModelPersonDTO();

    PersonDTOSmall addLinks(PersonDTOSmall personDTOSmall);

    TodoItemDTOSmall addLinks(TodoItemDTOSmall todoItemDTOSmall, Integer assigneeId);

    TodoItemDTO addLinks(TodoItemDTO todoItemDTO);

    CollectionModel<TodoItemDTO> addLinks(CollectionModel<TodoItemDTO> todoItemDTOS);
}
