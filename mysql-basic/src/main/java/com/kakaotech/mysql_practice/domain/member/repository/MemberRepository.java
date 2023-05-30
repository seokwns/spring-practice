package com.kakaotech.mysql_practice.domain.member.repository;

import com.kakaotech.mysql_practice.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepository {
    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    final static private String TABLE = "member";

    public Optional<Member> findById(Long id) {
        var sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE);
        var params = new MapSqlParameterSource()
                .addValue("id", id);

        /*
            여기서 BeanPropertyRowMapper를 사용하려면 Member에서 Setter를 다 열어줘야 하는데
            그러면 어디서든 멤버의 값을 다 바꿀 수 있어서 규모가 커질수록 사이드 이팩트를 무시할 수 없음
            따라서 Setter를 여는건 진짜진짜 필요할 때만 열어주자
            그래서 번거롭지만 RowMapper를 사용함
         */
        RowMapper<Member> rowMapper = (ResultSet resultSet, int rowNum) -> Member
                .builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .nickname(resultSet.getString("nickname"))
                .birthday(resultSet.getObject("birthday", LocalDate.class))
                .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                .build();

        var member = namedParameterJdbcTemplate.queryForObject(sql, params, rowMapper);
        return Optional.ofNullable(member);
    }

    public Member save(Member member) {
        if (member.getId() == null) {
            return insert(member);
        }
        return update(member);
    }

    private Member insert(Member member) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("Member")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return Member
                .builder()
                .id(id)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .createdAt(member.getCreatedAt())
                .build();
    }

    private Member update(Member member) {
        // TODO: implemented
        return member;
    }
}
