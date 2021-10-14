package se.lexicon.todo_it_api.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"assignee"})
@ToString(exclude = "assignee")
@Entity
@Table(name = "todo_item")
public class TodoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Integer todoId;
    @Column(name = "title", length = 50)
    private String title;
    @Column(name = "description", length = 1000)
    private String description;
    @Column(name = "deadline")
    private LocalDate deadLine;
    @Column(name = "done")
    private boolean done;
    @ManyToOne(
            cascade = {CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "assignee_id")
    private Person assignee;
}
