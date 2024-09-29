package hyunec.airouter.chatdomain

import net.datafaker.Faker
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode

@ActiveProfiles(value = ["test"])
@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest
abstract class TestDefaultSupport {

    companion object {
        @JvmStatic
        protected val faker = Faker()
    }
}
