package hyunec.airouter.coredomain

import net.datafaker.Faker
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode

@ActiveProfiles(value = ["test"])
@TestConstructor(autowireMode = AutowireMode.ALL)
abstract class TestDefaultSupport {

    companion object {
        @JvmStatic
        protected val faker = Faker()
    }
}
