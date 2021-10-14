package se.lexicon.todo_it_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lexicon.todo_it_api.model.dto.PersonDTO;
import se.lexicon.todo_it_api.model.dto.TodoItemDTO;
import se.lexicon.todo_it_api.model.forms.PersonForm;
import se.lexicon.todo_it_api.service.LinkService;
import se.lexicon.todo_it_api.service.PersonService;
import se.lexicon.todo_it_api.service.TodoItemService;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo/api/v1/people")
@CrossOrigin("*")
public class PersonController {

    private final PersonService personService;
    private final TodoItemService todoItemService;
    private final LinkService linkService;


    @PostMapping
    public ResponseEntity<PersonDTO> create(@Valid @RequestBody PersonForm form){
        PersonDTO personDTO = personService.create(form);
        return ResponseEntity
                .created(
                        linkTo(methodOn(PersonController.class).findById(personDTO.getPersonId())).toUri()
                )
                .body(linkService.addLinks(personDTO));
    }

    @GetMapping
    public ResponseEntity<?> find(@RequestParam(name = "search", defaultValue = "all") String search){
        switch (search){
            case "idle":
                return findIdlePeople();
            case "all":
                return findAll();
            default:
                throw new IllegalArgumentException("Invalid search param: valid params are 'all' and 'idle'");
        }

    }

    public ResponseEntity<CollectionModel<PersonDTO>> findAll(){
        List<PersonDTO> personDTOS = personService.findAll().stream()
                .map(linkService::addLinks)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(personDTOS, linkService.getLinksForCollectionModelPersonDTO()));
    }

    public ResponseEntity<CollectionModel<PersonDTO>> findIdlePeople(){
        List<PersonDTO> personDTOS = personService.findIdlePeople().stream()
                .map(linkService::addLinks)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(personDTOS, linkService.getLinksForCollectionModelPersonDTO()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> findById(@PathVariable("id") Integer id){
        PersonDTO personDTO = personService.findById(id);
        return ResponseEntity.ok(linkService.addLinks(personDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> update(@PathVariable("id") Integer id, @Valid @RequestBody PersonForm form){
        PersonDTO personDTO = personService.update(id, form);
        return ResponseEntity.ok(linkService.addLinks(personDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePerson(@PathVariable("id") Integer id){
        boolean deleted = personService.delete(id);
        return ResponseEntity.ok()
                .body(deleted ? "Person with id " + id + " was deleted" : "Person with id " + id + " was not deleted");
    }

    @GetMapping("/{id}/todos")
    public ResponseEntity<CollectionModel<TodoItemDTO>> getTodoItems(@PathVariable("id") Integer personId){
        return ResponseEntity.ok(linkService.addLinks(CollectionModel.of(todoItemService.findAllByPersonId(personId))));
    }

    @PutMapping("/{id}/todos/add")
    public ResponseEntity<PersonDTO> assignTodoItem(@PathVariable("id")Integer personId, @RequestParam(name = "todoId") Integer todoId){
        PersonDTO personDTO = personService.addTodoItem(todoId, personId);
        return ResponseEntity.ok(linkService.addLinks(personDTO));
    }

    @PutMapping("/{id}/todos/remove")
    public ResponseEntity<PersonDTO> removeTodoItem(@PathVariable("id") Integer personId, @RequestParam(name = "todoId") Integer todoId){
        PersonDTO personDTO = personService.removeTodoItem(todoId, personId);
        return ResponseEntity.ok(linkService.addLinks(personDTO));
    }



}
