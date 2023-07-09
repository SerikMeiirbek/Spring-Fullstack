package com.serikscode;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestcontainersTest extends AbstractTestContainerUnitTest{

    @Test
    void canStartPostgresDB() {
        Assertions.assertThat(postgreSQLContainer.isCreated()).isTrue();
        Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();

    }


}
