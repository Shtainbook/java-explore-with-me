package ru.practicum.requests.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.enums.RequestStatus;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "event_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "requester_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User requester;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}