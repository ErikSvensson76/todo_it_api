package se.lexicon.todo_it_api.service;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import se.lexicon.todo_it_api.controller.PersonController;
import se.lexicon.todo_it_api.controller.TodoItemController;
import se.lexicon.todo_it_api.model.dto.PersonDTO;
import se.lexicon.todo_it_api.model.dto.PersonDTOSmall;
import se.lexicon.todo_it_api.model.dto.TodoItemDTO;
import se.lexicon.todo_it_api.model.dto.TodoItemDTOSmall;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class LinkServiceImpl implements LinkService{

    @Override
    public PersonDTO addLinks(PersonDTO personDTO) {
        personDTO = personDTO.add(linkTo(methodOn(PersonController.class).findById(personDTO.getPersonId())).withSelfRel());

        if(personDTO.getTodoItems() != null){
            List<TodoItemDTOSmall> todoItemsWithLinks = new ArrayList<>();
            for(TodoItemDTOSmall dtoSmall : personDTO.getTodoItems()){
                todoItemsWithLinks.add(addLinks(dtoSmall, personDTO.getPersonId()));
            }
            personDTO.setTodoItems(todoItemsWithLinks);
        }
        return personDTO;
    }

    @Override
    public Link[] getLinksForCollectionModelPersonDTO(){
        return new Link[]{
                linkTo(methodOn(PersonController.class).find("all")).withRel("All"),
                linkTo(methodOn(PersonController.class).find("idle")).withRel("Idle people")
        };
    }

    @Override
    public PersonDTOSmall addLinks(PersonDTOSmall personDTOSmall) {
        return personDTOSmall.add(
                linkTo(methodOn(PersonController.class).findById(personDTOSmall.getPersonId())).withSelfRel(),
                linkTo(methodOn(PersonController.class).getTodoItems(personDTOSmall.getPersonId())).withRel("todos")
        );
    }


    @Override
    public TodoItemDTOSmall addLinks(TodoItemDTOSmall todoItemDTOSmall, Integer assigneeId){
        if(todoItemDTOSmall != null){
            todoItemDTOSmall = todoItemDTOSmall.add(linkTo(methodOn(TodoItemController.class).findById(todoItemDTOSmall.getTodoId())).withSelfRel());
            todoItemDTOSmall = todoItemDTOSmall.add(linkTo(methodOn(PersonController.class).removeTodoItem(assigneeId, todoItemDTOSmall.getTodoId())).withRel("Remove assignment"));

        }
        return todoItemDTOSmall;
    }

    @Override
    public TodoItemDTO addLinks(TodoItemDTO todoItemDTO) {
        if(todoItemDTO != null){
            todoItemDTO = todoItemDTO.add(linkTo(methodOn(TodoItemController.class).findById(todoItemDTO.getTodoId())).withSelfRel());
            if(todoItemDTO.getAssignee() != null){
                todoItemDTO = todoItemDTO.add(linkTo(methodOn(PersonController.class).findById(todoItemDTO.getAssignee().getPersonId())).withRel("assignee"));
                todoItemDTO = todoItemDTO.add(linkTo(methodOn(PersonController.class).removeTodoItem(todoItemDTO.getAssignee().getPersonId(), todoItemDTO.getTodoId())).withRel("Remove assignee"));
            }
        }
        return todoItemDTO;
    }

    @Override
    public CollectionModel<TodoItemDTO> addLinks(CollectionModel<TodoItemDTO> todoItemDTOS) {

        todoItemDTOS = todoItemDTOS.add(linkTo(methodOn(TodoItemController.class).find("all", null)).withRel("All TodoItems"));
        todoItemDTOS = todoItemDTOS.add(linkTo(methodOn(TodoItemController.class).find("unassigned", null)).withRel("Unassigned TodoItems"));
        todoItemDTOS = todoItemDTOS.add(linkTo(methodOn(TodoItemController.class).find("done_status", new String[]{"false"})).withRel("Unfinished TodoItems"));
        todoItemDTOS = todoItemDTOS.add(linkTo(methodOn(TodoItemController.class).find("done_status", new String[]{"true"})).withRel("Finished TodoItems"));
        todoItemDTOS = todoItemDTOS.add(linkTo(methodOn(TodoItemController.class).find("late", null)).withRel("All late TodoItems"));


        return todoItemDTOS;
    }
}
