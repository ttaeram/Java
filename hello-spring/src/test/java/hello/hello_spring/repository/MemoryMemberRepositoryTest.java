package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryMemberRepositoryTest {

    MemoryMemberRepository respository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        respository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");

        respository.save(member);

        Member result = respository.findById(member.getId()).get();
        // Assertions.assertEquals(member, result);
        assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        respository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        respository.save(member2);

        Member result = respository.findByName("spring2").get();

        assertThat(result).isEqualTo(member2);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        respository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        respository.save(member2);

        List<Member> result = respository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }
}
