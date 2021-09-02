package corbos.fieldagent.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SecurityClearanceRepositoryTest {

    @Autowired
    private SecurityClearanceRepository repo;

    public SecurityClearanceRepositoryTest() {
    }

    @Test
    public void testFindAll() {
        assertEquals(4, repo.findAll().size());
    }

}
