package corbos.fieldagent.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository repo;

    public CountryRepositoryTest() {
    }

    @Test
    public void testFindAll() {
        assertEquals(249, repo.findAll().size());
    }

    @Test
    public void testFindById() {
        assertEquals("Albania", repo.findById("ALB").orElse(null).getName());
        assertEquals("Cuba", repo.findById("CUB").orElse(null).getName());
        assertEquals("Macao", repo.findById("MAC").orElse(null).getName());
    }

}
