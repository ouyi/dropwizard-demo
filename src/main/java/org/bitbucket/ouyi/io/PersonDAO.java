package org.bitbucket.ouyi.io;

import com.codahale.metrics.annotation.Timed;
import org.bitbucket.ouyi.business.Person;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;

/**
 * Created by worker on 12/21/16.
 */
public class PersonDAO {

    private final DBI dbi;

    public PersonDAO(DBI dbi) {
        this.dbi = dbi;
    }

    @Timed
    @Transaction
    public void insertAll(Iterator<Person> persons) {
        // This is done in two statements due to h2 database limitations.
        // With a more advanced db engine, it can be done like this:
        // "insert into person (id, name, time_of_start) values (:id, :name, :time_of_start)"
        // "on conflict (id) do update set (name, time_of_start) = (:name, :timeOfStart)"
        try (Handle handle = dbi.open()) {
            persons.forEachRemaining(person -> {
                handle.createStatement("delete from person where id = :id")
                        .bind("id", person.getId())
                        .execute();
                handle.createStatement("insert into person (id, name, time_of_start) values (:id, :name, :time_of_start)")
                        .bind("id", person.getId())
                        .bind("name", person.getName())
                        .bind("time_of_start", Timestamp.from(person.getTimeOfStart().toInstant()))
                        .execute();
            });
        }
    }

    @Timed
    public Iterator<Person> selectAll() {
        try (Handle handle = dbi.open()) {
            return handle.createQuery("select id, name, time_of_start from person")
                    .map(new PersonMapper())
                    .iterator();
        }
    }

    public static class PersonMapper implements ResultSetMapper<Person> {
        @Override
        public Person map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new Person(
                    r.getInt("id"),
                    r.getString("name"),
                    ZonedDateTime.ofInstant(r.getTimestamp("time_of_start").toInstant(), ZoneId.of("UTC")));
        }
    }
}
