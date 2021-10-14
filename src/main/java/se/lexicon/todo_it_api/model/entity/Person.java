package se.lexicon.todo_it_api.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"todoItems"})
@ToString(exclude = "todoItems")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Integer personId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private LocalDate birthDate;
    @OneToMany(
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.LAZY,
            mappedBy = "assignee"
    )
    private List<TodoItem> todoItems;

    public void setTodoItems(List<TodoItem> todoItems){
        if(todoItems == null) todoItems = new ArrayList<>();
        if(todoItems.isEmpty()){
            if(this.todoItems != null){
                for(TodoItem todoItem : this.todoItems){
                    todoItem.setAssignee(null);
                }
            }
        }else {
            for(TodoItem todoItem : todoItems){
                todoItem.setAssignee(this);
            }
        }
        this.todoItems = todoItems;
    }

    public List<TodoItem> getTodoItems(){
        if(todoItems == null) todoItems = new ArrayList<>();
        return todoItems;
    }

    public void addTodoItem(TodoItem...todoItems){
        if(todoItems == null || todoItems.length == 0) return;
        if(this.todoItems == null) this.todoItems = new ArrayList<>();
        for(TodoItem todoItem : todoItems){
            if(!this.todoItems.contains(todoItem)){
                this.todoItems.add(todoItem);
                todoItem.setAssignee(this);
            }
        }
    }

    public void removeTodoItem(TodoItem...todoItems){
        if(todoItems == null || todoItems.length == 0) return;
        if(this.todoItems == null) this.todoItems = new ArrayList<>();
        for(TodoItem todoItem : todoItems){
            if(this.todoItems.remove(todoItem)){
                todoItem.setAssignee(null);
            }
        }
    }
}
