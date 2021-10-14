package se.lexicon.todo_it_api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.todo_it_api.data.PersonDAO;
import se.lexicon.todo_it_api.data.TodoItemDAO;
import se.lexicon.todo_it_api.exception.AppResourceNotFoundException;
import se.lexicon.todo_it_api.model.dto.PersonDTO;
import se.lexicon.todo_it_api.model.entity.Person;
import se.lexicon.todo_it_api.model.entity.TodoItem;
import se.lexicon.todo_it_api.model.forms.PersonForm;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService{

    private final PersonDAO personDAO;
    private final TodoItemDAO todoItemDAO;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PersonDTO create(PersonForm form) {
        Person person = personDAO.save(new Person(null, form.getFirstName().trim(), form.getLastName().trim(), form.getBirthDate(), null));
        return modelMapper.map(person, PersonDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDTO findById(Integer id) {
        return personDAO.findById(id)
                .map(person -> modelMapper.map(person, PersonDTO.class))
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find person with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonDTO> findAll() {
        List<Person> people = (List<Person>) personDAO.findAll();
        return people.stream()
                .map(person -> modelMapper.map(person, PersonDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonDTO> findIdlePeople() {
        return personDAO.findIdlePeople().stream()
                .map(person -> modelMapper.map(person, PersonDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PersonDTO update(Integer id, PersonForm form) {
        Person person = personDAO.findById(id)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find person with id " + id));

        person.setFirstName(form.getFirstName().trim());
        person.setLastName(form.getLastName().trim());
        person = personDAO.save(person);
        return modelMapper.map(person, PersonDTO.class);
    }

    @Override
    @Transactional
    public PersonDTO addTodoItem(Integer todoItemId, Integer personId) {
        TodoItem todoItem = todoItemDAO.findById(todoItemId)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find todo item with id " + todoItemId));

        Person person = personDAO.findById(personId)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find person with id " + personId));

        person.addTodoItem(todoItem);
        person = personDAO.save(person);
        return modelMapper.map(person, PersonDTO.class);
    }

    @Override
    @Transactional
    public PersonDTO removeTodoItem(Integer todoItemId, Integer personId) {
        TodoItem todoItem = todoItemDAO.findById(todoItemId)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find todo item with id " + todoItemId));

        Person person = personDAO.findById(personId)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find person with id " + personId));

        person.removeTodoItem(todoItem);
        person = personDAO.save(person);
        return modelMapper.map(person, PersonDTO.class);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        Person person = personDAO.findById(id)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find person with id " + id));
        person.setTodoItems(null);
        personDAO.delete(person);
        return !personDAO.findById(id).isPresent();
    }
}
