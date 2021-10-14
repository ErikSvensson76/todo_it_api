package se.lexicon.todo_it_api.service;

import se.lexicon.todo_it_api.model.dto.PersonDTO;
import se.lexicon.todo_it_api.model.forms.PersonForm;

import java.util.List;

public interface PersonService {

    PersonDTO create(PersonForm form);
    PersonDTO findById(Integer id);
    List<PersonDTO> findAll();
    List<PersonDTO> findIdlePeople();
    PersonDTO update(Integer id, PersonForm form);
    PersonDTO addTodoItem(Integer todoItemId, Integer personId);
    PersonDTO removeTodoItem(Integer todoItemId, Integer personId);
    boolean delete(Integer id);

}
