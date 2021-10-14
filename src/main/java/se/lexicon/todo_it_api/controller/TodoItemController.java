package se.lexicon.todo_it_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.lexicon.todo_it_api.model.dto.TodoItemDTO;
import se.lexicon.todo_it_api.model.forms.TodoItemForm;
import se.lexicon.todo_it_api.service.LinkService;
import se.lexicon.todo_it_api.service.TodoItemService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo/api/v1/todos")
@Slf4j
@CrossOrigin("*")
public class TodoItemController {

    private final TodoItemService todoItemService;
    private final LinkService linkService;
    private final List<String> searchTypes = Arrays.asList(
            "all", "unassigned", "done_status", "between", "before", "after", "title", "late"
    );

    @PostMapping
    public ResponseEntity<TodoItemDTO> create(@Valid @RequestBody TodoItemForm form){
        TodoItemDTO todoItemDTO = todoItemService.create(form);
        return ResponseEntity
                .created(linkTo(methodOn(TodoItemController.class).findById(todoItemDTO.getTodoId()))
                        .toUri()).body(linkService.addLinks(todoItemDTO));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<TodoItemDTO>> find(
            @RequestParam(name = "search", defaultValue = "all") String search,
            @RequestParam(name = "values", defaultValue = "all") String[]values)
    {

        List<TodoItemDTO> todoItemDTOS;

        switch (search){
            case "all":
                todoItemDTOS = todoItemService.findAll();
                break;
            case "unassigned":
                todoItemDTOS = todoItemService.findAllUnassigned();
                break;
            case "done_status":
                boolean doneStatus = Boolean.parseBoolean(values[0]);
                todoItemDTOS = todoItemService.findByDoneStatus(doneStatus);
                break;
            case "between":
                List<LocalDate> dateValues = Stream.of(values)
                        .map(LocalDate::parse)
                        .collect(Collectors.toList());

                if(dateValues.size() != 2) throw new IllegalArgumentException("Invalid params: expected 2 params. Actual param(s) were " + dateValues);
                LocalDate start = dateValues.get(0);
                LocalDate end = dateValues.get(1);
                todoItemDTOS = todoItemService.findByDeadlineBetween(start, end);
                break;
            case "before":
                LocalDate before = LocalDate.parse(Objects.requireNonNull(values[0]));
                todoItemDTOS = todoItemService.findByDeadlineBefore(before);
                break;
            case "after":
                LocalDate after = LocalDate.parse(Objects.requireNonNull(values[0]));
                todoItemDTOS = todoItemService.findByDeadlineAfter(after);
                break;
            case "late":
                todoItemDTOS = todoItemService.findAllUnfinishedAndOverdue();
                break;
            case "title":
                String title = values[0];
                todoItemDTOS = todoItemService.findByTitle(title);
                break;
            default:
                throw new IllegalArgumentException("Invalid search type '"+ search+"' valid types are: " + searchTypes);
        }

        todoItemDTOS = todoItemDTOS.stream()
                .map(linkService::addLinks)
                .collect(Collectors.toList());

        return ResponseEntity.ok(linkService.addLinks(CollectionModel.of(todoItemDTOS)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoItemDTO> findById(@PathVariable("id") Integer id){
        TodoItemDTO todoItemDTO = todoItemService.findById(id);
        return ResponseEntity.ok(linkService.addLinks(todoItemDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoItemDTO> update(@PathVariable("id") Integer id, @Valid @RequestBody TodoItemForm form){
        TodoItemDTO todoItemDTO = todoItemService.update(id, form);
        return ResponseEntity.ok(linkService.addLinks(todoItemDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id){
        boolean deleted = todoItemService.delete(id);
        return ResponseEntity.ok(deleted ? "TodoItem with id " + id + " was deleted" : "TodoItem with id " + id + " was not deleted");
    }
}
