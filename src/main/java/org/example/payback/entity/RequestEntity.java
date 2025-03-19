package org.example.payback.entity;


import jakarta.persistence.*;
import org.example.payback.dto.RequestType;

@Entity
@Table(name = "request")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    RequestType type;

    String content;

    public RequestEntity(RequestType type, String content) {
        this.type = type;
        this.content = content;
    }

    public RequestEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
