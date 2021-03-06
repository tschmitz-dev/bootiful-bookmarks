package de.tschmitz.rest.bookmarks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * JPA bookmark entity.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** External user id */
    @CreatedBy
    @ReadOnlyProperty
    private String userId;

    private String title;

    private String href;

    private String icon;

    @CreationTimestamp
    private LocalDateTime added;

    @ManyToMany
    @JoinTable(
            name = "bookmarks_tags",
            joinColumns = @JoinColumn(name = "bookmark_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<Tag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bookmark bookmark = (Bookmark) o;
        return id.equals(bookmark.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // getter and setter are generated by lombok
}
