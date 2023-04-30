package com.sivalabs.myapp.users.domain;

import static com.sivalabs.bookmarks.jooq.Tables.USERS;
import static org.jooq.Records.mapping;

import com.sivalabs.bookmarks.jooq.tables.records.UsersRecord;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
class UserRepository {
    private final DSLContext dsl;

    UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<User> findAll() {
        return this.dsl.selectFrom(USERS).fetch(mapping(User::new));
    }

    public User save(User user) {
        UsersRecord usersRecord = this.dsl
                .insertInto(USERS)
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.NAME, user.getName())
                .returning(USERS.ID)
                .fetchSingle();

        return findById(usersRecord.getId()).orElseThrow();
    }

    public Optional<User> findById(Long id) {
        User user = this.dsl.selectFrom(USERS).where(USERS.ID.eq(id)).fetchOne(mapping(User::new));
        return Optional.ofNullable(user);
    }
}
