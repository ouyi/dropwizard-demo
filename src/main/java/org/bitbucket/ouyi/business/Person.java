package org.bitbucket.ouyi.business;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Created by worker on 12/21/16.
 */
public class Person {
    private final int id;
    private final String name;
    private final ZonedDateTime timeOfStart;

    public Person(int id, String name, ZonedDateTime timeOfStart) {
        this.id = id;
        this.name = name;
        this.timeOfStart = timeOfStart;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getTimeOfStart() {
        return timeOfStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
