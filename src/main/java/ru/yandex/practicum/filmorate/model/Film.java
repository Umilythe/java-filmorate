package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> filmLikes = new HashSet<>();

    public void addLike(Long userId){
        filmLikes.add(userId);
    }

    public void deleteLike(Long userId){
        filmLikes.remove(userId);
    }

    public int getNumberOfLikes(){
        return filmLikes.size();
    }
}
