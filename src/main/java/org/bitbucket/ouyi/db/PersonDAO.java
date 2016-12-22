package org.bitbucket.ouyi.db;

import org.bitbucket.ouyi.business.Person;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.*;

import java.lang.annotation.*;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Created by worker on 12/21/16.
 */
public class PersonDAO {

    public interface BatchInsert {
        @Transaction
        @SqlBatch("insert into person (id, name, time_of_start) values (:id, :name, :time_of_start) ")
                //"on conflict do update set (name, time_of_start) = (:name, :timeOfStart)")
        void insertAll(@BindPerson Iterator<Person> persons);
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

    private final DBI dbi;

    public PersonDAO(DBI dbi) {
        this.dbi = dbi;
    }

    public void insertAll(Iterator<Person> persons) {
        BatchInsert batchInsert = dbi.onDemand(BatchInsert.class);
        batchInsert.insertAll(persons);
    }
}
