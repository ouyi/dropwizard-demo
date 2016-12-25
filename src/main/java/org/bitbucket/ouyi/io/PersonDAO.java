package org.bitbucket.ouyi.io;

import org.bitbucket.ouyi.business.Person;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.lang.annotation.*;
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

    public interface PersonTable {
        @Transaction
        @SqlBatch("insert into person (id, name, time_of_start) values (:id, :name, :time_of_start) ")
                //"on conflict do update set (name, time_of_start) = (:name, :timeOfStart)")
        void insertAll(@BindPerson Iterator<Person> persons);

        @SqlQuery("select id, name, time_of_start from person")
        @Mapper(PersonMapper.class)
        Iterator<Person> selectAll();
    }

    @BindingAnnotation(BindPerson.PersonBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindPerson {
        class PersonBinderFactory implements BinderFactory {
            public Binder build(Annotation annotation) {
                return new Binder<BindPerson, Person>() {
                    public void bind(SQLStatement q, BindPerson bind, Person arg) {
                        q.bind("id", arg.getId());
                        q.bind("name", arg.getName());
                        q.bind("time_of_start", Timestamp.from(arg.getTimeOfStart().toInstant()));
                    }
                };
            }
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

    private final DBI dbi;

    public PersonDAO(DBI dbi) {
        this.dbi = dbi;
    }

    public void insertAll(Iterator<Person> persons) {
        PersonTable personTable = dbi.onDemand(PersonTable.class);
        personTable.insertAll(persons);
    }
}
